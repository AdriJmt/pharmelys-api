package fr.pharmelys.api.service.impl;

import fr.pharmelys.api.bdpm.ImportStatusHolder;
import fr.pharmelys.api.dto.about.AboutResponseDTO;
import fr.pharmelys.api.service.AboutService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AboutServiceImpl implements AboutService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of("Europe/Paris"));

    private final ImportStatusHolder importStatusHolder;

    @Override
    public AboutResponseDTO getAbout() {
        var lastImport = importStatusHolder.getLastSuccessfulImport();
        String lastImportLabel = lastImport != null ? FORMATTER.format(lastImport) : "non disponible";

        return new AboutResponseDTO(
                "Donnees issues de la Base de Donnees Publique des Medicaments (BDPM) - "
                        + "ANSM, HAS, UNCAM. Source : https://base-donnees-publique.medicaments.gouv.fr",
                "Derniere mise a jour des donnees importees : " + lastImportLabel,
                "Pharmelys est un outil d'information et ne constitue pas un dispositif medical. "
                        + "Il ne remplace pas l'avis d'un medecin ou d'un pharmacien.",
                "https://base-donnees-publique.medicaments.gouv.fr/docs/telechargement/licence_bdpm.pdf");
    }
}
