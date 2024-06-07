package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.BookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<BookmarkEntity, String> {

    boolean existsByUserIdAndArtId(String userId, Long artId);

    void deleteByUserIdAndArtId(String userId, Long artId);

    void deleteAllByArtId(Long artId);

    List<BookmarkEntity> findAllByUserId(String userId);

    BookmarkEntity findByUserIdAndArtId(String userId, Long artId);

}
