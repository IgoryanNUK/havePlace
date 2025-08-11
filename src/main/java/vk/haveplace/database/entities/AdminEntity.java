package vk.haveplace.database.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="admins")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private int id;

    @Column(name = "admin_name")
    private String name;

    @Column(name = "admin_vk_id")
    private Long vkId;

    @Enumerated(EnumType.STRING)
    private Role role;

}
