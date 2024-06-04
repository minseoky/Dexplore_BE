package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.FootprintEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FootprintRepository extends JpaRepository<FootprintEntity, String> {

    boolean existsByUserIdAndArtId(String userId, Long artId);

    Long countAllByUserId(String userId);

    void deleteAllByArtId(Long artId);


}
