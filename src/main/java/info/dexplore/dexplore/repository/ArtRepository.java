package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.ArtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtRepository extends JpaRepository<ArtEntity, String> {

    ArtEntity findByArtId(Long artId);

    boolean existsByArtId(Long artId);

    List<ArtEntity> findArtEntitiesByMuseumId(Long museumId);

    List<ArtEntity> findAllByMuseumId(Long museumId);

    void deleteByArtId(Long artId);

    ArtEntity findByQrcodeId(Long qrcodeId);

    boolean existsByQrcodeId(Long qrcodeId);

    Long countByMuseumId(Long museumId);

}
