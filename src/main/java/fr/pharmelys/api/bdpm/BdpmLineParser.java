package fr.pharmelys.api.bdpm;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class BdpmLineParser {

    private BdpmLineParser() {
    }

    /**
     * Retourne null si la ligne est invalide (loggee en WARN), jamais d'exception.
     */
    public static String[] splitChecked(String line, int expectedMinColumns, String fileLabel) {
        String[] parts = line.split("\t", -1);
        if (parts.length < expectedMinColumns) {
            log.warn("[{}] Ligne ignoree, {} colonnes au lieu de {} minimum attendues : {}",
                    fileLabel, parts.length, expectedMinColumns, line);
            return new String[] {};
        }
        return parts;
    }

    public static LocalDate parseFrenchDate(String raw) {
        if (raw == null || raw.isBlank())
            return null;
        try {
            return LocalDate.parse(raw.trim(),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            log.warn("Date illisible ignoree : '{}'", raw);
            return null;
        }
    }

    public static boolean parseOuiNon(String raw) {
        return raw != null && raw.trim().equalsIgnoreCase("oui");
    }
}