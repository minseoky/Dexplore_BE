package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.MuseumEntity;
import info.dexplore.dexplore.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MuseumRepository extends JpaRepository<MuseumEntity, String> {
    boolean existsByMuseumId(Long museumId);

    boolean existsByMuseumName(String museumName);

    MuseumEntity findByMuseumId(Long museumId);

    List<MuseumEntity> findAllByUserId(String UserId);
}
