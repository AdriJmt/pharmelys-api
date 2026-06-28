package fr.pharmelys.api.repository;

import fr.pharmelys.api.entity.AlertSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AlertSubscriptionRepository extends JpaRepository<AlertSubscription, Long> {
    Optional<AlertSubscription> findByEmailIgnoreCaseAndMedication_CisCodeAndActiveTrue(String email, String cisCode);

    List<AlertSubscription> findByActiveTrue();
}