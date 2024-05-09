package info.dexplore.dexplore.service;

import info.dexplore.dexplore.dto.request.main.GetMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.UpdateMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.SaveMuseumRequestDto;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.dto.response.main.GetMuseumListResponseDto;
import info.dexplore.dexplore.dto.response.main.GetMuseumResponseDto;
import info.dexplore.dexplore.dto.response.main.UpdateMuseumResponseDto;
import info.dexplore.dexplore.dto.response.main.SaveMuseumResponseDto;
import info.dexplore.dexplore.entity.LocationEntity;
import info.dexplore.dexplore.entity.MuseumEntity;
import info.dexplore.dexplore.repository.LocationRepository;
import info.dexplore.dexplore.repository.MuseumRepository;
import info.dexplore.dexplore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final MuseumRepository museumRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    /**
     * museum 등록하기
     * @param requestDto
     * @return validationFailed, databaseError, duplicatedMuseumName, idNotFound, success
     */
    @Override
    public ResponseEntity<? super SaveMuseumResponseDto> saveMuseum(SaveMuseumRequestDto requestDto) {

        try {

            //유저 id 확인
            String userId = "Default User Id...";
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                userId = authentication.getName();
                log.info("[saveMuseum]: 추출한 id:{}", userId);
            }

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
                    phone
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
    public ResponseEntity<? super UpdateMuseumResponseDto> updateMuseum(UpdateMuseumRequestDto requestDto) {
        try {

            //유저 id 확인
            String userId = "Default User Id...";
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                userId = authentication.getName();
                log.info("[saveMuseum]: 추출한 id:{}", userId);
            }

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
                    phone
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


}
