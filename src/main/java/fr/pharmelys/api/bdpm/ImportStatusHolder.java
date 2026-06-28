package fr.pharmelys.api.bdpm;

import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Conserve l'horodatage du dernier import BDPM reussi, consulte par l'endpoint
 * /api/about.
 */
@Component
public class ImportStatusHolder {

    private final AtomicReference<Instant> lastSuccessfulImport = new AtomicReference<>();

    public void markImportCompleted() {
        lastSuccessfulImport.set(Instant.now());
    }

    public Instant getLastSuccessfulImport() {
        return lastSuccessfulImport.get();
    }
}