package info.dexplore.dexplore.dto.response.main.user;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class CheckBookmarkResponseDto extends ResponseDto {

    private boolean isBookmark;

    public CheckBookmarkResponseDto(boolean isBookmark) {
        super();
        this.isBookmark = isBookmark;
    }

    public static ResponseEntity<ResponseDto> artNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.ART_NOT_FOUND, ResponseMessage.ART_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> success(boolean isBookmark) {
        CheckBookmarkResponseDto responseBody = new CheckBookmarkResponseDto(isBookmark);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }


}
