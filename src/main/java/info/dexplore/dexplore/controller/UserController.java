package info.dexplore.dexplore.controller;

import info.dexplore.dexplore.dto.request.main.user.GetArtRequestDto;
import info.dexplore.dexplore.dto.request.main.user.GetNearestMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.user.GetNearestNArtsRequestDto;
import info.dexplore.dexplore.dto.request.main.user.test.TestRequestDto;
import info.dexplore.dexplore.dto.response.main.user.GetArtResponseDto;
import info.dexplore.dexplore.dto.response.main.user.GetNearestMuseumResponseDto;
import info.dexplore.dexplore.dto.response.main.user.GetNearestNArtsResponseDto;
import info.dexplore.dexplore.dto.response.main.user.test.TestResponseDto;
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

    @GetMapping("/test/echo-get")
    public ResponseEntity<? super TestResponseDto> getTest(@ModelAttribute @Valid TestRequestDto requestBody) {
        return TestResponseDto.success(requestBody.getMsg());
    }

    @PostMapping("/test/echo-post")
    public ResponseEntity<? super TestResponseDto> postTest(@RequestBody @Valid TestRequestDto requestBody) {
        return TestResponseDto.success(requestBody.getMsg());
    }


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
    /**
     * 가까운 작품 정보 N개 가져오기
     * @return validationFailed, museumNotFound, databaseError, success
     */
    @GetMapping("/get-nearest-n-arts")
    public ResponseEntity<? super GetNearestNArtsResponseDto> getNearestNArts(@ModelAttribute @Valid GetNearestNArtsRequestDto reqeustBody) {
        ResponseEntity<? super GetNearestNArtsResponseDto> response = mainService.getNearestNArtList(reqeustBody);
        log.info("[getNearestNArts]");
        return response;
    }
    /**
     * 작품 정보 가져오기
     * @return validationFailed, artNotFound, databaseError, success
     */
    @GetMapping("/get-art")
    public ResponseEntity<? super GetArtResponseDto> getArt(@ModelAttribute @Valid GetArtRequestDto requestBody) {
        ResponseEntity<? super GetArtResponseDto> response = mainService.getArt(requestBody);
        log.info("[getArt]");
        return response;
    }

}
