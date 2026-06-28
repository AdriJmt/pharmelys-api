package fr.pharmelys.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenericGroup {

    @Id
    @Column(length = 50)
    private String groupId;

    /**
     * Nom lisible du groupe générique
     */
    @Column(nullable = false, length = 255)
    private String label;
}