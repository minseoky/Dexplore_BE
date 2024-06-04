package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.BookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<BookmarkEntity, String> {

    boolean existsByUserIdAndArtId(String userId, Long artId);

    void deleteByUserIdAndArtId(String userId, Long artId);

    void deleteAllByArtId(Long artId);

}
