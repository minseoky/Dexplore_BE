package info.dexplore.dexplore.service;

import info.dexplore.dexplore.dto.request.auth.*;
import info.dexplore.dexplore.dto.response.auth.*;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<? super IdCheckResponseDto> checkId(IdCheckRequestDto requestDto);
    ResponseEntity<? super EmailCertificationResponseDto> sendCertificationEmail(EmailCertificationRequestDto requestDto);
    ResponseEntity<? super CheckEmailCertificationResponseDto> checkCertificationNumber(CheckEmailCertificationRequestDto requestDto);
    ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto requestDto);
    ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto requestDto);

}
