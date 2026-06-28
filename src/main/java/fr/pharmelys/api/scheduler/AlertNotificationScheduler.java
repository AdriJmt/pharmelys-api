package fr.pharmelys.api.scheduler;

import fr.pharmelys.api.entity.AlertSubscription;
import fr.pharmelys.api.repository.AlertSubscriptionRepository;
import fr.pharmelys.api.service.MedicationService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Notifie par email les abonnés a une alerte de disponibilite quand le
 * medicament suivi
 * n'est plus en rupture/tension active. Se declenche apres chaque import BDPM
 * (les ruptures
 * sont mises a jour a ce moment-la), via un cron de secours independant en cas
 * d'echec d'import.
 */
@Component
@RequiredArgsConstructor
public class AlertNotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(AlertNotificationScheduler.class);

    private final AlertSubscriptionRepository alertSubscriptionRepository;
    private final MedicationService medicationService;
    private final JavaMailSender mailSender;

    @Value("${pharmelys.alerts.from-address}")
    private String fromAddress;

    /**
     * Cron de secours, independant du rythme de l'import (toutes les heures par
     * defaut).
     */
    @Scheduled(cron = "${pharmelys.alerts.notification-cron:0 0 * * * *}")
    @Transactional
    public void checkAndNotify() {
        List<AlertSubscription> activeAlerts = alertSubscriptionRepository.findByActiveTrue();
        int notified = 0;

        for (AlertSubscription alert : activeAlerts) {
            String cisCode = alert.getMedication().getCisCode();
            if (medicationService.hasActiveShortage(cisCode)) {
                continue; // toujours en rupture, rien a faire
            }

            sendRestockEmail(alert);
            alert.setLastNotifiedAt(Instant.now());
            alert.setActive(false); // desactivation pour ne pas spammer, conforme au cahier des charges
            alertSubscriptionRepository.save(alert);
            notified++;
        }

        if (notified > 0) {
            log.info("Alertes de remise a disposition envoyees : {}", notified);
        }
    }

    private void sendRestockEmail(AlertSubscription alert) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(alert.getEmail());
        message.setSubject("Pharmelys - Medicament a nouveau disponible");
        message.setText("Le medicament " + alert.getMedication().getName()
                + " (CIS " + alert.getMedication().getCisCode() + ") n'est plus signale en rupture ou tension. "
                + "Cette information ne remplace pas l'avis de votre pharmacien sur la disponibilite reelle en officine.");
        try {
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Echec envoi email alerte pour {} : {}", alert.getId(), e.getMessage());
        }
    }
}
