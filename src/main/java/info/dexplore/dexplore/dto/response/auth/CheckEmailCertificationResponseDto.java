package info.dexplore.dexplore.dto.response.auth;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class CheckEmailCertificationResponseDto extends ResponseDto {

    public CheckEmailCertificationResponseDto() {
        super();
    }

    public static ResponseEntity<ResponseDto> certificationFail() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.CERTIFICATION_FAIL, ResponseMessage.CERTIFICATION_FAIL);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
    }
}
