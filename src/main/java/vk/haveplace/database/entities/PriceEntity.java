package vk.haveplace.database.entities;

import jakarta.persistence.*;
import lombok.Data;
import vk.haveplace.services.objects.TimeSlot;

import java.sql.Time;

@Entity
@Table(name = "prices")
@Data
@IdClass(TimeSlot.class)
public class PriceEntity {
    @Id
    @Column(name = "start_time")
    private Time start;
    @Id
    @Column(name = "end_time")
    private Time end;
    private Integer price;
}
