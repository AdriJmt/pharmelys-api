package fr.pharmelys.api.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.pharmelys.api.dto.alert.AlertDTO;
import fr.pharmelys.api.dto.alert.CreateAlertRequestDTO;
import fr.pharmelys.api.entity.AlertSubscription;
import fr.pharmelys.api.entity.Medication;
import fr.pharmelys.api.exception.alert.AlertNotFoundException;
import fr.pharmelys.api.exception.medication.MedicationNotFoundException;
import fr.pharmelys.api.mapper.AlertMapper;
import fr.pharmelys.api.repository.AlertSubscriptionRepository;
import fr.pharmelys.api.repository.MedicationRepository;
import fr.pharmelys.api.service.AlertService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertSubscriptionRepository alertSubscriptionRepository;
    private final MedicationRepository medicationRepository;

    private final AlertMapper alertMapper;

    @Override
    @Transactional
    public AlertDTO createAlert(CreateAlertRequestDTO request) {
        Medication medication = medicationRepository.findById(request.cisCode())
                .orElseThrow(() -> new MedicationNotFoundException(request.cisCode()));

        AlertSubscription existing = alertSubscriptionRepository
                .findByEmailIgnoreCaseAndMedication_CisCodeAndActiveTrue(request.email(), request.cisCode())
                .orElse(null);
        if (existing != null) {
            return alertMapper.toDto(existing);
        }

        AlertSubscription alert = new AlertSubscription();
        alert.setEmail(request.email());
        alert.setMedication(medication);
        alert.setActive(true);
        return alertMapper.toDto(alertSubscriptionRepository.save(alert));
    }

    @Override
    @Transactional
    public void deactivateAlert(Long alertId) {
        AlertSubscription alert = alertSubscriptionRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));
        alert.setActive(false);
        alertSubscriptionRepository.save(alert);
    }

}