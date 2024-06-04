package info.dexplore.dexplore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.request.auth.SignInRequestDto;
import info.dexplore.dexplore.dto.request.auth.SignUpRequestDto;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.dto.response.auth.SignInResponseDto;
import info.dexplore.dexplore.dto.response.auth.SignUpResponseDto;
import info.dexplore.dexplore.entity.CertificationEntity;
import info.dexplore.dexplore.entity.UserEntity;
import info.dexplore.dexplore.provider.JwtProvider;
import info.dexplore.dexplore.repository.CertificationRepository;
import info.dexplore.dexplore.repository.UserRepository;
import info.dexplore.dexplore.service.AuthServiceImpl;

@SpringBootTest
public class AuthServiceImplTest {
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private CertificationRepository certificationRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void signUp_Success(){
        SignUpRequestDto requestDto = new SignUpRequestDto();
        requestDto.setId("TestUser");
        requestDto.setPassword("TestPW");
        requestDto.setEmail("Test@gmail.com");
        requestDto.setCertificationNumber("123456");
        requestDto.setRole("ROLE_USER");

        CertificationEntity certificationEntity = new CertificationEntity("TestUser", "Test@gmail.com", "123456");
        when(userRepository.existsByUserId(requestDto.getId())).thenReturn(false);
        when(certificationRepository.findByUserId(requestDto.getId())).thenReturn(certificationEntity);

        ResponseEntity<? super SignUpResponseDto> response = authService.signUp(requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SUCCESS, responseBody.getCode());
        assertEquals(ResponseMessage.SUCCESS, responseBody.getMessage());
    }

    @Test
    void signUp_Fail_duplicated(){
        SignUpRequestDto requestDto = new SignUpRequestDto();
        requestDto.setId("TestUser");
        requestDto.setPassword("TestPW");
        requestDto.setEmail("Test@gmail.com");
        requestDto.setCertificationNumber("123456");
        requestDto.setRole("ROLE_USER");

        when(userRepository.existsByUserId(requestDto.getId())).thenReturn(true);

        ResponseEntity<? super SignUpResponseDto> response = authService.signUp(requestDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DUPLICATE_ID, responseBody.getCode());
        assertEquals(ResponseMessage.DUPLICATE_ID, responseBody.getMessage());
    }

    @Test
    void signUp_Fail_certificationFail_Email(){
        SignUpRequestDto requestDto = new SignUpRequestDto();
        requestDto.setId("TestUser");
        requestDto.setPassword("TestPW");
        requestDto.setEmail("Test@gmail.com");
        requestDto.setCertificationNumber("123456");
        requestDto.setRole("ROLE_USER");

        CertificationEntity certificationEntity = new CertificationEntity("TestUser", "Test@gmail2.com", "123456");
        
        when(userRepository.existsByUserId(requestDto.getId())).thenReturn(false);
        when(certificationRepository.findByUserId(requestDto.getId())).thenReturn(certificationEntity);

        ResponseEntity<? super SignUpResponseDto> response = authService.signUp(requestDto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.CERTIFICATION_FAIL, responseBody.getCode());
        assertEquals(ResponseMessage.CERTIFICATION_FAIL, responseBody.getMessage());
    }

    @Test
    void signUp_Fail_certificationFail_Number(){
        SignUpRequestDto requestDto = new SignUpRequestDto();
        requestDto.setId("TestUser");
        requestDto.setPassword("TestPW");
        requestDto.setEmail("Test@gmail.com");
        requestDto.setCertificationNumber("123456");
        requestDto.setRole("ROLE_USER");

        CertificationEntity certificationEntity = new CertificationEntity("TestUser", "Test@gmail.com", "1234567");
        
        when(userRepository.existsByUserId(requestDto.getId())).thenReturn(false);
        when(certificationRepository.findByUserId(requestDto.getId())).thenReturn(certificationEntity);

        ResponseEntity<? super SignUpResponseDto> response = authService.signUp(requestDto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.CERTIFICATION_FAIL, responseBody.getCode());
        assertEquals(ResponseMessage.CERTIFICATION_FAIL, responseBody.getMessage());
    }

    @Test
    void signUp_Fail_Role(){
        SignUpRequestDto requestDto = new SignUpRequestDto();
        requestDto.setId("TestUser");
        requestDto.setPassword("TestPW");
        requestDto.setEmail("Test@gmail.com");
        requestDto.setCertificationNumber("123456");
        requestDto.setRole("ROLE_WRONG");

        CertificationEntity certificationEntity = new CertificationEntity("TestUser", "Test@gmail.com", "123456");
        
        when(userRepository.existsByUserId(requestDto.getId())).thenReturn(false);
        when(certificationRepository.findByUserId(requestDto.getId())).thenReturn(certificationEntity);

        ResponseEntity<? super SignUpResponseDto> response = authService.signUp(requestDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.WRONG_ROLE, responseBody.getCode());
        assertEquals(ResponseMessage.WRONG_ROLE, responseBody.getMessage());
    }

