package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.TtsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TtsRepository extends JpaRepository<TtsEntity, String> {

    TtsEntity findByTtsId(Long ttsId);

    void deleteByTtsId(Long ttsId);

    boolean existsByTtsId(Long ttsId);

}
