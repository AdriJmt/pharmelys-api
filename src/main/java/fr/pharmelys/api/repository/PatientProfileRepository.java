package fr.pharmelys.api.repository;

import fr.pharmelys.api.entity.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PatientProfileRepository extends JpaRepository<PatientProfile, UUID> {
}