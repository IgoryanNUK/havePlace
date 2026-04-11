package vk.haveplace.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vk.haveplace.database.entities.LocationEntity;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Integer> {
    List<LocationEntity> findAllByIsExistingTrue();
}
