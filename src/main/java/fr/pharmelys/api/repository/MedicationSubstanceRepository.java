package fr.pharmelys.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.pharmelys.api.entity.MedicationSubstance;

public interface MedicationSubstanceRepository extends JpaRepository<MedicationSubstance, Long> {
    void deleteByMedication_CisCode(String cisCode);

    List<MedicationSubstance> findByMedication_CisCodeAndComponentNature(String cisCode, String componentNature);
}