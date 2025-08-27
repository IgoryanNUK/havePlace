package vk.haveplace.database.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "bookings")
@Data
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer id;

    @Column(name = "booking_date")
    private Date date;

    @Column(name = "booking_start_time")
    private Time startTime;

    @Column(name = "booking_end_time")
    private Time endTime;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationEntity location;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private AdminEntity admin;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    private String device;
    private Integer numberOfPlayers;
    private String comments;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private Boolean isAvailable;
}
