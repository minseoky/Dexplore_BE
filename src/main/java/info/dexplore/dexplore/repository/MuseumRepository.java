package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.MuseumEntity;
import info.dexplore.dexplore.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MuseumRepository extends JpaRepository<MuseumEntity, String> {
    boolean existsByMuseumId(Long museumId);

    boolean existsByMuseumName(String museumName);

    MuseumEntity findByMuseumId(Long museumId);
}