    @Test
    void signUp_Fail_DBError_1(){
        SignUpRequestDto requestDto = new SignUpRequestDto();
        requestDto.setId("TestUser");
        requestDto.setPassword("TestPW");
        requestDto.setEmail("Test@gmail.com");
        requestDto.setCertificationNumber("123456");
        requestDto.setRole("ROLE_USER");

        when(userRepository.findByUserId("TestUser")).thenThrow(new RuntimeException("Database error"));
        ResponseEntity<? super SignUpResponseDto> response = authService.signUp(requestDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void signUp_Fail_DBError_2(){
        SignUpRequestDto requestDto = new SignUpRequestDto();
        requestDto.setId("TestUser");
        requestDto.setPassword("TestPW");
        requestDto.setEmail("Test@gmail.com");
        requestDto.setCertificationNumber("123456");
        requestDto.setRole("ROLE_USER");

        when(userRepository.existsByUserId(requestDto.getId())).thenReturn(false);
        when(certificationRepository.findByUserId(requestDto.getId())).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<? super SignUpResponseDto> response = authService.signUp(requestDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void signIn_Success(){
        // Given
        SignInRequestDto requestDto = new SignInRequestDto();
        requestDto.setId("TestUser");
        requestDto.setPassword("TestPW");

        String encodedPassword = passwordEncoder.encode("TestPW");
        UserEntity userEntity = new UserEntity("TestUser", encodedPassword, "TestEmail@gmail.com", "app", "ROLE_ADMIN");

        when(userRepository.findByUserId("TestUser")).thenReturn(userEntity);
        when(jwtProvider.create("TestUser","ROLE_ADMIN")).thenReturn("vaildToken");

        // When
        ResponseEntity<? super SignInResponseDto> response = authService.signIn(requestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof SignInResponseDto);
        SignInResponseDto responseBody = (SignInResponseDto) response.getBody();
        assertEquals("vaildToken", responseBody.getToken());
        assertEquals("ROLE_ADMIN", responseBody.getRole());
    }

    @Test
    void signIn_Fail_NoUser(){
        // Given
        SignInRequestDto requestDto = new SignInRequestDto();
        requestDto.setId("TestUser_None");
        requestDto.setPassword("TestPW");

        UserEntity userEntity = new UserEntity();
        when(userRepository.findByUserId(anyString())).thenReturn(userEntity);

        // When
        ResponseEntity<? super SignInResponseDto> response = authService.signIn(requestDto);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SIGN_IN_FAIL, responseBody.getCode());
        assertEquals(ResponseMessage.SIGN_IN_FAIL, responseBody.getMessage());
    }

    @Test
    void signIn_Fail_WrongPassword(){
        // Given
        SignInRequestDto requestDto = new SignInRequestDto();
        requestDto.setId("TestUser");
        requestDto.setPassword("TestPW_Wrong");

        String encodedPassword = passwordEncoder.encode("TestPW");
        UserEntity userEntity = new UserEntity("TestUser", encodedPassword, "TestEmail@gmail.com", "app", "ROLE_ADMIN");

        when(userRepository.findByUserId("TestUser")).thenReturn(userEntity);

        // When
        ResponseEntity<? super SignInResponseDto> response = authService.signIn(requestDto);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SIGN_IN_FAIL, responseBody.getCode());
        assertEquals(ResponseMessage.SIGN_IN_FAIL, responseBody.getMessage());
    }

    @Test
    void signIn_Fail_DBError_1(){
        // Given
        SignInRequestDto requestDto = new SignInRequestDto();
        requestDto.setId("TestUser");
        requestDto.setPassword("TestPW");

        when(userRepository.findByUserId("TestUser")).thenThrow(new RuntimeException("Database error"));

        // When
        ResponseEntity<? super SignInResponseDto> response = authService.signIn(requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void signIn_Fail_DBError_2(){
        SignInRequestDto requestDto = new SignInRequestDto();
        requestDto.setId("TestUser");
        requestDto.setPassword("TestPW");

        String encodedPassword = passwordEncoder.encode("TestPW");
        UserEntity userEntity = new UserEntity("TestUser", encodedPassword, "TestEmail@gmail.com", "app", "ROLE_ADMIN");

        when(userRepository.findByUserId("TestUser")).thenReturn(userEntity);
        when(jwtProvider.create("TestUser", "ROLE_ADMIN")).thenThrow(new RuntimeException("Database error"));

        // when
        ResponseEntity<? super SignInResponseDto> response = authService.signIn(requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }
}