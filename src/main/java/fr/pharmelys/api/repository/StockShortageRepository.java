package fr.pharmelys.api.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import fr.pharmelys.api.entity.StockShortage;
import fr.pharmelys.api.entity.StockStatus;

public interface StockShortageRepository extends JpaRepository<StockShortage, Long> {
    Optional<StockShortage> findByMedication_CisCode(String cisCode);

    long countByStatus(StockStatus status);
}