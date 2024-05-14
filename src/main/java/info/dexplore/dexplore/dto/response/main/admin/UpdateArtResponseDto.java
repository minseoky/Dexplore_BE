package info.dexplore.dexplore.dto.response.main.admin;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UpdateArtResponseDto extends ResponseDto {

    public UpdateArtResponseDto() {
        super();
    }

    public static ResponseEntity<ResponseDto> idNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.ID_NOT_FOUND, ResponseMessage.ID_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> artNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.ART_NOT_FOUND, ResponseMessage.ART_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> museumNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.MUSEUM_NOT_FOUND, ResponseMessage.MUSEUM_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }
}
