package fr.pharmelys.api.repository;

import fr.pharmelys.api.entity.GenericGroupMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenericGroupMemberRepository extends JpaRepository<GenericGroupMember, Long> {
    List<GenericGroupMember> findByMedication_CisCode(String cisCode);

    List<GenericGroupMember> findByGroup_GroupIdInAndMedication_CisCodeNot(List<String> groupIds, String cisCode);
}