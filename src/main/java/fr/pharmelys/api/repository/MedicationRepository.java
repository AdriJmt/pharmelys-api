package fr.pharmelys.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.pharmelys.api.entity.Medication;

public interface MedicationRepository extends JpaRepository<Medication, String> {

    @Query(value = """
            SELECT * FROM medication m
            WHERE unaccent(m.name) ILIKE unaccent(CONCAT('%', :term, '%'))
            ORDER BY (unaccent(m.name) ILIKE unaccent(CONCAT(:term, '%'))) DESC, m.name ASC
            """, countQuery = """
            SELECT count(*) FROM medication m
            WHERE unaccent(m.name) ILIKE unaccent(CONCAT('%', :term, '%'))
            """, nativeQuery = true)
    Page<Medication> searchByNameRelevance(@Param("term") String term, Pageable pageable);
}