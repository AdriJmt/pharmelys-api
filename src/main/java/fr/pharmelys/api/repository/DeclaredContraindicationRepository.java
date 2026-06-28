package fr.pharmelys.api.repository;

import fr.pharmelys.api.entity.DeclaredContraindication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DeclaredContraindicationRepository extends JpaRepository<DeclaredContraindication, Long> {
    // Champ d'entité = "patient" (cf DeclaredContraindication.patient), pas
    // "patientProfile"
    List<DeclaredContraindication> findByPatient_Id(UUID profileId);
}