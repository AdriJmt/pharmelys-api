package fr.pharmelys.api.repository;

import fr.pharmelys.api.entity.Substance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubstanceRepository extends JpaRepository<Substance, String> {
}