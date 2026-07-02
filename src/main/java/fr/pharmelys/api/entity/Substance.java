package fr.pharmelys.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_substance_code", columnNames = "substanceCode")
}, indexes = {
        @Index(name = "idx_substance_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Substance {

    @Id
    @Column(length = 50)
    private String substanceCode;

    @Column(nullable = false, length = 255)
    private String name;
}
