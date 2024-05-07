package info.dexplore.dexplore.service;


import info.dexplore.dexplore.dto.request.auth.*;
import info.dexplore.dexplore.dto.response.auth.*;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.CertificationEntity;
import info.dexplore.dexplore.entity.UserEntity;
import info.dexplore.dexplore.provider.CertificationNumberProvider;
import info.dexplore.dexplore.provider.EmailProvider;
import info.dexplore.dexplore.provider.JwtProvider;
import info.dexplore.dexplore.repository.CertificationRepository;
import info.dexplore.dexplore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import info.dexplore.dexplore.common.Role;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final CertificationRepository certificationRepository;
    private final EmailProvider emailProvider;
    private final JwtProvider jwtProvider;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    /**
     * Id 중복 체크
     * @param requestDto
     * @return validationFailed, duplicateId, databaseError, success
     */
    @Override
    public ResponseEntity<? super IdCheckResponseDto> checkId(IdCheckRequestDto requestDto) {

        try {

            String id = requestDto.getId();
            boolean exists = userRepository.existsByUserId(id);
            if(exists) return IdCheckResponseDto.duplicateId();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }

        return IdCheckResponseDto.success();
    }

    /**
     * 이메일 전송
     * @param requestDto
     * @return validationFailed, duplicateId, mailSendFail, databaseError, success
     */
    public ResponseEntity<? super EmailCertificationResponseDto> sendCertificationEmail(EmailCertificationRequestDto requestDto) {


        try {
            String id = requestDto.getId();
            String email = requestDto.getEmail();
            boolean exists = userRepository.existsByUserId(id);
            if(exists) return EmailCertificationResponseDto.duplicateId();

            String certificationNumber = CertificationNumberProvider.generateNumber();

            boolean hasSent = emailProvider.sendCertificationMail(email, certificationNumber);
            if(!hasSent) return EmailCertificationResponseDto.mailSendFail();

            CertificationEntity certificationEntity = new CertificationEntity(id, email, certificationNumber);
            certificationRepository.save(certificationEntity);


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ResponseDto.success();
    }

    /**
     * 이메일 인증 번호 검증
     * @param requestDto
     * @return validationFailed, certificationFailed, databaseError, success
     */
    @Override
    public ResponseEntity<? super CheckEmailCertificationResponseDto> checkCertificationNumber(CheckEmailCertificationRequestDto requestDto) {

        try {
            String userId = requestDto.getId();
            String email = requestDto.getEmail();
            String certificationNumber = requestDto.getCertificationNumber();

            CertificationEntity certificationEntity = certificationRepository.findByUserId(userId);
            if(certificationEntity == null) return CheckEmailCertificationResponseDto.certificationFail();

            boolean isMatched = certificationEntity.getEmail().equals(email) && certificationEntity.getCertificationNumber().equals(certificationNumber);
            if(!isMatched) return CheckEmailCertificationResponseDto.certificationFail();


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ResponseDto.success();
    }

    /**
     * 회원가입
     * @param requestDto
     * @return validationFailed, duplicatedId, certificationFailed, wrongRole, databaseError, success
     */
    @Override
    public ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto requestDto) {

        try {

            String userId = requestDto.getId();
            boolean isExistId = userRepository.existsByUserId(userId);
            if(isExistId) return SignUpResponseDto.duplicateId();

            String email = requestDto.getEmail();
            String certificationNumber = requestDto.getCertificationNumber();
            CertificationEntity certificationEntity = certificationRepository.findByUserId(userId);
            boolean isMatched = certificationEntity.getEmail().equals(email) && certificationEntity.getCertificationNumber().equals(certificationNumber);
            if(!isMatched) return SignUpResponseDto.certificationFail();

            String password = requestDto.getPassword();
            //비밀번호 저장시 단방향 해시 암호화
            String encodedPassword = passwordEncoder.encode(password);
            requestDto.setPassword(encodedPassword);

            String role = requestDto.getRole();

            // 유저 확인
            if(!role.equals(Role.USER) && !role.equals(Role.ADMIN))
                return SignUpResponseDto.wrongRole();


            if(role.equals(Role.USER)) {
                UserEntity userEntity = new UserEntity(userId, encodedPassword, email, "app", Role.USER);
                userRepository.save(userEntity);
            }
            else if(role.equals(Role.ADMIN)) {
                UserEntity userEntity = new UserEntity(userId, encodedPassword, email, "app", Role.ADMIN);
                userRepository.save(userEntity);
            }
            certificationRepository.deleteByUserId(userId);


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ResponseDto.success();
    }

    /**
     * 로그인
     * @param requestDto
     * @return success, validationFail, signInFail, databaseError
     */
    @Override
    public ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto requestDto) {
        String token = null;

        try {

            String userId = requestDto.getId();
            UserEntity userEntity = userRepository.findByUserId(userId);
            if (userEntity == null) SignInResponseDto.signInFail();

            String password = requestDto.getPassword();
            String encodedPassword = userEntity.getPassword();
            boolean isMatched = passwordEncoder.matches(password, encodedPassword);
            if (!isMatched) return SignInResponseDto.signInFail();

            token = jwtProvider.create(userId);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }

        return SignInResponseDto.success(token);
    }


}
