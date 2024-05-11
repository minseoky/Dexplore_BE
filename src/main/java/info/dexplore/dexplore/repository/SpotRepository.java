package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.SpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotRepository extends JpaRepository<SpotEntity, String> {
}
