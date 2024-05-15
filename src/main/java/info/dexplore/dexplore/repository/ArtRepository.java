package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.ArtEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtRepository extends JpaRepository<ArtEntity, String> {

    ArtEntity findByArtId(Long artId);

    boolean existsByArtId(Long artId);

    List<ArtEntity> findArtEntitiesByMuseumId(Long museumId);

    void deleteByArtId(Long artId);

    ArtEntity findByQrcodeId(Long qrcodeId);

    boolean existsByQrcodeId(Long qrcodeId);

}
