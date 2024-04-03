package info.dexplore.dexplore.dto.response.auth;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class SignUpResponseDto extends ResponseDto {

    private String role;

    public SignUpResponseDto(String role) {
        super();
        this.role = role;
    }

    public static ResponseEntity<ResponseDto> duplicateId() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.DUPLICATE_ID, ResponseMessage.DUPLICATE_ID);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> certificationFail() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.CERTIFICATION_FAIL, ResponseMessage.CERTIFICATION_FAIL);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> wrongRole() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.WRONG_ROLE, ResponseMessage.WRONG_ROLE);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

}
