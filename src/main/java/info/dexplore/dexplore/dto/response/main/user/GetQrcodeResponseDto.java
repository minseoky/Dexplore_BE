package info.dexplore.dexplore.dto.response.main.user;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.QrcodeEntity;
import info.dexplore.dexplore.entity.TtsEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class GetQrcodeResponseDto extends ResponseDto {

    private QrcodeEntity qrcode;

    public GetQrcodeResponseDto(QrcodeEntity qrcode) {
        super();
        this.qrcode = qrcode;
    }

    public static ResponseEntity<ResponseDto> qrcodeNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.QR_NOT_FOUND, ResponseMessage.QR_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> success(QrcodeEntity qrcode) {
        GetQrcodeResponseDto responseBody = new GetQrcodeResponseDto(qrcode);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }


}
