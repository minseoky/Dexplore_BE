package info.dexplore.dexplore.dto.response.auth;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class SignInResponseDto extends ResponseDto {

    private String token;
    private int expirationTime;
    private String role;

    private SignInResponseDto (String token, String role) {
        super();
        this.token = token;
        this.expirationTime = 3600;
        this.role = role;
    }

    public static ResponseEntity<SignInResponseDto> success (String token, String role) {
        SignInResponseDto responseBody = new SignInResponseDto(token, role);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> signInFail () {
        ResponseDto responseBody = new ResponseDto(ResponseCode.SIGN_IN_FAIL, ResponseMessage.SIGN_IN_FAIL);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
    }
}
