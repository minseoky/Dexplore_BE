package info.dexplore.dexplore.controller;

import info.dexplore.dexplore.dto.request.main.admin.GetMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.user.GetNearestMuseumRequestDto;
import info.dexplore.dexplore.dto.response.main.admin.GetMuseumResponseDto;
import info.dexplore.dexplore.dto.response.main.user.GetNearestMuseumResponseDto;
import info.dexplore.dexplore.service.MainService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final MainService mainService;

    /**
     * 박물관 정보 가져오기
     * @return validationFailed, museumNotFound, IdNotMatching, databaseError, success
     */
    @GetMapping("/get-nearest-museum")
    public ResponseEntity<? super GetNearestMuseumResponseDto> getNearestMuseum(@ModelAttribute @Valid GetNearestMuseumRequestDto requestBody) {
        ResponseEntity<? super GetNearestMuseumResponseDto> response = mainService.getNearestMuseum(requestBody);
        log.info("[getNearestMuseum]");
        return response;
    }

}
