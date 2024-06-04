package info.dexplore.dexplore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.junit.jupiter.api.extension.ExtendWith;

import com.amazonaws.services.s3.AmazonS3;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.request.main.admin.*;
import info.dexplore.dexplore.dto.request.main.user.*;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.dto.response.main.admin.*;
import info.dexplore.dexplore.dto.response.main.user.*;
import info.dexplore.dexplore.entity.*;
import info.dexplore.dexplore.provider.QrcodeProvider;
import info.dexplore.dexplore.provider.TtsProvider;
import info.dexplore.dexplore.repository.*;

import info.dexplore.dexplore.service.MainServiceImpl;

@ExtendWith(MockitoExtension.class)
public class MainServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private MuseumRepository museumRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private ArtRepository artRepository;
    @Mock
    private TtsRepository ttsRepository;
    @Mock
    private QrcodeRepository qrcodeRepository;
    @Mock
    private SpotRepository spotRepository;
    @Mock
    private FootprintRepository footprintRepository;
    @Mock
    private BookmarkRepository bookmarkRepository;
    
    @Mock
    private TtsProvider ttsProvider;
    @Mock
    private QrcodeProvider qrcodeProvider;
    

    @Mock
    private AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Spy
    @InjectMocks
    private MainServiceImpl mainServiceImpl;

    @BeforeEach
    public void setUp() {
        // Mock SecurityContext and Authentication
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);

        // Set the SecurityContext to SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);

        // Configure the mocked objects
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getName()).thenReturn("TestUser");
    }
    
    @Test
    void saveMuseum_Success() throws IOException {
        // Given
        String userId = "TestUser";
       
        SaveMuseumRequestDto requestDto = new SaveMuseumRequestDto();
        requestDto.setMuseumName("Test_Museum");
        requestDto.setEntPrice("9000");
        requestDto.setMuseumEmail("Test_Museum@gmail.com");
        requestDto.setStartTime("9:00");
        requestDto.setEndTime("18:00");
        requestDto.setClosingDay("Monday");
        requestDto.setDescription("This is Test_Museum");
        requestDto.setPhone("031-123-4567");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);
       
        // Mock the S3 client behavior with correct arguments
        String expectedKey = userId + requestDto.getMuseumName() + originalFileName;
        URL mockUrl = new URL("https://mock-s3-url.com/test_image.jpg");
        when(amazonS3.getUrl(bucket, expectedKey)).thenReturn(mockUrl);

        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumName(requestDto.getMuseumName())).thenReturn(false);
        
        // When
        ResponseEntity<? super SaveMuseumResponseDto> response = mainServiceImpl.saveMuseum(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SUCCESS, responseBody.getCode());
        assertEquals(ResponseMessage.SUCCESS, responseBody.getMessage());
    }

    @Test
    void saveMuseum_Fail_NotFound() throws IOException {
        // Given
        String userId = "TestUser";
       
        SaveMuseumRequestDto requestDto = new SaveMuseumRequestDto();
        requestDto.setMuseumName("Test_Museum");
        requestDto.setEntPrice("9000");
        requestDto.setMuseumEmail("Test_Museum@gmail.com");
        requestDto.setStartTime("9:00");
        requestDto.setEndTime("18:00");
        requestDto.setClosingDay("Monday");
        requestDto.setDescription("This is Test_Museum");
        requestDto.setPhone("031-123-4567");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);
    
        when(userRepository.existsByUserId(userId)).thenReturn(false);
        
        // When
        ResponseEntity<? super SaveMuseumResponseDto> response = mainServiceImpl.saveMuseum(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.ID_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.ID_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void saveMuseum_Fail_Duplicated() throws IOException {
        // Given
        String userId = "TestUser";
       
        SaveMuseumRequestDto requestDto = new SaveMuseumRequestDto();
        requestDto.setMuseumName("Test_Museum");
        requestDto.setEntPrice("9000");
        requestDto.setMuseumEmail("Test_Museum@gmail.com");
        requestDto.setStartTime("9:00");
        requestDto.setEndTime("18:00");
        requestDto.setClosingDay("Monday");
        requestDto.setDescription("This is Test_Museum");
        requestDto.setPhone("031-123-4567");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);
    
        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumName(requestDto.getMuseumName())).thenReturn(true);
        
        // When
        ResponseEntity<? super SaveMuseumResponseDto> response = mainServiceImpl.saveMuseum(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DUPLICATE_MUSEUM_NAME, responseBody.getCode());
        assertEquals(ResponseMessage.DUPLICATE_MUSEUM_NAME, responseBody.getMessage());
    }

    @Test
    void saveMuseum_Fail_DBError_Museum() throws IOException {
        //Given
        String userId = "TestUser";
       
        SaveMuseumRequestDto requestDto = new SaveMuseumRequestDto();
        requestDto.setMuseumName("Test_Museum");
        requestDto.setEntPrice("9000");
        requestDto.setMuseumEmail("Test_Museum@gmail.com");
        requestDto.setStartTime("9:00");
        requestDto.setEndTime("18:00");
        requestDto.setClosingDay("Monday");
        requestDto.setDescription("This is Test_Museum");
        requestDto.setPhone("031-123-4567");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);

        String expectedKey = userId + requestDto.getMuseumName() + originalFileName;
        URL mockUrl = new URL("https://mock-s3-url.com/test_image.jpg");

        when(amazonS3.getUrl(bucket, expectedKey)).thenReturn(mockUrl);
        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumName(requestDto.getMuseumName())).thenReturn(false);
        when(museumRepository.save(any(MuseumEntity.class))).thenThrow(new RuntimeException("Database error"));
        
        // When
        ResponseEntity<? super SaveMuseumResponseDto> response = mainServiceImpl.saveMuseum(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void saveMuseum_Fail_DBError_Location() throws IOException {
        //Given
        String userId = "TestUser";
       
        SaveMuseumRequestDto requestDto = new SaveMuseumRequestDto();
        requestDto.setMuseumName("Test_Museum");
        requestDto.setEntPrice("9000");
        requestDto.setMuseumEmail("Test_Museum@gmail.com");
        requestDto.setStartTime("9:00");
        requestDto.setEndTime("18:00");
        requestDto.setClosingDay("Monday");
        requestDto.setDescription("This is Test_Museum");
        requestDto.setPhone("031-123-4567");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);

        String expectedKey = userId + requestDto.getMuseumName() + originalFileName;
        URL mockUrl = new URL("https://mock-s3-url.com/test_image.jpg");

        when(amazonS3.getUrl(bucket, expectedKey)).thenReturn(mockUrl);
        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumName(requestDto.getMuseumName())).thenReturn(false);
        when(locationRepository.save(any(LocationEntity.class))).thenThrow(new RuntimeException("Database error"));
        
        // When
        ResponseEntity<? super SaveMuseumResponseDto> response = mainServiceImpl.saveMuseum(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void updateMuseum_Success() throws IOException {
        // Given
        String userId = "TestUser";
       
        UpdateMuseumRequestDto requestDto = new UpdateMuseumRequestDto();
            requestDto.setMuseumId((long) 1);
            requestDto.setMuseumName("Test_Museum");
            requestDto.setEntPrice("9000");
            requestDto.setMuseumEmail("Test_Museum@gmail.com");
            requestDto.setStartTime("9:00");
            requestDto.setEndTime("18:00");
            requestDto.setClosingDay("Monday");
            requestDto.setDescription("This is Test_Museum");
            requestDto.setPhone("031-123-4567");
            requestDto.setLatitude(BigDecimal.valueOf(12.000000));
            requestDto.setLongitude(BigDecimal.valueOf(12.000000));
            requestDto.setLevel("1");
            requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
            requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
            requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
            requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);
        // Mock the S3 client behavior with correct arguments
        String expectedKey = userId + requestDto.getMuseumName() + originalFileName;
        URL mockUrl = new URL("https://mock-s3-url.com/test_image.jpg");

        MuseumEntity museumEntity = new MuseumEntity((long) 1, "Test_Museum", userId, (long) 1, "9000", "Test_Museum@gmail.com", "9:00", "18:00", "Monday", "This is Test_Museum", "031-123-4567", "https://example.com/123123/123");
        LocationEntity locationEntity = new LocationEntity((long) 1, BigDecimal.valueOf(12.000000), BigDecimal.valueOf(12.000000), "1", BigDecimal.valueOf(8.000000), BigDecimal.valueOf(8.000000), BigDecimal.valueOf(8.000000), BigDecimal.valueOf(8.000000));
     
        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(amazonS3.getUrl(bucket, expectedKey)).thenReturn(mockUrl);
        when(museumRepository.findByMuseumId(requestDto.getMuseumId())).thenReturn(museumEntity);
        when(locationRepository.findByLocationId(museumEntity.getLocationId())).thenReturn(locationEntity);

        // When
        ResponseEntity<? super UpdateMuseumResponseDto> response = mainServiceImpl.updateMuseum(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SUCCESS, responseBody.getCode());
        assertEquals(ResponseMessage.SUCCESS, responseBody.getMessage());
    }

    @Test
    void updateMuseum_Fail_NotFound_User() throws IOException {
        // Given
        String userId = "TestUser";
       
        UpdateMuseumRequestDto requestDto = new UpdateMuseumRequestDto();
            requestDto.setMuseumId((long) 1);
            requestDto.setMuseumName("Test_Museum");
            requestDto.setEntPrice("9000");
            requestDto.setMuseumEmail("Test_Museum@gmail.com");
            requestDto.setStartTime("9:00");
            requestDto.setEndTime("18:00");
            requestDto.setClosingDay("Monday");
            requestDto.setDescription("This is Test_Museum");
            requestDto.setPhone("031-123-4567");
            requestDto.setLatitude(BigDecimal.valueOf(12.000000));
            requestDto.setLongitude(BigDecimal.valueOf(12.000000));
            requestDto.setLevel("1");
            requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
            requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
            requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
            requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);
        
        when(userRepository.existsByUserId(userId)).thenReturn(false);
        
        // When
        ResponseEntity<? super UpdateMuseumResponseDto> response = mainServiceImpl.updateMuseum(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.ID_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.ID_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void updateMuseum_Fail_NotFound_Museum() throws IOException {
        // Given
        String userId = "TestUser";
       
        UpdateMuseumRequestDto requestDto = new UpdateMuseumRequestDto();
        requestDto.setMuseumId((long) 1);
        requestDto.setMuseumName("Test_Museum");
        requestDto.setEntPrice("9000");
        requestDto.setMuseumEmail("Test_Museum@gmail.com");
        requestDto.setStartTime("9:00");
        requestDto.setEndTime("18:00");
        requestDto.setClosingDay("Monday");
        requestDto.setDescription("This is Test_Museum");
        requestDto.setPhone("031-123-4567");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);
        
        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(false);
        
        // When
        ResponseEntity<? super UpdateMuseumResponseDto> response = mainServiceImpl.updateMuseum(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.MUSEUM_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.MUSEUM_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void updateMuseum_Fail_DBError_Location() throws IOException {
        // Given
        String userId = "TestUser";
       
        UpdateMuseumRequestDto requestDto = new UpdateMuseumRequestDto();
        requestDto.setMuseumId((long) 1);
        requestDto.setMuseumName("Test_Museum");
        requestDto.setEntPrice("9000");
        requestDto.setMuseumEmail("Test_Museum@gmail.com");
        requestDto.setStartTime("9:00");
        requestDto.setEndTime("18:00");
        requestDto.setClosingDay("Monday");
        requestDto.setDescription("This is Test_Museum");
        requestDto.setPhone("031-123-4567");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);

        String expectedKey = userId + requestDto.getMuseumName() + originalFileName;
        URL mockUrl = new URL("https://mock-s3-url.com/test_image.jpg");

        MuseumEntity museumEntity = new MuseumEntity((long) 1, "Test_Museum", userId, (long) 1, "9000", "Test_Museum@gmail.com", "9:00", "18:00", "Monday", "This is Test_Museum", "031-123-4567", "https://example.com/123123/123");
        LocationEntity locationEntity = new LocationEntity((long) 1, BigDecimal.valueOf(12.000000), BigDecimal.valueOf(12.000000), "1", BigDecimal.valueOf(8.000000), BigDecimal.valueOf(8.000000), BigDecimal.valueOf(8.000000), BigDecimal.valueOf(8.000000));
     
        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(amazonS3.getUrl(bucket, expectedKey)).thenReturn(mockUrl);
        when(locationRepository.findByLocationId(museumEntity.getLocationId())).thenReturn(locationEntity);
        when(museumRepository.findByMuseumId(requestDto.getMuseumId())).thenReturn(museumEntity);

        when(locationRepository.save(any(LocationEntity.class))).thenThrow(new RuntimeException("Database error"));
        
        // When
        ResponseEntity<? super UpdateMuseumResponseDto> response = mainServiceImpl.updateMuseum(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void updateMuseum_Fail_DBError_Museum() throws IOException {
        // Given
        String userId = "TestUser";
       
        UpdateMuseumRequestDto requestDto = new UpdateMuseumRequestDto();
        requestDto.setMuseumId((long) 1);
        requestDto.setMuseumName("Test_Museum");
        requestDto.setEntPrice("9000");
        requestDto.setMuseumEmail("Test_Museum@gmail.com");
        requestDto.setStartTime("9:00");
        requestDto.setEndTime("18:00");
        requestDto.setClosingDay("Monday");
        requestDto.setDescription("This is Test_Museum");
        requestDto.setPhone("031-123-4567");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);

        String expectedKey = userId + requestDto.getMuseumName() + originalFileName;
        URL mockUrl = new URL("https://mock-s3-url.com/test_image.jpg");

        MuseumEntity museumEntity = new MuseumEntity((long) 1, "Test_Museum", userId, (long) 1, "9000", "Test_Museum@gmail.com", "9:00", "18:00", "Monday", "This is Test_Museum", "031-123-4567", "https://example.com/123123/123");
        LocationEntity locationEntity = new LocationEntity((long) 1, BigDecimal.valueOf(12.000000), BigDecimal.valueOf(12.000000), "1", BigDecimal.valueOf(8.000000), BigDecimal.valueOf(8.000000), BigDecimal.valueOf(8.000000), BigDecimal.valueOf(8.000000));
     
        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(amazonS3.getUrl(bucket, expectedKey)).thenReturn(mockUrl);
        when(locationRepository.findByLocationId(museumEntity.getLocationId())).thenReturn(locationEntity);
        when(museumRepository.findByMuseumId(requestDto.getMuseumId())).thenReturn(museumEntity);

        when(museumRepository.save(any(MuseumEntity.class))).thenThrow(new RuntimeException("Database error"));
        
        // When
        ResponseEntity<? super UpdateMuseumResponseDto> response = mainServiceImpl.updateMuseum(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }
    
    @Test
    void deleteMuseum_Success(){
        // Given
        String userId = "TestUser";

        DeleteMuseumRequestDto requestDto = new DeleteMuseumRequestDto();
        requestDto.setMuseumId(1L);

        MuseumEntity museumEntity = new MuseumEntity(1L, "Test_Museum", userId, 1L, "9000", "Test_Museum@gmail.com", "9:00", "18:00", "Monday", "This is Test_Museum", "031-123-4567", "https://example.com/123123/123");
        ArtEntity artEntity = new ArtEntity(1L, requestDto.getMuseumId(), 1L, 1L, 1L, "Test_art", "This is Test_art", "1999", "Tester", "https://example.com/111111");

        List<ArtEntity> artEntities = new ArrayList<>();
        artEntities.add(artEntity);

        TtsEntity ttsEntity = new TtsEntity(1L, "http://example.com/bucket");
       
        when(ttsRepository.findByTtsId(artEntity.getTtsId())).thenReturn(ttsEntity);

        when(museumRepository.findByMuseumId(requestDto.getMuseumId())).thenReturn(museumEntity);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(artRepository.findArtEntitiesByMuseumId(requestDto.getMuseumId())).thenReturn(artEntities);
        when(ttsRepository.findByTtsId(artEntity.getTtsId())).thenReturn(ttsEntity);

        // When
        ResponseEntity<? super DeleteMuseumResponseDto> response = mainServiceImpl.deleteMuseum(requestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SUCCESS, responseBody.getCode());
        assertEquals(ResponseMessage.SUCCESS, responseBody.getMessage());
    }

    @Test
    void deleteMuseum_Fail_NotFound_Museum(){
        // Given
        String userId = "TestUser";

        DeleteMuseumRequestDto requestDto = new DeleteMuseumRequestDto();
        requestDto.setMuseumId(1L);

        
        MuseumEntity museumEntity = new MuseumEntity(1L, "Test_Museum", userId, 1L, "9000", "Test_Museum@gmail.com", "9:00", "18:00", "Monday", "This is Test_Museum", "031-123-4567", "https://example.com/123123/123");
        when(museumRepository.findByMuseumId(requestDto.getMuseumId())).thenReturn(museumEntity);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(false);

        // When
        ResponseEntity<? super DeleteMuseumResponseDto> response = mainServiceImpl.deleteMuseum(requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.MUSEUM_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.MUSEUM_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void deleteMuseum_Fail_NotFound_User(){
        // Given
        String userId = "Test_User";

        DeleteMuseumRequestDto requestDto = new DeleteMuseumRequestDto();
        requestDto.setMuseumId(1L);

        
        MuseumEntity museumEntity = new MuseumEntity(1L, "Test_Museum", userId, 1L, "9000", "Test_Museum@gmail.com", "9:00", "18:00", "Monday", "This is Test_Museum", "031-123-4567", "https://example.com/123123/123");

        when(museumRepository.findByMuseumId(requestDto.getMuseumId())).thenReturn(museumEntity);

        // When
        ResponseEntity<? super DeleteMuseumResponseDto> response = mainServiceImpl.deleteMuseum(requestDto);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.ID_NOT_MATCHING, responseBody.getCode());
        assertEquals(ResponseMessage.ID_NOT_MATCHING, responseBody.getMessage());
    }

    @Test
    void deleteMuseum_Fail_DBError_Museum(){
        // Given
        DeleteMuseumRequestDto requestDto = new DeleteMuseumRequestDto();
        requestDto.setMuseumId(1L);

        // When
        ResponseEntity<? super DeleteMuseumResponseDto> response = mainServiceImpl.deleteMuseum(requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void deleteMuseum_Fail_DBError_Art(){
        // Given
        String userId = "TestUser";

        DeleteMuseumRequestDto requestDto = new DeleteMuseumRequestDto();
        requestDto.setMuseumId(1L);

        MuseumEntity museumEntity = new MuseumEntity(1L, "Test_Museum", userId, 1L, "9000", "Test_Museum@gmail.com", "9:00", "18:00", "Monday", "This is Test_Museum", "031-123-4567", "https://example.com/123123/123");
        ArtEntity artEntity = new ArtEntity(1L, requestDto.getMuseumId(), 1L, 1L, 1L, "Test_art", "This is Test_art", "1999", "Tester", "https://example.com/111111");

        List<ArtEntity> artEntities = new ArrayList<>();
        artEntities.add(artEntity);
       
        when(museumRepository.findByMuseumId(requestDto.getMuseumId())).thenReturn(museumEntity);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(artRepository.findArtEntitiesByMuseumId(requestDto.getMuseumId())).thenThrow(new RuntimeException("Database Error"));

        // When
        ResponseEntity<? super DeleteMuseumResponseDto> response = mainServiceImpl.deleteMuseum(requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void deleteMuseum_Fail_DBError_TTS(){
        // Given
        String userId = "TestUser";

        DeleteMuseumRequestDto requestDto = new DeleteMuseumRequestDto();
        requestDto.setMuseumId(1L);

        MuseumEntity museumEntity = new MuseumEntity(1L, "Test_Museum", userId, 1L, "9000", "Test_Museum@gmail.com", "9:00", "18:00", "Monday", "This is Test_Museum", "031-123-4567", "https://example.com/123123/123");
        ArtEntity artEntity = new ArtEntity(1L, requestDto.getMuseumId(), 1L, 1L, 1L, "Test_art", "This is Test_art", "1999", "Tester", "https://example.com/111111");

        List<ArtEntity> artEntities = new ArrayList<>();
        artEntities.add(artEntity);
    
        when(museumRepository.findByMuseumId(requestDto.getMuseumId())).thenReturn(museumEntity);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(artRepository.findArtEntitiesByMuseumId(requestDto.getMuseumId())).thenReturn(artEntities);
        when(ttsRepository.findByTtsId(artEntity.getTtsId())).thenThrow(new RuntimeException("Database Error"));

        // When
        ResponseEntity<? super DeleteMuseumResponseDto> response = mainServiceImpl.deleteMuseum(requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void getMuseum_Success(){
        // Given 
        String userId = "TestUser";

        GetMuseumRequestDto requestDto = new GetMuseumRequestDto();
        requestDto.setMuseumId(1L);

        MuseumEntity museumEntity = new MuseumEntity(1L, "Test_Museum", userId, 1L, "9000", "Test_Museum@gmail.com", "9:00", "18:00", "Monday", "This is Test_Museum", "031-123-4567", "https://example.com/123123/123");

        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(museumRepository.findByMuseumId(requestDto.getMuseumId())).thenReturn(museumEntity);

        // When
        ResponseEntity<? super GetMuseumResponseDto> response = mainServiceImpl.getMuseum(requestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SUCCESS, responseBody.getCode());
        assertEquals(ResponseMessage.SUCCESS, responseBody.getMessage());
    }

    @Test
    void getMuseum_Fail_NotFound_Museum(){
        // Given 
        GetMuseumRequestDto requestDto = new GetMuseumRequestDto();
        requestDto.setMuseumId(1L); 

        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(false);

        // When
        ResponseEntity<? super GetMuseumResponseDto> response = mainServiceImpl.getMuseum(requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.MUSEUM_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.MUSEUM_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void getMuseum_Fail_Forbidden(){
        // Given 
        String userId = "NotUser";

        GetMuseumRequestDto requestDto = new GetMuseumRequestDto();
        requestDto.setMuseumId(1L); 

        MuseumEntity museumEntity = new MuseumEntity(1L, "Test_Museum", userId, 1L, "9000", "Test_Museum@gmail.com", "9:00", "18:00", "Monday", "This is Test_Museum", "031-123-4567", "https://example.com/123123/123");

        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(museumRepository.findByMuseumId(requestDto.getMuseumId())).thenReturn(museumEntity);

        // When
        ResponseEntity<? super GetMuseumResponseDto> response = mainServiceImpl.getMuseum(requestDto);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.ID_NOT_MATCHING, responseBody.getCode());
        assertEquals(ResponseMessage.ID_NOT_MATCHING, responseBody.getMessage());
    }

    @Test
    void getMuseum_Fail_DBError(){
        // Given        
        GetMuseumRequestDto requestDto = new GetMuseumRequestDto();
        requestDto.setMuseumId(1L); 

        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenThrow(new RuntimeException("Database Error"));
        // When
        ResponseEntity<? super GetMuseumResponseDto> response = mainServiceImpl.getMuseum(requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void getMuseumList_Success(){
        // When
        ResponseEntity<? super GetMuseumListResponseDto> response = mainServiceImpl.getMuseumList();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SUCCESS, responseBody.getCode());
        assertEquals(ResponseMessage.SUCCESS, responseBody.getMessage());
    }

    @Test 
    void getMuseumList_Fail_DBError() {
        // Given
        String userId = "TestUser";
        when(museumRepository.findAllByUserId(userId)).thenThrow(new RuntimeException("Database Error"));

        // When
        ResponseEntity<? super GetMuseumListResponseDto> response = mainServiceImpl.getMuseumList();

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void saveArt_Success() throws IOException{
        // Given
        String userId = "TestUser";
       
        SaveArtRequestDto requestDto = new SaveArtRequestDto();
        requestDto.setMuseumId(1L);
        requestDto.setArtName("Test_Art");
        requestDto.setArtDescription("This is Test_Art");
        requestDto.setArtYear("1998");
        requestDto.setAuthName("Author");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);
       
        // Mock the S3 client behavior with correct arguments
        String expectedKey = userId + requestDto.getArtName() + originalFileName;
        URL mockUrl = new URL("https://mock-s3-url.com/test_image.jpg");
        when(amazonS3.getUrl(bucket, expectedKey)).thenReturn(mockUrl);

        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        
        // When
        ResponseEntity<? super SaveArtResponseDto> response = mainServiceImpl.saveArt(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SUCCESS, responseBody.getCode());
        assertEquals(ResponseMessage.SUCCESS, responseBody.getMessage());
    }

    @Test
    void saveArt_Fail_NotFound_User() throws IOException{
        // Given
        String userId = "TestUser";
       
        SaveArtRequestDto requestDto = new SaveArtRequestDto();
        requestDto.setMuseumId(1L);
        requestDto.setArtName("Test_Art");
        requestDto.setArtDescription("This is Test_Art");
        requestDto.setArtYear("1998");
        requestDto.setAuthName("Author");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);

        when(userRepository.existsByUserId(userId)).thenReturn(false);
        
        // When
        ResponseEntity<? super SaveArtResponseDto> response = mainServiceImpl.saveArt(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.ID_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.ID_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void saveArt_Fail_NotFound_Museum() throws IOException{
        // Given
        String userId = "TestUser";
       
        SaveArtRequestDto requestDto = new SaveArtRequestDto();
        requestDto.setMuseumId(1L);
        requestDto.setArtName("Test_Art");
        requestDto.setArtDescription("This is Test_Art");
        requestDto.setArtYear("1998");
        requestDto.setAuthName("Author");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);
       
        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(false);
        
        // When
        ResponseEntity<? super SaveArtResponseDto> response = mainServiceImpl.saveArt(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.MUSEUM_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.MUSEUM_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void saveArt_Fail_DBError_User() throws IOException{
        // Given
        String userId = "TestUser";
       
        SaveArtRequestDto requestDto = new SaveArtRequestDto();
        requestDto.setMuseumId(1L);
        requestDto.setArtName("Test_Art");
        requestDto.setArtDescription("This is Test_Art");
        requestDto.setArtYear("1998");
        requestDto.setAuthName("Author");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);

        when(userRepository.existsByUserId(userId)).thenThrow(new RuntimeException("Database Error"));
        
        // When
        ResponseEntity<? super SaveArtResponseDto> response = mainServiceImpl.saveArt(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void saveArt_Fail_DBError_Museum() throws IOException{
        // Given
        String userId = "TestUser";
       
        SaveArtRequestDto requestDto = new SaveArtRequestDto();
        requestDto.setMuseumId(1L);
        requestDto.setArtName("Test_Art");
        requestDto.setArtDescription("This is Test_Art");
        requestDto.setArtYear("1998");
        requestDto.setAuthName("Author");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);

        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenThrow(new RuntimeException("Database Error"));
        // When
        ResponseEntity<? super SaveArtResponseDto> response = mainServiceImpl.saveArt(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void saveArt_Fail_DBError_Save_Spot() throws IOException{
        // Given
        String userId = "TestUser";
       
        SaveArtRequestDto requestDto = new SaveArtRequestDto();
        requestDto.setMuseumId(1L);
        requestDto.setArtName("Test_Art");
        requestDto.setArtDescription("This is Test_Art");
        requestDto.setArtYear("1998");
        requestDto.setAuthName("Author");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);
       
        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(spotRepository.save(any(SpotEntity.class))).thenThrow(new RuntimeException("Database Error"));
        
        // When
        ResponseEntity<? super SaveArtResponseDto> response = mainServiceImpl.saveArt(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void saveArt_Fail_DBError_Save_art() throws IOException{
        // Given
        String userId = "TestUser";
       
        SaveArtRequestDto requestDto = new SaveArtRequestDto();
        requestDto.setMuseumId(1L);
        requestDto.setArtName("Test_Art");
        requestDto.setArtDescription("This is Test_Art");
        requestDto.setArtYear("1998");
        requestDto.setAuthName("Author");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);
       
        // Mock the S3 client behavior with correct arguments
        String expectedKey = userId + requestDto.getArtName() + originalFileName;
        URL mockUrl = new URL("https://mock-s3-url.com/test_image.jpg");
        when(amazonS3.getUrl(bucket, expectedKey)).thenReturn(mockUrl);

        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(artRepository.save(any(ArtEntity.class))).thenThrow(new RuntimeException("Database Error"));
        
        // When
        ResponseEntity<? super SaveArtResponseDto> response = mainServiceImpl.saveArt(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void updateArt_Success() throws IOException{
        // Given
        String userId = "TestUser";
       
        UpdateArtRequestDto requestDto = new UpdateArtRequestDto();
        requestDto.setArtId(1L);
        requestDto.setMuseumId(1L);
        requestDto.setArtName("Test_Art");
        requestDto.setArtDescription("This is Test_Art");
        requestDto.setArtYear("1998");
        requestDto.setAuthName("Author");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);
        
        ArtEntity art = new ArtEntity(1L, 1L, 1L, 1L, 1L, "Test_Art", "This is Test_Art", "1998", "Author", "https://example.com/123123/123");
        
        // Mock the S3 client behavior with correct arguments
        String expectedKey = userId + requestDto.getArtName() + originalFileName;
        URL mockUrl = new URL("https://mock-s3-url.com/test_image.jpg");
        when(amazonS3.getUrl(bucket, expectedKey)).thenReturn(mockUrl);

        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(artRepository.existsByArtId(requestDto.getArtId())).thenReturn(true);
        when(artRepository.findByArtId(requestDto.getArtId())).thenReturn(art);

        // When
        ResponseEntity<? super UpdateArtResponseDto> response = mainServiceImpl.updateArt(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SUCCESS, responseBody.getCode());
        assertEquals(ResponseMessage.SUCCESS, responseBody.getMessage());
    }

    @Test
    void updateArt_Fail_NotFound_User() throws IOException{
        // Given
        String userId = "TestUser";
       
        UpdateArtRequestDto requestDto = new UpdateArtRequestDto();
        requestDto.setArtId(1L);
        requestDto.setMuseumId(1L);
        requestDto.setArtName("Test_Art");
        requestDto.setArtDescription("This is Test_Art");
        requestDto.setArtYear("1998");
        requestDto.setAuthName("Author");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);

        when(userRepository.existsByUserId(userId)).thenReturn(false);

        // When
        ResponseEntity<? super UpdateArtResponseDto> response = mainServiceImpl.updateArt(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.ID_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.ID_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void updateArt_Fail_NotFound_Museum() throws IOException{
        // Given
        String userId = "TestUser";
       
        UpdateArtRequestDto requestDto = new UpdateArtRequestDto();
        requestDto.setArtId(1L);
        requestDto.setMuseumId(1L);
        requestDto.setArtName("Test_Art");
        requestDto.setArtDescription("This is Test_Art");
        requestDto.setArtYear("1998");
        requestDto.setAuthName("Author");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);

        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(false);

        // When
        ResponseEntity<? super UpdateArtResponseDto> response = mainServiceImpl.updateArt(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.MUSEUM_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.MUSEUM_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void updateArt_Fail_NotFound_Art() throws IOException{
        // Given
        String userId = "TestUser";
       
        UpdateArtRequestDto requestDto = new UpdateArtRequestDto();
        requestDto.setArtId(1L);
        requestDto.setMuseumId(1L);
        requestDto.setArtName("Test_Art");
        requestDto.setArtDescription("This is Test_Art");
        requestDto.setArtYear("1998");
        requestDto.setAuthName("Author");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);

        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(artRepository.existsByArtId(requestDto.getArtId())).thenReturn(false);
        // When
        ResponseEntity<? super UpdateArtResponseDto> response = mainServiceImpl.updateArt(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.ART_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.ART_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void updateArt_Fail_DBError_User() throws IOException{
        // Given
        String userId = "TestUser";
       
        UpdateArtRequestDto requestDto = new UpdateArtRequestDto();
        requestDto.setArtId(1L);
        requestDto.setMuseumId(1L);
        requestDto.setArtName("Test_Art");
        requestDto.setArtDescription("This is Test_Art");
        requestDto.setArtYear("1998");
        requestDto.setAuthName("Author");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);
        
        when(userRepository.existsByUserId(userId)).thenThrow(new RuntimeException("Database Error"));
        
        // When
        ResponseEntity<? super UpdateArtResponseDto> response = mainServiceImpl.updateArt(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void updateArt_Fail_DBError_Museum() throws IOException{
        // Given
        String userId = "TestUser";
       
        UpdateArtRequestDto requestDto = new UpdateArtRequestDto();
        requestDto.setArtId(1L);
        requestDto.setMuseumId(1L);
        requestDto.setArtName("Test_Art");
        requestDto.setArtDescription("This is Test_Art");
        requestDto.setArtYear("1998");
        requestDto.setAuthName("Author");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);
        
        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenThrow(new RuntimeException("Database Error"));
        
        // When
        ResponseEntity<? super UpdateArtResponseDto> response = mainServiceImpl.updateArt(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void updateArt_Fail_DBError_Art() throws IOException{
        // Given
        String userId = "TestUser";
       
        UpdateArtRequestDto requestDto = new UpdateArtRequestDto();
        requestDto.setArtId(1L);
        requestDto.setMuseumId(1L);
        requestDto.setArtName("Test_Art");
        requestDto.setArtDescription("This is Test_Art");
        requestDto.setArtYear("1998");
        requestDto.setAuthName("Author");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);
        
        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(artRepository.existsByArtId(requestDto.getArtId())).thenThrow(new RuntimeException("Database Error"));
        
        // When
        ResponseEntity<? super UpdateArtResponseDto> response = mainServiceImpl.updateArt(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void updateArt_Fail_DBError_Save_Spot() throws IOException{
        // Given
        String userId = "TestUser";
       
        UpdateArtRequestDto requestDto = new UpdateArtRequestDto();
        requestDto.setArtId(1L);
        requestDto.setMuseumId(1L);
        requestDto.setArtName("Test_Art");
        requestDto.setArtDescription("This is Test_Art");
        requestDto.setArtYear("1998");
        requestDto.setAuthName("Author");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);
        
        ArtEntity art = new ArtEntity(1L, 1L, 1L, 1L, 1L, "Test_Art", "This is Test_Art", "1998", "Author", "https://example.com/123123/123");
        
        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(artRepository.existsByArtId(requestDto.getArtId())).thenReturn(true);
        when(artRepository.findByArtId(requestDto.getArtId())).thenReturn(art);
        when(spotRepository.save(any(SpotEntity.class))).thenThrow(new RuntimeException("Database Error"));

        // When
        ResponseEntity<? super UpdateArtResponseDto> response = mainServiceImpl.updateArt(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void updateArt_Fail_DBError_Save_Art() throws IOException{
        // Given
        String userId = "TestUser";
       
        UpdateArtRequestDto requestDto = new UpdateArtRequestDto();
        requestDto.setArtId(1L);
        requestDto.setMuseumId(1L);
        requestDto.setArtName("Test_Art");
        requestDto.setArtDescription("This is Test_Art");
        requestDto.setArtYear("1998");
        requestDto.setAuthName("Author");
        requestDto.setLatitude(BigDecimal.valueOf(12.000000));
        requestDto.setLongitude(BigDecimal.valueOf(12.000000));
        requestDto.setLevel("1");
        requestDto.setEdgeLatitude1(BigDecimal.valueOf(8.000000));
        requestDto.setEdgeLatitude2(BigDecimal.valueOf(14.000000));
        requestDto.setEdgeLongitude1(BigDecimal.valueOf(9.000000));
        requestDto.setEdgeLongitude2(BigDecimal.valueOf(15.000000));

        String name = "Test_image";
        String originalFileName = name;
        String contentType = "image/jpeg"; 
        byte[] content = null;

        MultipartFile imageFile = new MockMultipartFile(name, originalFileName, contentType, content);

        ArtEntity art = new ArtEntity(1L, 1L, 1L, 1L, 1L, "Test_Art", "This is Test_Art", "1998", "Author", "https://example.com/123123/123");

        // Mock the S3 client behavior with correct arguments
        String expectedKey = userId + requestDto.getArtName() + originalFileName;
        URL mockUrl = new URL("https://mock-s3-url.com/test_image.jpg");
        when(amazonS3.getUrl(bucket, expectedKey)).thenReturn(mockUrl);
        
        when(userRepository.existsByUserId(userId)).thenReturn(true);
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(artRepository.existsByArtId(requestDto.getArtId())).thenReturn(true);
        when(artRepository.findByArtId(requestDto.getArtId())).thenReturn(art);
        when(artRepository.save(any(ArtEntity.class))).thenThrow(new RuntimeException("Database Error"));

        // When
        ResponseEntity<? super UpdateArtResponseDto> response = mainServiceImpl.updateArt(imageFile, requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void deleteArt_Success(){
        // Given
        DeleteArtRequestDto requestDto = new DeleteArtRequestDto();
        requestDto.setArtId(1L);

        ArtEntity artEntity = new ArtEntity(1L, 1L, 1L, 1L, 1L, "Test_art", "This is Test_art", "1999", "Tester", "https://example.com/111111");
        TtsEntity ttsEntity = new TtsEntity(1L, "http://example.com/bucket");
        
        when(artRepository.existsByArtId(requestDto.getArtId())).thenReturn(true);
        when(artRepository.findByArtId(requestDto.getArtId())).thenReturn(artEntity);
        when(ttsRepository.findByTtsId(artEntity.getTtsId())).thenReturn(ttsEntity);
 
        // When
        ResponseEntity<? super DeleteArtResponseDto> response = mainServiceImpl.deleteArt(requestDto);
 
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SUCCESS, responseBody.getCode());
        assertEquals(ResponseMessage.SUCCESS, responseBody.getMessage());
    }

    @Test
    void deleteArt_Fail_NotFound_Art(){
        // Given
        DeleteArtRequestDto requestDto = new DeleteArtRequestDto();
        requestDto.setArtId(1L);
        
        when(artRepository.existsByArtId(requestDto.getArtId())).thenReturn(false);
        
        // When
        ResponseEntity<? super DeleteArtResponseDto> response = mainServiceImpl.deleteArt(requestDto);
 
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.ART_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.ART_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void deleteArt_Fail_DBError_Art(){
        // Given
        DeleteArtRequestDto requestDto = new DeleteArtRequestDto();
        requestDto.setArtId(1L);
        
        when(artRepository.existsByArtId(requestDto.getArtId())).thenThrow(new RuntimeException("Database Error"));
        
        // When
        ResponseEntity<? super DeleteArtResponseDto> response = mainServiceImpl.deleteArt(requestDto);
 
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test 
    void getArtList_Success(){
        // Given
        String userId = "TestUser";

        GetArtsRequestDto requestDto = new GetArtsRequestDto();
        requestDto.setMuseumId(1L);

        MuseumEntity museumEntity = new MuseumEntity(1L, "Test_Museum", userId, 1L, "9000", "Test_Museum@gmail.com", "9:00", "18:00", "Monday", "This is Test_Museum", "031-123-4567", "https://example.com/123123/123");
        
        ArtEntity artEntity = new ArtEntity(1L, 1L, 1L, 1L, 1L, "Test_art", "This is Test_art", "1999", "Tester", "https://example.com/111111");
        List<ArtEntity> artEntities = new ArrayList<>();
        artEntities.add(artEntity);
        
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(museumRepository.findByMuseumId(requestDto.getMuseumId())).thenReturn(museumEntity);
        when(artRepository.findArtEntitiesByMuseumId(requestDto.getMuseumId())).thenReturn(artEntities);

        // when
        ResponseEntity<? super GetArtsResponseDto> response = mainServiceImpl.getArtList(requestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SUCCESS, responseBody.getCode());
        assertEquals(ResponseMessage.SUCCESS, responseBody.getMessage());
    }

    @Test 
    void getArtList_Fail_NotFound_Museum(){
        // Given
        GetArtsRequestDto requestDto = new GetArtsRequestDto();
        requestDto.setMuseumId(1L);
        
        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(false);

        // when
        ResponseEntity<? super GetArtsResponseDto> response = mainServiceImpl.getArtList(requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.MUSEUM_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.MUSEUM_NOT_FOUND, responseBody.getMessage());
    }

    @Test 
    void getArtList_Fail_Forbidden(){
        // Given
        String userId = "NotUser";

        GetArtsRequestDto requestDto = new GetArtsRequestDto();
        requestDto.setMuseumId(1L);

        MuseumEntity museumEntity = new MuseumEntity(1L, "Test_Museum", userId, 1L, "9000", "Test_Museum@gmail.com", "9:00", "18:00", "Monday", "This is Test_Museum", "031-123-4567", "https://example.com/123123/123");

        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(museumRepository.findByMuseumId(requestDto.getMuseumId())).thenReturn(museumEntity);

        // when
        ResponseEntity<? super GetArtsResponseDto> response = mainServiceImpl.getArtList(requestDto);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.ID_NOT_MATCHING, responseBody.getCode());
        assertEquals(ResponseMessage.ID_NOT_MATCHING, responseBody.getMessage());
    }

    @Test 
    void getArtList_Fail_DBError_Museum(){
        // Given
        GetArtsRequestDto requestDto = new GetArtsRequestDto();
        requestDto.setMuseumId(1L);

        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenThrow(new RuntimeException("Database Error"));

        // when
        ResponseEntity<? super GetArtsResponseDto> response = mainServiceImpl.getArtList(requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void getNearestMuseum_Success(){
        // Given
        GetNearestMuseumRequestDto requestDto = new GetNearestMuseumRequestDto();
        requestDto.setLatitude(BigDecimal.valueOf(37.0000000));
        requestDto.setLongitude(BigDecimal.valueOf(-122.0000000));

        MuseumEntity museumEntity = new MuseumEntity(1L, "Test_Museum", "TestUser", 1L, "9000", "Test_Museum@gmail.com", "9:00", "18:00", "Monday", "This is Test_Museum", "031-123-4567", "https://example.com/123123/123");

        LocationEntity locationEntity = new LocationEntity(1L, BigDecimal.valueOf(37.0000000),BigDecimal.valueOf(127.0000000),"1", BigDecimal.valueOf(37.1230000),BigDecimal.valueOf(-122.4560000),BigDecimal.valueOf(37.4560000),BigDecimal.valueOf(-122.7890000));
        List<LocationEntity> museumLocationEntities = new ArrayList<>();
        museumLocationEntities.add(locationEntity);

        when(locationRepository.findAll()).thenReturn(museumLocationEntities);
        when(museumRepository.findFirstByLocationId(anyLong())).thenReturn(museumEntity);
        // When
        ResponseEntity<? super GetNearestMuseumResponseDto> response = mainServiceImpl.getNearestMuseum(requestDto);


        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SUCCESS, responseBody.getCode());
        assertEquals(ResponseMessage.SUCCESS, responseBody.getMessage());
    }
    @Test
    void getNearestMuseum_Fail_NotFound_Museum(){
        // Given
        GetNearestMuseumRequestDto requestDto = new GetNearestMuseumRequestDto();
        requestDto.setLatitude(BigDecimal.valueOf(37.0000000));
        requestDto.setLongitude(BigDecimal.valueOf(127.0000000));


        // When
        ResponseEntity<? super GetNearestMuseumResponseDto> response = mainServiceImpl.getNearestMuseum(requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.MUSEUM_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.MUSEUM_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void getNearestMuseum_Fail_DBError(){
        // Given
        GetNearestMuseumRequestDto requestDto = new GetNearestMuseumRequestDto();
        requestDto.setLatitude(BigDecimal.valueOf(37.0000000));
        requestDto.setLongitude(BigDecimal.valueOf(127.0000000));

        when(museumRepository.findFirstByLocationId(anyLong())).thenThrow(new RuntimeException("Database Error"));

        // When
        ResponseEntity<? super GetNearestMuseumResponseDto> response = mainServiceImpl.getNearestMuseum(requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void getNearestNArtList_Success(){
        // Given
        GetNearestNArtsRequestDto requestDto = new GetNearestNArtsRequestDto();
        requestDto.setAmount(1);
        requestDto.setLatitude(BigDecimal.valueOf(37.0000000));
        requestDto.setLongitude(BigDecimal.valueOf(-122.0000000));
        requestDto.setMuseumId(1L);

        ArtEntity artEntity = new ArtEntity(1L, requestDto.getMuseumId(), 1L, 1L, 1L, "Test_art", "This is Test_art", "1999", "Tester", "https://example.com/111111");
        List<ArtEntity> artEntities = new ArrayList<>();
        artEntities.add(artEntity);

        SpotEntity spotEntity = new SpotEntity(1L, BigDecimal.valueOf(37.0000000),BigDecimal.valueOf(127.0000000),"1", BigDecimal.valueOf(37.1230000),BigDecimal.valueOf(-122.4560000),BigDecimal.valueOf(37.4560000),BigDecimal.valueOf(-122.7890000));

        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(true);
        when(artRepository.findAllByMuseumId(requestDto.getMuseumId())).thenReturn(artEntities);
        when(spotRepository.findBySpotId(anyLong())).thenReturn(spotEntity);

        // When
        ResponseEntity<? super GetNearestNArtsResponseDto> response = mainServiceImpl.getNearestNArtList(requestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SUCCESS, responseBody.getCode());
        assertEquals(ResponseMessage.SUCCESS, responseBody.getMessage());
    }

    @Test
    void getNearestNArtList_Fail_NotFound_Museum(){
        // Given
        GetNearestNArtsRequestDto requestDto = new GetNearestNArtsRequestDto();
        requestDto.setAmount(1);
        requestDto.setLatitude(BigDecimal.valueOf(37.0000000));
        requestDto.setLongitude(BigDecimal.valueOf(-122.0000000));
        requestDto.setMuseumId(1L);

        ArtEntity artEntity = new ArtEntity(1L, requestDto.getMuseumId(), 1L, 1L, 1L, "Test_art", "This is Test_art", "1999", "Tester", "https://example.com/111111");
        List<ArtEntity> artEntities = new ArrayList<>();
        artEntities.add(artEntity);

        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenReturn(false);
        
        // When
        ResponseEntity<? super GetNearestNArtsResponseDto> response = mainServiceImpl.getNearestNArtList(requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.MUSEUM_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.MUSEUM_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void getNearestNArtList_Fail_DBError_Museum(){
        // Given
        GetNearestNArtsRequestDto requestDto = new GetNearestNArtsRequestDto();
        requestDto.setAmount(1);
        requestDto.setLatitude(BigDecimal.valueOf(37.0000000));
        requestDto.setLongitude(BigDecimal.valueOf(-122.0000000));
        requestDto.setMuseumId(1L);

        ArtEntity artEntity = new ArtEntity(1L, requestDto.getMuseumId(), 1L, 1L, 1L, "Test_art", "This is Test_art", "1999", "Tester", "https://example.com/111111");
        List<ArtEntity> artEntities = new ArrayList<>();
        artEntities.add(artEntity);

        when(museumRepository.existsByMuseumId(requestDto.getMuseumId())).thenThrow(new RuntimeException("Database Error"));

        // When
        ResponseEntity<? super GetNearestNArtsResponseDto> response = mainServiceImpl.getNearestNArtList(requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void getArt_Success(){
        // Given
        GetArtRequestDto requestDto = new GetArtRequestDto();
        requestDto.setArtId(1L);

        ArtEntity artEntity = new ArtEntity(1L, 1L, 1L, 1L, 1L, "Test_art", "This is Test_art", "1999", "Tester", "https://example.com/111111");

        when(artRepository.existsByArtId(requestDto.getArtId())).thenReturn(true);
        when(artRepository.findByArtId(requestDto.getArtId())).thenReturn(artEntity);

        // When
        ResponseEntity<? super GetArtResponseDto> response = mainServiceImpl.getArt(requestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SUCCESS, responseBody.getCode());
        assertEquals(ResponseMessage.SUCCESS, responseBody.getMessage());
    }

    @Test
    void getArt_Fail_NotFound_Art(){
        // Given
        GetArtRequestDto requestDto = new GetArtRequestDto();
        requestDto.setArtId(1L);

        when(artRepository.existsByArtId(requestDto.getArtId())).thenReturn(false);

        // When
        ResponseEntity<? super GetArtResponseDto> response = mainServiceImpl.getArt(requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.ART_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.ART_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void getArt_Fail_DBError(){
        // Given
        GetArtRequestDto requestDto = new GetArtRequestDto();
        requestDto.setArtId(1L);

        when(artRepository.existsByArtId(requestDto.getArtId())).thenThrow(new RuntimeException("Database Error"));

        // When
        ResponseEntity<? super GetArtResponseDto> response = mainServiceImpl.getArt(requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void getArtByHash_Success(){
        // Given
        GetArtByHashRequestDto requestDto = new GetArtByHashRequestDto();
        requestDto.setQrcodeHashKey("TestHashkey");

        QrcodeEntity qrcodeEntity = new QrcodeEntity(1L, "TestArtQrcode", "TestHashkey");
        ArtEntity artEntity = new ArtEntity(1L, 1L, 1L, 1L, 1L, "Test_art", "This is Test_art", "1999", "Tester", "https://example.com/111111");

        when(qrcodeRepository.findByQrcodeHashkey(requestDto.getQrcodeHashKey())).thenReturn(qrcodeEntity);
        when(artRepository.existsByQrcodeId(qrcodeEntity.getQrcodeId())).thenReturn(true);
        when(artRepository.findByQrcodeId(qrcodeEntity.getQrcodeId())).thenReturn(artEntity);

        // When
        ResponseEntity<? super GetArtByHashResponseDto> response = mainServiceImpl.getArtByHash(requestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SUCCESS, responseBody.getCode());
        assertEquals(ResponseMessage.SUCCESS, responseBody.getMessage());
    }

    @Test
    void getArtByHash_Fail_NotFound(){
        // Given
        GetArtByHashRequestDto requestDto = new GetArtByHashRequestDto();
        requestDto.setQrcodeHashKey("TestHashkey");

        QrcodeEntity qrcodeEntity = new QrcodeEntity(1L, "TestArtQrcode", "TestHashkey");

        when(qrcodeRepository.findByQrcodeHashkey(requestDto.getQrcodeHashKey())).thenReturn(qrcodeEntity);
        when(artRepository.existsByQrcodeId(qrcodeEntity.getQrcodeId())).thenReturn(false);

        // When
        ResponseEntity<? super GetArtByHashResponseDto> response = mainServiceImpl.getArtByHash(requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.ART_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.ART_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void getArtByHash_Fail_DBError_Qrcode(){
        // Given
        GetArtByHashRequestDto requestDto = new GetArtByHashRequestDto();
        requestDto.setQrcodeHashKey("TestHashkey");

        when(qrcodeRepository.findByQrcodeHashkey(requestDto.getQrcodeHashKey())).thenThrow(new RuntimeException("Database Error"));

        // When
        ResponseEntity<? super GetArtByHashResponseDto> response = mainServiceImpl.getArtByHash(requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void getArtByHash_Fail_DBError_Art(){
        // Given
        GetArtByHashRequestDto requestDto = new GetArtByHashRequestDto();
        requestDto.setQrcodeHashKey("TestHashkey");

        QrcodeEntity qrcodeEntity = new QrcodeEntity(1L, "TestArtQrcode", "TestHashkey");
    
        when(qrcodeRepository.findByQrcodeHashkey(requestDto.getQrcodeHashKey())).thenReturn(qrcodeEntity);
        when(artRepository.existsByQrcodeId(qrcodeEntity.getQrcodeId())).thenThrow(new RuntimeException("Database Error"));

        // When
        ResponseEntity<? super GetArtByHashResponseDto> response = mainServiceImpl.getArtByHash(requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void getTts_Success(){
        // Given
        GetTtsRequestDto requestDto = new GetTtsRequestDto();
        requestDto.setTtsId(1L);

        TtsEntity ttsEntity = new TtsEntity(1L, "https://example.com/tts");

        when(ttsRepository.existsByTtsId(requestDto.getTtsId())).thenReturn(true);
        when(ttsRepository.findByTtsId(requestDto.getTtsId())).thenReturn(ttsEntity);

        // When
        ResponseEntity<? super GetTtsResponseDto> response = mainServiceImpl.getTts(requestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SUCCESS, responseBody.getCode());
        assertEquals(ResponseMessage.SUCCESS, responseBody.getMessage());
    }

    @Test
    void getTts_Fail_NotFound(){
        // Given
        GetTtsRequestDto requestDto = new GetTtsRequestDto();
        requestDto.setTtsId(1L);

        when(ttsRepository.existsByTtsId(requestDto.getTtsId())).thenReturn(false);

        // When
        ResponseEntity<? super GetTtsResponseDto> response = mainServiceImpl.getTts(requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.TTS_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.TTS_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void getTts_Fail_DBError(){
        // Given
        GetTtsRequestDto requestDto = new GetTtsRequestDto();
        requestDto.setTtsId(1L);

        when(ttsRepository.existsByTtsId(requestDto.getTtsId())).thenThrow(new RuntimeException("Database Error"));

        // When
        ResponseEntity<? super GetTtsResponseDto> response = mainServiceImpl.getTts(requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }

    @Test
    void getQrcode_Success(){
        // Given
        GetQrcodeRequestDto requestDto = new GetQrcodeRequestDto();
        requestDto.setQrcodeId(1L);

        QrcodeEntity qrcodeEntity = new QrcodeEntity(1L, "TestArtQrcode", "TestHashkey");
    
        when(qrcodeRepository.existsByQrcodeId(requestDto.getQrcodeId())).thenReturn(true);
        when(qrcodeRepository.findByQrcodeId(requestDto.getQrcodeId())).thenReturn(qrcodeEntity);
        // When
        ResponseEntity<? super GetQrcodeResponseDto> response = mainServiceImpl.getQrcode(requestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.SUCCESS, responseBody.getCode());
        assertEquals(ResponseMessage.SUCCESS, responseBody.getMessage());
    }

    @Test
    void getQrcode_Fail_NotFound(){
        // Given
        GetQrcodeRequestDto requestDto = new GetQrcodeRequestDto();
        requestDto.setQrcodeId(1L);
    
        when(qrcodeRepository.existsByQrcodeId(requestDto.getQrcodeId())).thenReturn(false);
        // When
        ResponseEntity<? super GetQrcodeResponseDto> response = mainServiceImpl.getQrcode(requestDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.QR_NOT_FOUND, responseBody.getCode());
        assertEquals(ResponseMessage.QR_NOT_FOUND, responseBody.getMessage());
    }

    @Test
    void getQrcode_Fail_DBError(){
        // Given
        GetQrcodeRequestDto requestDto = new GetQrcodeRequestDto();
        requestDto.setQrcodeId(1L);

        when(qrcodeRepository.existsByQrcodeId(requestDto.getQrcodeId())).thenThrow(new RuntimeException("Database Error"));

        // When
        ResponseEntity<? super GetQrcodeResponseDto> response = mainServiceImpl.getQrcode(requestDto);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ResponseDto);
        ResponseDto responseBody = (ResponseDto) response.getBody();
        assertEquals(ResponseCode.DATABASE_ERROR, responseBody.getCode());
        assertEquals(ResponseMessage.DATABASE_ERROR, responseBody.getMessage());
    }
}

