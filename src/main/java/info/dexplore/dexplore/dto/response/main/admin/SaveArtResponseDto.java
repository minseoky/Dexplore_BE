package info.dexplore.dexplore.dto.response.main.admin;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.QrcodeEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Getter
public class SaveArtResponseDto extends ResponseDto {

    private Long qrcodeId;

    public SaveArtResponseDto(Long qrcodeId) {
        super();
        this.qrcodeId = qrcodeId;
    }


    public static ResponseEntity<ResponseDto> idNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.ID_NOT_FOUND, ResponseMessage.ID_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> museumNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.MUSEUM_NOT_FOUND, ResponseMessage.MUSEUM_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> success(Long qrcodeId) {
        SaveArtResponseDto responseBody = new SaveArtResponseDto(qrcodeId);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}
