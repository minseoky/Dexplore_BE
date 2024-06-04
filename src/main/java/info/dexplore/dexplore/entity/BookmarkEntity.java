package info.dexplore.dexplore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "bookmark")
@Table(name = "bookmarks")
@IdClass(BookmarkId.class)
public class BookmarkEntity {

    @Id
    private String userId;
    @Id
    private Long artId;
}
