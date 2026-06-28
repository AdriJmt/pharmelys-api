package fr.pharmelys.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(indexes = {
        @Index(name = "idx_ggm_group", columnList = "group_id"),
        @Index(name = "idx_ggm_medication", columnList = "medication_cis_code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenericGroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    private GenericGroup group;

    @ManyToOne(optional = false)
    @JoinColumn(name = "medication_cis_code")
    private Medication medication;

    private Integer genericType;
}