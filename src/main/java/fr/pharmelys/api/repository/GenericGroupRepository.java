package fr.pharmelys.api.repository;

import fr.pharmelys.api.entity.GenericGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenericGroupRepository extends JpaRepository<GenericGroup, String> {
}
