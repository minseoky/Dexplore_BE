package info.dexplore.dexplore.dto.response.main;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SaveMuseumResponseDto extends ResponseDto {

    public SaveMuseumResponseDto() {
        super();
    }

    public static ResponseEntity<ResponseDto> idNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.ID_NOT_FOUND, ResponseMessage.ID_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> duplicatedMuseumName() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.DUPLICATE_MUSEUM_NAME, ResponseMessage.DUPLICATE_MUSEUM_NAME);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }
}
