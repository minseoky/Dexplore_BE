package info.dexplore.dexplore.dto.response.main.user;

import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.BookmarkEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class GetBookmarkListResponseDto extends ResponseDto {

    private List<BookmarkEntity> bookmarkList;

    public GetBookmarkListResponseDto(List<BookmarkEntity> bookmarkList) {
        super();
        this.bookmarkList = bookmarkList;
    }

    public static ResponseEntity<ResponseDto> success(List<BookmarkEntity> bookmarkList) {
        GetBookmarkListResponseDto responseBody = new GetBookmarkListResponseDto(bookmarkList);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

}
