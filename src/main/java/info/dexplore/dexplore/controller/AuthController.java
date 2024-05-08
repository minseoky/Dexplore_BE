package info.dexplore.dexplore.controller;

import info.dexplore.dexplore.dto.request.auth.*;
import info.dexplore.dexplore.dto.response.auth.*;
import info.dexplore.dexplore.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Id 중복 체크
     * @return validationFailed, duplicateMuseumName, databaseError, success
     */
    @PostMapping("/id-check")
    public ResponseEntity<? super IdCheckResponseDto> idCheck (@RequestBody @Valid IdCheckRequestDto requestBody) {
        ResponseEntity<? super IdCheckResponseDto> response = authService.checkId(requestBody);
        log.info("[idCheck]: {id:" + requestBody.getId() + "}");
        return response;
    }
    /**
     * 이메일 전송
     * @return validationFailed, duplicateId, mailSendFail, databaseError, success
     */
    @PostMapping("/email-certification")
    public ResponseEntity<? super EmailCertificationResponseDto> emailCertification(@RequestBody @Valid EmailCertificationRequestDto requestBody) {
        ResponseEntity<? super EmailCertificationResponseDto> response = authService.sendCertificationEmail(requestBody);
        log.info("[emailCertification]: {id: " + requestBody.getId() + ", email: " + requestBody.getEmail() + "}");
        return response;
    }

    /**
     * 이메일 인증 번호 검증
     * @return validationFailed, certificationFailed, databaseError, success
     */
    @PostMapping("/check-certification")
    public ResponseEntity<? super CheckEmailCertificationResponseDto> checkEmailCertification (@RequestBody @Valid CheckEmailCertificationRequestDto requestBody) {
        ResponseEntity<? super CheckEmailCertificationResponseDto> response = authService.checkCertificationNumber(requestBody);
        log.info("[checkEmailCertification]: {email: " + requestBody.getEmail() + ", certNum: " + requestBody.getCertificationNumber() + "}");
        return response;
    }

    /**
     * 회원가입
     * @return validationFailed, duplicatedId, certificationFailed, databaseError, success
     */
    @PostMapping("/sign-up")
    public ResponseEntity<? super SignUpResponseDto> signUp (@RequestBody @Valid SignUpRequestDto requestBody) {
        ResponseEntity<? super SignUpResponseDto> response = authService.signUp(requestBody);
        log.info("[signUp]: {id: " + requestBody.getId() + ", password: " + requestBody.getPassword() + ", email: " + requestBody.getEmail() + "}");
        return response;
    }

    /**
     * 로그인
     * @param requestBody
     * @return validationFailed, signinFailed, databaseError, success
     */
    @PostMapping("/sign-in")
    public ResponseEntity<? super SignInResponseDto> signIn (@RequestBody @Valid SignInRequestDto requestBody) {
        ResponseEntity<? super SignInResponseDto> response = authService.signIn(requestBody);
        log.info("[signIn]: {id: " + requestBody.getId() + ", password: " + requestBody.getPassword() + "}");
        return response;
    }

}
