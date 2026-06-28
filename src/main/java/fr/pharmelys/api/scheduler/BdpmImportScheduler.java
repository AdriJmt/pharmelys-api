package fr.pharmelys.api.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.pharmelys.api.bdpm.BdpmImportService;
import fr.pharmelys.api.bdpm.BdpmProperties;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BdpmImportScheduler {

    private static final Logger log = LoggerFactory.getLogger(BdpmImportScheduler.class);

    private final BdpmImportService importService;
    private final BdpmProperties properties;

    /**
     * @Async pour ne pas bloquer le demarrage de l'API : les endpoints repondent
     *        immediatement (avec les donnees H2 deja presentes sur disque s'il y en
     *        a),
     *        l'import tourne en arriere-plan.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void onStartup() {
        if (!properties.isImportOnStartup()) {
            log.info("Import au demarrage desactive (pharmelys.bdpm.import-on-startup=false)");
            return;
        }
        log.info("Lancement de l'import BDPM au demarrage...");
        importService.importAll();
    }

    @Scheduled(cron = "${pharmelys.bdpm.import-cron:0 0 5 * * *}")
    public void onSchedule() {
        log.info("Lancement de l'import BDPM planifie...");
        importService.importAll();
    }
}