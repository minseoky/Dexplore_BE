package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity, String> {
}
