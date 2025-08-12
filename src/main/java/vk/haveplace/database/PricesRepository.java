package vk.haveplace.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vk.haveplace.database.entities.PriceEntity;

@Repository
public interface PricesRepository extends JpaRepository<PriceEntity, Integer> {
}
