package info.dexplore.dexplore.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import info.dexplore.dexplore.dto.request.main.GetMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.SaveArtRequestDto;
import info.dexplore.dexplore.dto.request.main.UpdateMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.SaveMuseumRequestDto;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.dto.response.main.*;
import info.dexplore.dexplore.entity.ArtEntity;
import info.dexplore.dexplore.entity.LocationEntity;
import info.dexplore.dexplore.entity.MuseumEntity;
import info.dexplore.dexplore.provider.QrcodeProvider;
import info.dexplore.dexplore.provider.TtsProvider;
import info.dexplore.dexplore.repository.ArtRepository;
import info.dexplore.dexplore.repository.LocationRepository;
import info.dexplore.dexplore.repository.MuseumRepository;
import info.dexplore.dexplore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final MuseumRepository museumRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final ArtRepository artRepository;

    private final QrcodeProvider qrcodeProvider;
    private final TtsProvider ttsProvider;

    /**
     * museum 등록하기
     * @param requestDto
     * @return validationFailed, databaseError, duplicatedMuseumName, idNotFound, success
     */
    @Override
    @Transactional
    public ResponseEntity<? super SaveMuseumResponseDto> saveMuseum(MultipartFile imageFile, SaveMuseumRequestDto requestDto) {

        try {

            //유저 id 확인
            String userId = findUserIdFromJwt();
            log.info("[saveMuseum]: 추출한 id:{}", userId);


            boolean exists = userRepository.existsByUserId(userId);
            if(!exists) {
                return SaveMuseumResponseDto.idNotFound();
            }


            String museumName = requestDto.getMuseumName();

            //박물관 이름 중복 확인
            exists = museumRepository.existsByMuseumName(museumName);
            if(exists) {
                return SaveMuseumResponseDto.duplicatedMuseumName();
            }

            String entPrice = requestDto.getEntPrice();
            String museumEmail = requestDto.getMuseumEmail();
            String startTime = requestDto.getStartTime();
            String endTime = requestDto.getEndTime();
            String closingDay = requestDto.getClosingDay();
            String description = requestDto.getDescription();
            String phone = requestDto.getPhone();

            BigDecimal latitude = requestDto.getLatitude();
            BigDecimal longitude = requestDto.getLongitude();
            String level = requestDto.getLevel();
            BigDecimal edgeLatitude1 = requestDto.getEdgeLatitude1();
            BigDecimal edgeLongitude1 = requestDto.getEdgeLongitude1();
            BigDecimal edgeLatitude2 = requestDto.getEdgeLatitude2();
            BigDecimal edgeLongitude2 = requestDto.getEdgeLongitude2();

            //S3 버킷 저장 및 img_url 생성
            String fileName = userId + museumName + imageFile.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageFile.getSize());
            metadata.setContentType(imageFile.getContentType());

            amazonS3.putObject(bucket, fileName, imageFile.getInputStream(), metadata);

            String imgUrl = amazonS3.getUrl(bucket, fileName).toString();

            LocationEntity location = new LocationEntity(
                    null,
                    latitude,
                    longitude,
                    level,
                    edgeLatitude1,
                    edgeLongitude1,
                    edgeLatitude2,
                    edgeLongitude2
            );

            locationRepository.save(location);
            Long locationId = location.getLocationId();

            MuseumEntity museum = new MuseumEntity(
                    null,
                    museumName,
                    userId,
                    locationId,
                    entPrice,
                    museumEmail,
                    startTime,
                    endTime,
                    closingDay,
                    description,
                    phone,
                    imgUrl
            );

            museumRepository.save(museum);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ResponseDto.success();
    }

    /**
     * 박물관 정보 수정하기
     * @param requestDto
     * @return validationFailed, databaseError, idNotFound, success
     */
    @Override
    @Transactional
    public ResponseEntity<? super UpdateMuseumResponseDto> updateMuseum(MultipartFile imageFile, UpdateMuseumRequestDto requestDto) {
        try {

            //유저 id 확인
            String userId = findUserIdFromJwt();
            log.info("[saveMuseum]: 추출한 id:{}", userId);

            boolean exists = userRepository.existsByUserId(userId);
            if(!exists) {
                return UpdateMuseumResponseDto.idNotFound();
            }

            String museumName = requestDto.getMuseumName();

            //박물관 id 존재여부 확인
            exists = museumRepository.existsByMuseumId(requestDto.getMuseumId());
            if(!exists) {
                return UpdateMuseumResponseDto.museumNotFound();
            }

            MuseumEntity museum = museumRepository.findByMuseumId(requestDto.getMuseumId());
            LocationEntity location = locationRepository.findByLocationId(museum.getLocationId());

            String entPrice = requestDto.getEntPrice();
            String museumEmail = requestDto.getMuseumEmail();
            String startTime = requestDto.getStartTime();
            String endTime = requestDto.getEndTime();
            String closingDay = requestDto.getClosingDay();
            String description = requestDto.getDescription();
            String phone = requestDto.getPhone();

            // 해당 imgUrl의 이미지 제거
            String imgUrl = museum.getImgUrl();
            URL url = new URL(imgUrl);
            String[] parts = url.getPath().split("/", 2);
            String key = parts[1]; // 파일 키(경로) 추출

            // 버킷에서 파일 삭제
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));

            //S3 버킷 저장 및 새 img_url 생성
            String fileName = userId + museumName + imageFile.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageFile.getSize());
            metadata.setContentType(imageFile.getContentType());

            amazonS3.putObject(bucket, fileName, imageFile.getInputStream(), metadata);

            String newImgUrl = amazonS3.getUrl(bucket, fileName).toString();

            BigDecimal latitude = requestDto.getLatitude();
            BigDecimal longitude = requestDto.getLongitude();
            String level = requestDto.getLevel();
            BigDecimal edgeLatitude1 = requestDto.getEdgeLatitude1();
            BigDecimal edgeLongitude1 = requestDto.getEdgeLongitude1();
            BigDecimal edgeLatitude2 = requestDto.getEdgeLatitude2();
            BigDecimal edgeLongitude2 = requestDto.getEdgeLongitude2();

            LocationEntity newLocation = new LocationEntity(
                    location.getLocationId(),
                    latitude,
                    longitude,
                    level,
                    edgeLatitude1,
                    edgeLongitude1,
                    edgeLatitude2,
                    edgeLongitude2
            );

            MuseumEntity newMuseum = new MuseumEntity(
                    museum.getMuseumId(),
                    museumName,
                    userId,
                    newLocation.getLocationId(),
                    entPrice,
                    museumEmail,
                    startTime,
                    endTime,
                    closingDay,
                    description,
                    phone,
                    newImgUrl
            );

            locationRepository.save(newLocation);
            museumRepository.save(newMuseum);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ResponseDto.success();
    }


    /**
     * museum_id로 단일 박물관 찾기
     * @param requestDto
     * @return validationFailed, databaseError, museumNotFound, idNotMatching, success
     */
    @Override
    public ResponseEntity<? super GetMuseumResponseDto> getMuseum(GetMuseumRequestDto requestDto) {

        MuseumEntity museum;

        try {

            Long museumId = requestDto.getMuseumId();
            boolean exists = museumRepository.existsByMuseumId(museumId);

            if (!exists) {
                return GetMuseumResponseDto.museumNotFound();
            }
            else {
                museum = museumRepository.findByMuseumId(museumId);

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                    String userId = authentication.getName();

                    //요청한 userid와 박물관 오너id가 매칭되는지 확인
                    if(!museum.getUserId().equals(userId)) {
                        return GetMuseumResponseDto.idNotMatching();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return GetMuseumResponseDto.success(museum);
    }

    /**
     * user_id로 모든 박물관 찾기
     * @return validationFailed, databaseError, success
     */
    @Override
    public ResponseEntity<? super GetMuseumListResponseDto> getMuseumList() {

        List<MuseumEntity> allByUserId;

        try {
            String userId = "Default User Id...";
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                userId = authentication.getName();
            }

            allByUserId = museumRepository.findAllByUserId(userId);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }

        return GetMuseumListResponseDto.success(allByUserId);
    }


    /**
     * museum_id의 박물관에 작품정보 등록
     * @return validationFailed, databaseError, idNotFound, museumNotFound, success
     */
    @Override
    @Transactional
    public ResponseEntity<? super SaveArtResponseDto> saveArt(MultipartFile imageFile, SaveArtRequestDto requestDto) {

        try {
            String userId = findUserIdFromJwt();

            boolean exists = userRepository.existsByUserId(userId);
            if(!exists) {
                return SaveArtResponseDto.idNotFound();
            }

            Long museumId = requestDto.getMuseumId();
            exists = museumRepository.existsByMuseumId(museumId);
            if(!exists) {
                return SaveArtResponseDto.museumNotFound();
            }

            //미술품의 location 정보 생성
            BigDecimal latitude = requestDto.getLatitude();
            BigDecimal longitude = requestDto.getLongitude();
            String level = requestDto.getLevel();
            BigDecimal edgeLatitude1 = requestDto.getEdgeLatitude1();
            BigDecimal edgeLongitude1 = requestDto.getEdgeLongitude1();
            BigDecimal edgeLatitude2 = requestDto.getEdgeLatitude2();
            BigDecimal edgeLongitude2 = requestDto.getEdgeLongitude2();

            LocationEntity location = new LocationEntity(
                    null,
                    latitude,
                    longitude,
                    level,
                    edgeLatitude1,
                    edgeLongitude1,
                    edgeLatitude2,
                    edgeLongitude2
            );

            locationRepository.save(location);
            Long locationId = location.getLocationId();

            String artName = requestDto.getArtName();
            String artDescription = requestDto.getArtDescription();
            String artYear = requestDto.getArtYear();
            String authName = requestDto.getAuthName();

            //S3 버킷 저장 및 img_url 생성
            String fileName = userId + artName + imageFile.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageFile.getSize());
            metadata.setContentType(imageFile.getContentType());

            amazonS3.putObject(bucket, fileName, imageFile.getInputStream(), metadata);

            String imgUrl = amazonS3.getUrl(bucket, fileName).toString();
            Long qrcodeId = qrcodeProvider.generateQrcode(artName);
            Long ttsId = ttsProvider.generateTts(artName, artDescription);
            ArtEntity art = new ArtEntity(
                    null,
                    museumId,
                    locationId,
                    qrcodeId,
                    ttsId,
                    artName,
                    artDescription,
                    artYear,
                    authName,
                    imgUrl
            );

            artRepository.save(art);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ResponseDto.success();
    }

    /**
     * 요청자의 JWT에서 userId 추출
     * @return 요청자의 userId
     */
    private String findUserIdFromJwt() {
        String userId = "Default user id...";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            userId = authentication.getName();
        }
        return userId;
    }

}
