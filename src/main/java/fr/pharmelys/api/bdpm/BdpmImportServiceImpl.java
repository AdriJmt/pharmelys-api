package fr.pharmelys.api.bdpm;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.pharmelys.api.entity.GenericGroup;
import fr.pharmelys.api.entity.GenericGroupMember;
import fr.pharmelys.api.entity.Medication;
import fr.pharmelys.api.entity.MedicationSubstance;
import fr.pharmelys.api.entity.StockShortage;
import fr.pharmelys.api.entity.StockStatus;
import fr.pharmelys.api.entity.Substance;
import fr.pharmelys.api.repository.GenericGroupMemberRepository;
import fr.pharmelys.api.repository.GenericGroupRepository;
import fr.pharmelys.api.repository.MedicationRepository;
import fr.pharmelys.api.repository.MedicationSubstanceRepository;
import fr.pharmelys.api.repository.StockShortageRepository;
import fr.pharmelys.api.repository.SubstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BdpmImportServiceImpl implements BdpmImportService {

    private final BdpmFileClient fileClient;
    private final BdpmProperties properties;
    private final MedicationRepository medicationRepository;
    private final SubstanceRepository substanceRepository;
    private final MedicationSubstanceRepository medicationSubstanceRepository;
    private final GenericGroupRepository genericGroupRepository;
    private final GenericGroupMemberRepository genericGroupMemberRepository;
    private final StockShortageRepository stockShortageRepository;
    private final ImportStatusHolder importStatusHolder;
    // caches valides pour la duree d'un import, evitent les allers-retours DB en
    // boucle
    private final Map<String, Medication> medicationCache = new HashMap<>();
    private final Map<String, Substance> substanceCache = new HashMap<>();
    private final Map<String, GenericGroup> genericGroupCache = new HashMap<>();

    @Override
    public void importAll() {
        log.info("=== Debut import BDPM ===");
        runSafely("medications", this::importMedications);
        runSafely("compositions", this::importCompositions);
        runSafely("groupes generiques", this::importGenericGroups);
        runSafely("ruptures de stock", this::importShortages);
        importStatusHolder.markImportCompleted();
        log.info("=== Fin import BDPM ===");
    }

    private void runSafely(String label, Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            log.error("Import '{}' echoue, donnees precedentes conservees : {}", label, e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------
    // MEDICATIONS
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public void importMedications() {
        List<String> lines = downloadOrThrow(properties.getBaseUrl() + properties.getSpecialitesFile(), "CIS_bdpm.txt");

        int imported = 0;
        for (String line : lines) {
            Medication medication = parseMedicationRow(line);
            if (medication == null)
                continue;
            medicationRepository.save(medication);
            imported++;
        }
        log.info("Medications importees : {} / {} lignes lues", imported, lines.size());
    }

    private Medication parseMedicationRow(String line) {
        String[] c = BdpmLineParser.splitChecked(line, 11, "CIS_bdpm");
        if (c == null) {
            return null;
        }

        Medication m = medicationRepository.findById(c[0]).orElseGet(Medication::new);
        m.setCisCode(c[0]);
        m.setName(c[1]);
        m.setPharmaceuticalForm(c[2]);
        m.setAdministrationRoute(c[3]);
        m.setAuthorizationStatus(c[4]);
        m.setAuthorizationProcedure(c[5]);
        m.setMarketingStatus(c[6]);
        m.setAuthorizationDate(BdpmLineParser.parseFrenchDate(c[7]));
        m.setHolders(c.length > 10 ? c[10] : null);
        m.setEnhancedSurveillance(c.length > 11 && BdpmLineParser.parseOuiNon(c[11]));
        return m;
    }

    // ------------------------------------------------------------------
    // COMPOSITIONS
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public void importCompositions() {
        List<String> lines = downloadOrThrow(properties.getBaseUrl() + properties.getCompositionsFile(),
                "CIS_COMPO_bdpm.txt");

        // le fichier source est integralement remplace a chaque import : pas de cle
        // naturelle
        // fiable ligne a ligne, donc on vide puis on recharge plutot que d'essayer de
        // diffuser.
        medicationSubstanceRepository.deleteAll();
        medicationCache.clear();
        substanceCache.clear();

        int imported = 0;
        for (String line : lines) {
            MedicationSubstance row = parseCompositionRow(line);
            if (row == null)
                continue;
            medicationSubstanceRepository.save(row);
            imported++;
        }
        log.info("Compositions importees : {} / {} lignes lues", imported, lines.size());
    }

    private MedicationSubstance parseCompositionRow(String line) {
        String[] c = BdpmLineParser.splitChecked(line, 7, "CIS_COMPO_bdpm");
        if (c == null) {
            return null;
        }

        Medication medication = medicationCache.computeIfAbsent(c[0],
                code -> medicationRepository.findById(code).orElse(null));
        if (medication == null) {
            log.warn("[CIS_COMPO_bdpm] Medicament {} inconnu, composition ignoree", c[0]);
            return null;
        }

        String substanceCode = c[2];
        Substance substance = substanceCache.computeIfAbsent(substanceCode, code -> {
            Substance s = substanceRepository.findById(code).orElseGet(Substance::new);
            s.setSubstanceCode(code);
            s.setName(c[3]);
            return substanceRepository.save(s);
        });

        MedicationSubstance ms = new MedicationSubstance();
        ms.setMedication(medication);
        ms.setSubstance(substance);
        ms.setPharmaceuticalForm(c[1]);
        ms.setDosage(c[4]);
        ms.setDosageReference(c[5]);
        ms.setComponentNature(c[6]);
        return ms;
    }

    // ------------------------------------------------------------------
    // GROUPES GENERIQUES
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public void importGenericGroups() {
        List<String> lines = downloadOrThrow(properties.getBaseUrl() + properties.getGroupesGeneriquesFile(),
                "CIS_GENER_bdpm.txt");

        genericGroupMemberRepository.deleteAllInBatch();
        genericGroupRepository.deleteAllInBatch();
        genericGroupCache.clear();
        medicationCache.clear();

        int imported = 0;
        for (String line : lines) {
            GenericGroupMember member = parseGenericGroupRow(line);
            if (member == null)
                continue;
            genericGroupMemberRepository.save(member);
            imported++;
        }
        log.info("Membres de groupes generiques importes : {} / {} lignes lues", imported, lines.size());
    }

    private GenericGroupMember parseGenericGroupRow(String line) {
        String[] c = BdpmLineParser.splitChecked(line, 4, "CIS_GENER_bdpm");
        if (c == null) {
            return null;
        }

        Medication medication = medicationCache.computeIfAbsent(c[2],
                code -> medicationRepository.findById(code).orElse(null));
        if (medication == null) {
            log.warn("[CIS_GENER_bdpm] Medicament {} inconnu, membre de groupe ignore", c[2]);
            return null;
        }

        GenericGroup group = genericGroupCache.computeIfAbsent(c[0], id -> {
            GenericGroup g = genericGroupRepository.findById(id).orElseGet(GenericGroup::new);
            g.setGroupId(id);
            g.setLabel(c[1]);
            return genericGroupRepository.save(g);
        });
        if (group == null) {
            log.warn("[CIS_GENER_bdpm] Groupe {} introuvable apres creation, membre ignore", c[0]);
            return null;
        }

        GenericGroupMember member = new GenericGroupMember();
        member.setGroup(group);
        member.setMedication(medication);
        member.setGenericType(parseGenericTypeOrNull(c[3]));
        return member;
    }

    private Integer parseGenericTypeOrNull(String raw) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ------------------------------------------------------------------
    // RUPTURES DE STOCK
    // ------------------------------------------------------------------

    /**
     * IMPORTANT : format non confirme publiquement (cf cahier des charges).
     * Hypothese de colonnes
     * (0=CIS, 1=denomination, 2=statut) en mode best-effort. Logge un apercu des 5
     * premieres lignes
     * pour ajustement une fois le fichier reel inspecte.
     */
    @Override
    @Transactional
    public void importShortages() {
        List<String> lines = downloadOrThrow(
                properties.getBaseUrl() + properties.getRupturesFile(), "fichier ruptures");
        logPreview(lines);

        int imported = 0;
        for (String line : lines) {
            StockShortage shortage = parseShortageRow(line);
            if (shortage == null)
                continue;
            stockShortageRepository.save(shortage);
            imported++;
        }
        log.info("Ruptures importees/mises a jour : {} / {} lignes lues", imported, lines.size());
    }

    private StockShortage parseShortageRow(String line) {
        String[] c = line.split("\t", -1);
        if (c.length < 3) {
            log.warn("[ruptures] Ligne ignoree, format inattendu : {}", line);
            return null;
        }

        String cisCode = c[0].trim();
        Medication medication = medicationRepository.findById(cisCode).orElse(null);
        if (medication == null) {
            log.warn("[ruptures] Medicament {} inconnu, ligne ignoree", cisCode);
            return null;
        }

        StockShortage shortage = stockShortageRepository.findByMedication_CisCode(cisCode)
                .orElseGet(StockShortage::new);
        shortage.setMedication(medication);
        shortage.setRawName(medication.getName());
        shortage.setStatus(mapStatus(c[2]));
        shortage.setReportDate(BdpmLineParser.parseFrenchDate(c[3]));
        shortage.setRestockDate(BdpmLineParser.parseFrenchDate(c[4]));

        return shortage;
    }

    private void logPreview(List<String> lines) {
        if (!log.isInfoEnabled()) {
            return;
        }

        for (int i = 0; i < Math.min(5, lines.size()); i++) {
            String line = lines.get(i);
            String[] preview = line.split("\t", -1);

            log.info("[ruptures][preview ligne {}] {} colonnes -> {}", i, preview.length, line);
        }
    }

    private StockStatus mapStatus(String raw) {
        if (raw == null) {
            return StockStatus.OTHER;
        }
        String v = raw.trim().toLowerCase();
        if (v.contains("rupture"))
            return StockStatus.SHORTAGE;
        if (v.contains("tension"))
            return StockStatus.TENSION;
        if (v.contains("disposition") || v.contains("disponible"))
            return StockStatus.RESTOCKED;
        if (v.contains("arret"))
            return StockStatus.DISCONTINUED;
        return StockStatus.OTHER;
    }

    // ------------------------------------------------------------------
    // COMMUN
    // ------------------------------------------------------------------

    private List<String> downloadOrThrow(String url, String label) {
        try {
            return fileClient.downloadLines(url);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Telechargement " + label + " interrompu", e);
        } catch (IOException e) {
            throw new RuntimeException("Telechargement " + label + " impossible", e);
        }
    }
}