package fr.pharmelys.api.repository;

import fr.pharmelys.api.entity.GenericGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenericGroupMemberRepository extends JpaRepository<GenericGroupMember, Long> {
}