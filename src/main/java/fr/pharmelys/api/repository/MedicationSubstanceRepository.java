package fr.pharmelys.api.repository;

import fr.pharmelys.api.entity.MedicationSubstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationSubstanceRepository extends JpaRepository<MedicationSubstance, Long> {
    void deleteByMedication_CisCode(String cisCode);
}