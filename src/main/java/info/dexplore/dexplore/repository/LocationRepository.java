package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, String> {

    LocationEntity findByLocationId(Long locationId);

    void deleteByLocationId(Long locationId);
}
