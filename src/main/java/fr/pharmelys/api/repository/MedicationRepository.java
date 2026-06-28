package fr.pharmelys.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.pharmelys.api.entity.Medication;

public interface MedicationRepository extends JpaRepository<Medication, String> {
    @Query(value = """
            SELECT * FROM medication m
            WHERE unaccent(m.name) ILIKE unaccent(CONCAT('%', :term, '%'))
            ORDER BY (unaccent(m.name) ILIKE unaccent(CONCAT(:term, '%'))) DESC, m.name ASC
            LIMIT 20
            """, nativeQuery = true)
    List<Medication> searchByNameRelevance(@Param("term") String term);
}