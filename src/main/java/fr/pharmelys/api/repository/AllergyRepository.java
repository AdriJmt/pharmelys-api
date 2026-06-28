package fr.pharmelys.api.repository;

import fr.pharmelys.api.entity.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AllergyRepository extends JpaRepository<Allergy, Long> {
    List<Allergy> findByPatientProfile_Id(UUID profileId);
}