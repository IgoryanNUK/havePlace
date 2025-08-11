package vk.haveplace.database.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="clients")
@Data
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="client_id")
    private int id;

    @Column(name="client_name")
    private String name;

    @Column(name = "client_phone")
    private String phone;

    @Column(name = "client_vk_link")
    private String vkLink;
}
