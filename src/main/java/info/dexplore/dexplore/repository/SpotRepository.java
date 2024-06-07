package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.SpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotRepository extends JpaRepository<SpotEntity, String> {
    void deleteBySpotId(Long spotId);
    SpotEntity findBySpotId(Long spotId);
}
