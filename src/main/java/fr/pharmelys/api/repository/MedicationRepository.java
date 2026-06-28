package fr.pharmelys.api.repository;

import fr.pharmelys.api.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationRepository extends JpaRepository<Medication, String> {
}