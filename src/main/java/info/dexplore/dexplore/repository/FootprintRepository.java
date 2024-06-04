package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.FootprintEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FootprintRepository extends JpaRepository<FootprintEntity, String> {

    boolean existsByUserIdAndArtId(String userId, Long artId);

    int countByUserId(String userId);


}
