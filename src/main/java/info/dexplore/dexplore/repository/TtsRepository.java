package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.TtsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TtsRepository extends JpaRepository<TtsEntity, String> {
}
