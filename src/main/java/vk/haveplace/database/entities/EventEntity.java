package vk.haveplace.database.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(name = "event_time")
    private Timestamp time;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private BookingEntity booking;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private AdminEntity admin;

    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    private String comments;


    public EventEntity(BookingEntity booking, ClientEntity client, OperationType operationType,
                       String comments) {
        this.time = Timestamp.valueOf(LocalDateTime.now());
        this.booking = booking;
        this.client = client;
        this.operationType = operationType;
        this.comments = comments;
    }

    public EventEntity(BookingEntity booking, AdminEntity admin, OperationType operationType,
                       String comments) {
        this.time = Timestamp.valueOf(LocalDateTime.now());
        this.booking = booking;
        this.admin = admin;
        this.operationType = operationType;
        this.comments = comments;
    }
}
