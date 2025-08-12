package vk.haveplace.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vk.haveplace.database.entities.EventEntity;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
}
