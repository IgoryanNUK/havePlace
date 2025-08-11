package vk.haveplace.database.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;

@Entity
@Table(name = "regular_events")
@Data
public class RegularEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @JoinColumn(name = "location_id")
    @ManyToOne
    private LocationEntity location;

    private String dayOfWeek;

    private Time startTime;
    private Time endTime;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    private Integer numberOfPlayers;

}
