package fr.pharmelys.api.dto.medication;

import java.time.LocalDate;

/**
 * Détail d'une rupture/tension d'approvisionnement en cours.
 *
 * @param status      statut courant ({@code SHORTAGE} ou {@code TENSION})
 * @param reportDate  date de signalement de la rupture
 * @param restockDate date de remise à disposition prévue, {@code null} si
 *                    inconnue
 */
public record ShortageInfoDTO(String status, LocalDate reportDate, LocalDate restockDate) {
}
