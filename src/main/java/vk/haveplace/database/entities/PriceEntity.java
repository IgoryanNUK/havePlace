package vk.haveplace.database.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Time;

@Entity
@Table(name = "prices")
@Data
public class PriceEntity {
    private Time startTime;
    private Time endTime;
    private Integer price;
}
