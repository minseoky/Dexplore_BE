package info.dexplore.dexplore.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import info.dexplore.dexplore.dto.request.main.admin.*;
import info.dexplore.dexplore.dto.request.main.user.*;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.dto.response.main.admin.*;
import info.dexplore.dexplore.dto.response.main.user.*;
import info.dexplore.dexplore.entity.*;
import info.dexplore.dexplore.provider.QrcodeProvider;
import info.dexplore.dexplore.provider.TtsProvider;
import info.dexplore.dexplore.repository.*;
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
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private static final double EARTH_RADIUS = 6371.0;
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final MuseumRepository museumRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final ArtRepository artRepository;
    private final SpotRepository spotRepository;
    private final QrcodeRepository qrcodeRepository;
    private final TtsRepository ttsRepository;
    private final FootprintRepository footprintRepository;
    private final BookmarkRepository bookmarkRepository;

    private final QrcodeProvider qrcodeProvider;
    private final TtsProvider ttsProvider;

    /**
     * museum 등록하기
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
     * @return validationFailed, databaseError, idNotMatching, idNotFound, success
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

            if(!museum.getUserId().equals(userId)) {
                return UpdateMuseumResponseDto.idNotMatching();
            }

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

    @Override
    @Transactional
    public ResponseEntity<? super UpdateMuseumResponseDto> updateMuseumWithNoImg(UpdateMuseumRequestDto requestDto) {
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

            if(!museum.getUserId().equals(userId)) {
                return UpdateMuseumResponseDto.idNotMatching();
            }

            String entPrice = requestDto.getEntPrice();
            String museumEmail = requestDto.getMuseumEmail();
            String startTime = requestDto.getStartTime();
            String endTime = requestDto.getEndTime();
            String closingDay = requestDto.getClosingDay();
            String description = requestDto.getDescription();
            String phone = requestDto.getPhone();
            String imgUrl = museum.getImgUrl();

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
                    imgUrl
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
     * 박물관과 포함된 모든 작품정보 삭제
     * @return validationFailed, databaseError, museumNotFound, idNotMatching, success
     */
    @Override
    @Transactional
    public ResponseEntity<? super DeleteMuseumResponseDto> deleteMuseum(DeleteMuseumRequestDto requestDto) {

        try {

            Long museumId = requestDto.getMuseumId();

            MuseumEntity museum = museumRepository.findByMuseumId(museumId);

            //유저 id 확인
            String userId = findUserIdFromJwt();

            if(!userId.equals(museum.getUserId())) {
                return DeleteMuseumResponseDto.idNotMatching();
            }

            boolean exists = museumRepository.existsByMuseumId(museumId);
            if(!exists) {
                return DeleteMuseumResponseDto.museumNotFound();
            }


            List<ArtEntity> artEntities = artRepository.findArtEntitiesByMuseumId(museumId);

            for (ArtEntity artEntity : artEntities) {
                Long artId = artEntity.getArtId();
                // spot qr tts 삭제
                Long qrcodeId = artEntity.getQrcodeId();
                Long spotId = artEntity.getSpotId();
                Long ttsId = artEntity.getTtsId();
                String ttsUrl = ttsRepository.findByTtsId(ttsId).getBucketUrl();

                // art 이미지 S3 버킷에서 삭제(iter)
                String imgUrl = artEntity.getImgUrl();
                URL url = new URL(imgUrl);
                String[] parts = url.getPath().split("/", 2);
                String key = parts[1]; // 파일 키(경로) 추출
                amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));

                footprintRepository.deleteAllByArtId(artId);
                bookmarkRepository.deleteAllByArtId(artId);

                artRepository.deleteByArtId(artId);

                qrcodeRepository.deleteByQrcodeId(qrcodeId);
                spotRepository.deleteBySpotId(spotId);
                // 버킷에서 음성파일 삭제
                ttsProvider.deleteTts(ttsUrl);
                ttsRepository.deleteByTtsId(ttsId);


            }

            // 박물관 삭제
            museumRepository.deleteByMuseumId(museumId);
            // 박물관 location 정보 삭제
            Long locationId = museum.getLocationId();

            // 박물관 이미지 S3 버킷에서 삭제
            String imgUrl = museum.getImgUrl();
            URL url = new URL(imgUrl);
            String[] parts = url.getPath().split("/", 2);
            String key = parts[1]; // 파일 키(경로) 추출
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));

            locationRepository.deleteByLocationId(locationId);



        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ResponseDto.success();
    }


    /**
     * museum_id로 단일 박물관 찾기
     * @return validationFailed, databaseError, museumNotFound, idNotMatching, success
     */


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

            //미술품의 spot 정보 생성
            BigDecimal latitude = requestDto.getLatitude();
            BigDecimal longitude = requestDto.getLongitude();
            String level = requestDto.getLevel();
            BigDecimal edgeLatitude1 = requestDto.getEdgeLatitude1();
            BigDecimal edgeLongitude1 = requestDto.getEdgeLongitude1();
            BigDecimal edgeLatitude2 = requestDto.getEdgeLatitude2();
            BigDecimal edgeLongitude2 = requestDto.getEdgeLongitude2();

            SpotEntity spot = new SpotEntity(
                    null,
                    latitude,
                    longitude,
                    level,
                    edgeLatitude1,
                    edgeLongitude1,
                    edgeLatitude2,
                    edgeLongitude2
            );

            spotRepository.save(spot);
            Long spotId = spot.getSpotId();

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
                    spotId,
                    qrcodeId,
                    ttsId,
                    artName,
                    artDescription,
                    artYear,
                    authName,
                    imgUrl
            );

            artRepository.save(art);

            return SaveArtResponseDto.success(qrcodeId);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }

    }

    /**
     * museum_id의 박물관의 작품정보 수정
     * @return validationFailed, databaseError, idNotFound, idNotMatching, artNotFound, museumNotFound, success
     */
    @Override
    @Transactional
    public ResponseEntity<? super UpdateArtResponseDto> updateArt(MultipartFile imageFile, UpdateArtRequestDto requestDto) {
        try {

            String userId = findUserIdFromJwt();

            boolean exists = userRepository.existsByUserId(userId);
            if(!exists) {
                return UpdateArtResponseDto.idNotFound();
            }

            Long museumId = requestDto.getMuseumId();
            exists = museumRepository.existsByMuseumId(museumId);
            if(!exists) {
                return UpdateArtResponseDto.museumNotFound();
            }

            Long artId = requestDto.getArtId();
            exists = artRepository.existsByArtId(artId);
            if(!exists) {
                return UpdateArtResponseDto.artNotFound();
            }
            //미술품 찾아오기
            ArtEntity art = artRepository.findByArtId(requestDto.getArtId());

            MuseumEntity museum = museumRepository.findByMuseumId(museumId);
            if(!museum.getUserId().equals(userId)) {
                return UpdateArtResponseDto.idNotMatching();
            }
            //미술품의 spot 정보 생성
            BigDecimal latitude = requestDto.getLatitude();
            BigDecimal longitude = requestDto.getLongitude();
            String level = requestDto.getLevel();
            BigDecimal edgeLatitude1 = requestDto.getEdgeLatitude1();
            BigDecimal edgeLongitude1 = requestDto.getEdgeLongitude1();
            BigDecimal edgeLatitude2 = requestDto.getEdgeLatitude2();
            BigDecimal edgeLongitude2 = requestDto.getEdgeLongitude2();

            SpotEntity spot = new SpotEntity(
                    art.getSpotId(),
                    latitude,
                    longitude,
                    level,
                    edgeLatitude1,
                    edgeLongitude1,
                    edgeLatitude2,
                    edgeLongitude2
            );

            spotRepository.save(spot);

            String artName = requestDto.getArtName();
            String artDescription = requestDto.getArtDescription();
            String artYear = requestDto.getArtYear();
            String authName = requestDto.getAuthName();


            Long oldQrcodeId = art.getQrcodeId();
            qrcodeRepository.deleteByQrcodeId(oldQrcodeId);
            Long newQrcodeId = qrcodeProvider.generateQrcode(artName);

            // 해당 imgUrl의 이미지 제거
            String imgUrl = art.getImgUrl();
            URL url = new URL(imgUrl);
            String[] parts = url.getPath().split("/", 2);
            String key = parts[1]; // 파일 키(경로) 추출

            // 버킷에서 파일 삭제
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));

            //S3 버킷 저장 및 새 img_url 생성
            String fileName = userId + artName + imageFile.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageFile.getSize());
            metadata.setContentType(imageFile.getContentType());

            amazonS3.putObject(bucket, fileName, imageFile.getInputStream(), metadata);

            String newImgUrl = amazonS3.getUrl(bucket, fileName).toString();

            ArtEntity newArt = new ArtEntity(
                    art.getArtId(),
                    art.getMuseumId(),
                    art.getSpotId(),
                    newQrcodeId,
                    art.getTtsId(),
                    artName,
                    artDescription,
                    artYear,
                    authName,
                    newImgUrl
            );
            footprintRepository.deleteAllByArtId(artId);
            bookmarkRepository.deleteAllByArtId(artId);

            artRepository.save(newArt);


            //description이 변경된경우
            if(art.getArtDescription().equals(artDescription)) {
                ttsProvider.updateTts(artName, artDescription, art.getTtsId());
            }

            return UpdateArtResponseDto.success(newQrcodeId);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<? super UpdateArtResponseDto> updateArtWithNoImg(UpdateArtRequestDto requestDto) {
        try {

            String userId = findUserIdFromJwt();

            boolean exists = userRepository.existsByUserId(userId);
            if(!exists) {
                return UpdateArtResponseDto.idNotFound();
            }

            Long museumId = requestDto.getMuseumId();
            exists = museumRepository.existsByMuseumId(museumId);
            if(!exists) {
                return UpdateArtResponseDto.museumNotFound();
            }

            Long artId = requestDto.getArtId();
            exists = artRepository.existsByArtId(artId);
            if(!exists) {
                return UpdateArtResponseDto.artNotFound();
            }
            //미술품 찾아오기
            ArtEntity art = artRepository.findByArtId(requestDto.getArtId());

            MuseumEntity museum = museumRepository.findByMuseumId(museumId);
            if(!museum.getUserId().equals(userId)) {
                return UpdateArtResponseDto.idNotMatching();
            }

            //미술품의 spot 정보 생성
            BigDecimal latitude = requestDto.getLatitude();
            BigDecimal longitude = requestDto.getLongitude();
            String level = requestDto.getLevel();
            BigDecimal edgeLatitude1 = requestDto.getEdgeLatitude1();
            BigDecimal edgeLongitude1 = requestDto.getEdgeLongitude1();
            BigDecimal edgeLatitude2 = requestDto.getEdgeLatitude2();
            BigDecimal edgeLongitude2 = requestDto.getEdgeLongitude2();

            SpotEntity spot = new SpotEntity(
                    art.getSpotId(),
                    latitude,
                    longitude,
                    level,
                    edgeLatitude1,
                    edgeLongitude1,
                    edgeLatitude2,
                    edgeLongitude2
            );

            spotRepository.save(spot);

            String artName = requestDto.getArtName();
            String artDescription = requestDto.getArtDescription();
            String artYear = requestDto.getArtYear();
            String authName = requestDto.getAuthName();
            String imgUrl = art.getImgUrl();

            Long oldQrcodeId = art.getQrcodeId();
            qrcodeRepository.deleteByQrcodeId(oldQrcodeId);
            Long newQrcodeId = qrcodeProvider.generateQrcode(artName);

            ArtEntity newArt = new ArtEntity(
                    art.getArtId(),
                    art.getMuseumId(),
                    art.getSpotId(),
                    newQrcodeId,
                    art.getTtsId(),
                    artName,
                    artDescription,
                    artYear,
                    authName,
                    imgUrl
            );

            artRepository.save(newArt);

            //description이 변경된경우
            if(art.getArtDescription().equals(artDescription)) {
                ttsProvider.updateTts(artName, artDescription, art.getTtsId());
            }


            return UpdateArtResponseDto.success(newQrcodeId);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }

    }

    /**
     * 작품정보 삭제
     * @return validationFailed, databaseError, artNotFound, idNotMatching, success
     */
    @Override
    @Transactional
    public ResponseEntity<? super DeleteArtResponseDto> deleteArt(DeleteArtRequestDto requestDto) {

        try {

            Long artId = requestDto.getArtId();

            boolean exists = artRepository.existsByArtId(artId);
            if(!exists) {
                return DeleteArtResponseDto.artNotFound();
            }


            ArtEntity art = artRepository.findByArtId(artId);
            String userId = findUserIdFromJwt();
            Long museumId = art.getMuseumId();
            MuseumEntity museum = museumRepository.findByMuseumId(museumId);
            if(!museum.getUserId().equals(userId)) {
                return DeleteArtResponseDto.idNotMatching();
            }

            Long qrcodeId = art.getQrcodeId();
            Long spotId = art.getSpotId();
            Long ttsId = art.getTtsId();
            String ttsUrl = ttsRepository.findByTtsId(ttsId).getBucketUrl();

            // art 이미지 S3 버킷에서 삭제(iter)
            String imgUrl = art.getImgUrl();
            URL url = new URL(imgUrl);
            String[] parts = url.getPath().split("/", 2);
            String key = parts[1]; // 파일 키(경로) 추출
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));

            footprintRepository.deleteAllByArtId(artId);
            bookmarkRepository.deleteAllByArtId(artId);

            artRepository.deleteByArtId(artId);


            qrcodeRepository.deleteByQrcodeId(qrcodeId);
            spotRepository.deleteBySpotId(spotId);
            // 버킷에서 음성파일 삭제
            ttsProvider.deleteTts(ttsUrl);
            ttsRepository.deleteByTtsId(ttsId);


            return ResponseDto.success();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }

    }


    /**
     * 작품정보 조회(ADMIN)
     * @return validationFailed, databaseError, museumNotFound, idNotMatching, success
     */
    @Override
    public ResponseEntity<? super GetArtsResponseDto> getArtList(GetArtsRequestDto requestDto) {

        List<ArtEntity> artEntities;

        try {

            Long museumId = requestDto.getMuseumId();

            boolean exists = museumRepository.existsByMuseumId(museumId);

            if(!exists) {
                return GetArtsResponseDto.museumNotFound();
            }

            MuseumEntity museum = museumRepository.findByMuseumId(museumId);

            String userId = findUserIdFromJwt();

            if(!museum.getUserId().equals(userId)) {
                return GetArtsResponseDto.idNotMatching();
            }

            artEntities = artRepository.findArtEntitiesByMuseumId(museumId);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
        return GetArtsResponseDto.success(artEntities);
    }

    /**
     * 박물관 id에 속하는 모든 작품의 qrcode를 리스트로 반환
     * @return validationFailed, databaseError, museumNotFound, idNotMatching, success
     */
    @Override
    public ResponseEntity<? super GetQrcodeListByMuseumIdResponseDto> getQrcodeListByMuseumId(GetQrcodeListByMuseumIdRequestDto requestDto) {

        try {

            long museumId = requestDto.getMuseumId();

            boolean exists = museumRepository.existsByMuseumId(museumId);
            if(!exists) {
                return GetQrcodeListByMuseumIdResponseDto.museumNotFound();
            }

            MuseumEntity museum = museumRepository.findByMuseumId(museumId);

            String userId = findUserIdFromJwt();

            if(!museum.getUserId().equals(userId)) {
                return GetQrcodeListByMuseumIdResponseDto.idNotMatching();
            }

            List<ArtEntity> artList = artRepository.findArtEntitiesByMuseumId(museumId);
            List<QrcodeEntity> qrcodeList = new ArrayList<>();
            for (ArtEntity art : artList) {
                Long qrcodeId = art.getQrcodeId();
                QrcodeEntity qrcode = qrcodeRepository.findByQrcodeId(qrcodeId);
                qrcodeList.add(qrcode);
            }

            return GetQrcodeListByMuseumIdResponseDto.success(qrcodeList);


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    // -----------------------------------------------

    @Override
    public ResponseEntity<? super GetMuseumResponseDto> getMuseum(GetMuseumRequestDto requestDto) {


        try {
            MuseumEntity museum;
            LocationEntity location;

            Long museumId = requestDto.getMuseumId();
            boolean exists = museumRepository.existsByMuseumId(museumId);

            if (!exists) {
                return GetMuseumResponseDto.museumNotFound();
            }
            else {
                museum = museumRepository.findByMuseumId(museumId);


                location = locationRepository.findByLocationId(museum.getLocationId());

            }

            return GetMuseumResponseDto.success(museum, location);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    /**
     * 사용자 위치에서 가장 가까운 박물관 반환
     * @return validationFailed, databaseError, museumNotFound, success
     */
    @Override
    @Transactional
    public ResponseEntity<? super GetNearestMuseumResponseDto> getNearestMuseum(GetNearestMuseumRequestDto requestDto) {

        MuseumEntity nearestMuseum;

        try {

            // 유저 현재 위치 추출
            BigDecimal userLatitude = requestDto.getLatitude();
            BigDecimal userLongitude = requestDto.getLongitude();

            // 박물관 위치 정보 조회
            List<LocationEntity> museumLocations = locationRepository.findAll();


            // 가장 가까운 박물관 찾기
            BigDecimal minDistance = null;
            Long nearestLocationId = null;

            for (LocationEntity location : museumLocations) {
                BigDecimal distance = calculateDistance(
                        userLatitude, userLongitude,
                        location.getLatitude(), location.getLongitude()
                );

                // 최소 거리 갱신
                if (minDistance == null || distance.compareTo(minDistance) < 0) {
                    minDistance = distance;
                    nearestLocationId = location.getLocationId();
                }
            }

            nearestMuseum = museumRepository.findFirstByLocationId(nearestLocationId);


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }

        if(nearestMuseum == null) {
            return GetNearestMuseumResponseDto.museumNotFound();
        }

        return GetNearestMuseumResponseDto.success(nearestMuseum);
    }

    /**
     * 사용자 위치에서 가장 가까운 박물관 N개 반환
     * @return validationFailed, databaseError, success
     */
    @Override
    public ResponseEntity<? super GetNearestNMuseumsResponseDto> getNearestNMuseums(GetNearestNMuseumsRequestDto requestDto) {
        try {
            BigDecimal latitude = requestDto.getLatitude();
            BigDecimal longitude = requestDto.getLongitude();

            int amount = requestDto.getAmount();

            List<MuseumEntity> museumList = museumRepository.findAll();

            TreeMap<BigDecimal, MuseumEntity> distanceMap = new TreeMap<>();

            for (MuseumEntity museumEntity : museumList) {
                LocationEntity loc = locationRepository.findByLocationId(museumEntity.getLocationId());
                BigDecimal locLatitude = loc.getLatitude();
                BigDecimal locLongitude = loc.getLongitude();
                BigDecimal distance = calculateDistance(latitude, longitude, locLatitude, locLongitude);

                distanceMap.put(distance, museumEntity);
            }

            // TreeMap에서 가장 가까운 N개의 박물관을 선택
            List<MuseumEntity> nearestMuseumList = new ArrayList<>(amount);
            int count = 0;
            for (Map.Entry<BigDecimal, MuseumEntity> entry : distanceMap.entrySet()) {
                nearestMuseumList.add(entry.getValue());
                count++;
                if (count >= amount) {
                    break;
                }
            }

            return GetNearestNMuseumsResponseDto.success(nearestMuseumList);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }
    /**
     * 사용자별 박물관 추천
     * @return validationFailed, databaseError, success
     */
    @Override
    public ResponseEntity<? super GetMuseumRecommendationsResponseDto> getMuseumRecommendations(GetMuseumRecommendationsRequestDto requestDto) {
        try {

            /*
            추천 로직 개발 전, 무작위 추출로 대체
             */
            int amount = requestDto.getAmount();
            List<MuseumEntity> all = museumRepository.findAll();
            int size = all.size();

            // 무작위로 섞기
            Collections.shuffle(all);

            // amount만큼 랜덤으로 뽑기
            List<MuseumEntity> result = new ArrayList<>();
            for (int i = 0; i < Math.min(amount, size); i++) {
                result.add(all.get(i));
            }

            return GetMuseumRecommendationsResponseDto.success(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    /**
     * 사용자 위치에서 가장 가까운 작품 N개 반환
     * @return validationFailed, databaseError, museumNotFound, success
     */
    @Override
    @Transactional
    public ResponseEntity<? super GetNearestNArtsResponseDto> getNearestNArtList(GetNearestNArtsRequestDto requestDto) {
        try {
            Long museumId = requestDto.getMuseumId();
            BigDecimal latitude = requestDto.getLatitude();
            BigDecimal longitude = requestDto.getLongitude();
            int amount = requestDto.getAmount();

            boolean exists = museumRepository.existsByMuseumId(museumId);
            if(!exists) {
                return GetNearestNArtsResponseDto.museumNotFound();
            }

            List<ArtEntity> artList = artRepository.findAllByMuseumId(museumId);

            // TreeMap을 사용하여 거리를 기준으로 예술 작품을 자동으로 정렬
            TreeMap<BigDecimal, ArtEntity> distanceMap = new TreeMap<>();

            for (ArtEntity artEntity : artList) {
                SpotEntity spot = spotRepository.findBySpotId(artEntity.getSpotId());
                BigDecimal spotLatitude = spot.getLatitude();
                BigDecimal spotLongitude = spot.getLongitude();
                BigDecimal distance = calculateDistance(latitude, longitude, spotLatitude, spotLongitude);

                distanceMap.put(distance, artEntity);
            }

            // TreeMap에서 가장 가까운 N개의 예술 작품을 선택
            List<ArtEntity> nearestArtEntities = new ArrayList<>(amount);
            int count = 0;
            for (Map.Entry<BigDecimal, ArtEntity> entry : distanceMap.entrySet()) {
                nearestArtEntities.add(entry.getValue());
                count++;
                if (count >= amount) {
                    break;
                }
            }

            return GetNearestNArtsResponseDto.success(nearestArtEntities);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    /**
     * 박물관 별 작품 모두 반환
     * @return validationFailed, databaseError, museumNotFound, success
     */
    @Override
    public ResponseEntity<? super GetAllArtsByMuseumIdResponseDto> getAllArts(GetAllArtsByMuseumIdRequestDto requestDto) {
        try {

            Long museumId = requestDto.getMuseumId();

            boolean exists = museumRepository.existsByMuseumId(museumId);
            if(!exists) {
                return GetAllArtsByMuseumIdResponseDto.museumNotFound();
            }

            List<ArtEntity> artList = artRepository.findAllByMuseumId(museumId);

            return GetAllArtsByMuseumIdResponseDto.success(artList);


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    /**
     * 작품 id로 작품정보 조회
     * @return validationFailed, databaseError, artNotFound, success
     */
    @Override
    public ResponseEntity<? super GetArtResponseDto> getArt(GetArtRequestDto requestDto) {

        try {

            Long artId = requestDto.getArtId();

            boolean exists = artRepository.existsByArtId(artId);
            if(!exists) {
                return GetArtResponseDto.artNotFound();
            }

            ArtEntity art = artRepository.findByArtId(artId);
            Long spotId = art.getSpotId();
            SpotEntity spot = spotRepository.findBySpotId(spotId);

            String userId = findUserIdFromJwt();
            exists = footprintRepository.existsByUserIdAndArtId(userId, artId);
            if(!exists) {
                footprintRepository.save(new FootprintEntity(userId, artId));
            }

            return GetArtResponseDto.success(art,spot);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    /**
     * qrcode hash로 art 조회
     * @return validationFailed, databaseError, artNotFound, success
     */
    @Override
    public ResponseEntity<? super GetArtByHashResponseDto> getArtByHash(GetArtByHashRequestDto requestDto) {

        try {

            String qrcodeHashKey = requestDto.getQrcodeHashKey();

            QrcodeEntity qrcode = qrcodeRepository.findByQrcodeHashkey(qrcodeHashKey);

            Long qrcodeId = qrcode.getQrcodeId();

            boolean exists = artRepository.existsByQrcodeId(qrcodeId);
            if(!exists) {
                return GetArtByHashResponseDto.artNotFound();
            }

            ArtEntity art = artRepository.findByQrcodeId(qrcodeId);
            Long spotId = art.getSpotId();
            SpotEntity spot = spotRepository.findBySpotId(spotId);

            String userId = findUserIdFromJwt();
            Long artId = art.getArtId();
            exists = footprintRepository.existsByUserIdAndArtId(userId, artId);
            if(!exists) {
                footprintRepository.save(new FootprintEntity(userId, artId));
            }

            return GetArtByHashResponseDto.success(art, spot);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    /**
     * tts정보 조회
     * @return validationFailed, databaseError, ttsNotFound, success
     */
    @Override
    public ResponseEntity<? super GetTtsResponseDto> getTts(GetTtsRequestDto requestDto) {

        try {

            Long ttsId = requestDto.getTtsId();

            boolean exists = ttsRepository.existsByTtsId(ttsId);
            if(!exists) {
                return GetTtsResponseDto.ttsNotFound();
            }

            TtsEntity tts = ttsRepository.findByTtsId(ttsId);
            return GetTtsResponseDto.success(tts);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    /**
     * qrcode 조회
     * @return validationFailed, databaseError, qrcodeNotFound, success
     */
    @Override
    public ResponseEntity<? super GetQrcodeResponseDto> getQrcode(GetQrcodeRequestDto requestDto) {

        try {

            Long qrcodeId = requestDto.getQrcodeId();
            boolean exists = qrcodeRepository.existsByQrcodeId(qrcodeId);
            if(!exists) {
                return GetQrcodeResponseDto.qrcodeNotFound();
            }

            QrcodeEntity qrcode = qrcodeRepository.findByQrcodeId(qrcodeId);
            return GetQrcodeResponseDto.success(qrcode);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    /**
     * 박물관 별 관람률
     * @return validationFailed, databaseError, museumNotFound, success
     */
    @Override
    public ResponseEntity<? super GetViewingRateResponseDto> getViewingRate(GetViewingRateRequestDto requestDto) {
        try {

            Long museumId = requestDto.getMuseumId();

            boolean exists = museumRepository.existsByMuseumId(museumId);
            if(!exists) {
                return GetViewingRateResponseDto.museumNotFound();
            }

            int amount = 0;
            String userId = findUserIdFromJwt();
            List<ArtEntity> artList = artRepository.findAllByMuseumId(museumId);
            for(ArtEntity art:artList) {
                if(footprintRepository.existsByUserIdAndArtId(userId, art.getArtId())) {
                    amount++;
                }
            }

            double percentage = ((double)amount/(double)artList.size())*100;

            return GetViewingRateResponseDto.success(percentage);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    /**
     * 북마크 토글
     * @return validationFailed, databaseError, artNotFound, success
     */
    @Override
    @Transactional
    public ResponseEntity<? super BookmarkingResponseDto> bookmarking(BookmarkingRequestDto requestDto) {
        try {

            Long artId = requestDto.getArtId();
            boolean exists = artRepository.existsByArtId(artId);
            if(!exists) {
                return BookmarkingResponseDto.artNotFound();
            }

            String userId = findUserIdFromJwt();

            exists = bookmarkRepository.existsByUserIdAndArtId(userId, artId);
            if(exists) {
                bookmarkRepository.deleteByUserIdAndArtId(userId, artId);
            } else {
                bookmarkRepository.save(new BookmarkEntity(userId, artId));
            }
            return BookmarkingResponseDto.success(!exists);



        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    /**
     * 요청자의 bookmark리스트 전송
     * @return validationFailed, databaseError, success
     */
    @Override
    public ResponseEntity<? super GetBookmarkListResponseDto> getBookmarkList(GetBookmarkListRequestDto requestDto) {
        try {

            String userId = findUserIdFromJwt();

            List<BookmarkEntity> bookmarkList = bookmarkRepository.findAllByUserId(userId);

            return GetBookmarkListResponseDto.success(bookmarkList);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }
    /**
     * 요청자의 bookmark된 작품 리스트 전송
     * @return validationFailed, databaseError, success
     */
    @Override
    public ResponseEntity<? super GetBookmarkedArtsResponseDto> getBookmarkedArtList(GetBookmarkedArtsRequestDto requestDto) {
        try {

            String userId = findUserIdFromJwt();

            List<BookmarkEntity> bookmarkList = bookmarkRepository.findAllByUserId(userId);
            List<ArtEntity> artList = new ArrayList<>();
            for(BookmarkEntity b: bookmarkList) {
                Long artId = b.getArtId();
                ArtEntity art = artRepository.findByArtId(artId);
                artList.add(art);
            }

            return GetBookmarkedArtsResponseDto.success(artList);


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    /**
     * 요청자가 artId 작품을 북마크 했는지 여부
     * @return validationFailed, databaseError, success
     */
    @Override
    public ResponseEntity<? super CheckBookmarkResponseDto> checkBookmark(CheckBookmarkRequestDto requestDto) {
        try {

            Long artId = requestDto.getArtId();

            boolean exists = artRepository.existsByArtId(artId);
            if(!exists) {
                return CheckBookmarkResponseDto.artNotFound();
            }

            String userId = findUserIdFromJwt();

            boolean isBookmark = bookmarkRepository.existsByUserIdAndArtId(userId, artId);

            return CheckBookmarkResponseDto.success(isBookmark);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
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

    private BigDecimal calculateDistance(BigDecimal latitude1, BigDecimal longitude1,
                                         BigDecimal latitude2, BigDecimal longitude2) {
        double lat1Rad = Math.toRadians(latitude1.doubleValue());
        double lat2Rad = Math.toRadians(latitude2.doubleValue());
        double lon1Rad = Math.toRadians(longitude1.doubleValue());
        double lon2Rad = Math.toRadians(longitude2.doubleValue());

        double x = (lon2Rad - lon1Rad) * Math.cos((lat1Rad + lat2Rad) / 2);
        double y = (lat2Rad - lat1Rad);
        double distance = Math.sqrt(x * x + y * y) * EARTH_RADIUS;

        return BigDecimal.valueOf(distance);
    }

}
