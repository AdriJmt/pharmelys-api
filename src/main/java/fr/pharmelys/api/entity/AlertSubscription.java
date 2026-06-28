package fr.pharmelys.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(indexes = {
        @Index(name = "idx_alert_email_medication", columnList = "email, medication_cis_code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @ManyToOne(optional = false)
    private Medication medication;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    private Instant createdAt;

    private Instant lastNotifiedAt;
}