package info.dexplore.dexplore.controller;

import info.dexplore.dexplore.dto.request.main.user.*;
import info.dexplore.dexplore.dto.request.main.user.test.TestRequestDto;
import info.dexplore.dexplore.dto.response.main.user.*;
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
     * @return validationFailed, museumNotFound, databaseError, success
     */
    @GetMapping("/get-museum")
    public ResponseEntity<? super GetMuseumResponseDto> getMuseum(@ModelAttribute @Valid GetMuseumRequestDto requestBody) {
        ResponseEntity<? super GetMuseumResponseDto> response = mainService.getMuseum(requestBody);
        log.info("[getMuseum]: {museumId: {}}", requestBody.getMuseumId());
        return response;
    }


    /**
     * 가까운 박물관 정보 가져오기
     * @return validationFailed, museumNotFound, IdNotMatching, databaseError, success
     */
    @GetMapping("/get-nearest-museum")
    public ResponseEntity<? super GetNearestMuseumResponseDto> getNearestMuseum(@ModelAttribute @Valid GetNearestMuseumRequestDto requestBody) {
        ResponseEntity<? super GetNearestMuseumResponseDto> response = mainService.getNearestMuseum(requestBody);
        log.info("[getNearestMuseum]");
        return response;
    }
    /**
     * 가까운 박물관 정보 N개 가져오기
     * @return validationFailed, databaseError, success
     */
    @GetMapping("/get-nearest-n-museums")
    public ResponseEntity<? super GetNearestNMuseumsResponseDto> getNearestNMuseums(@ModelAttribute @Valid GetNearestNMuseumsRequestDto reqeustBody) {
        ResponseEntity<? super GetNearestNMuseumsResponseDto> response = mainService.getNearestNMuseums(reqeustBody);
        log.info("[getNearestNMuseums]");
        return response;
    }
    /**
     * 추천 박물관 리스트 추천
     * @return validationFailed, databaseError, success
     */
    @GetMapping("/get-museum-recommendations")
    public ResponseEntity<? super GetMuseumRecommendationsResponseDto> getNearestNMuseums(@ModelAttribute @Valid GetMuseumRecommendationsRequestDto reqeustBody) {
        ResponseEntity<? super GetMuseumRecommendationsResponseDto> response = mainService.getMuseumRecommendations(reqeustBody);
        log.info("[getMuseumRecommendations]");
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

    /**
     * qr hash로 작품 정보 가져오기
     * @return validationFailed, artNotFound, databaseError, success
     */
    @GetMapping("/get-art-by-hash")
    public ResponseEntity<? super GetArtByHashResponseDto> getArtByHash(@ModelAttribute @Valid GetArtByHashRequestDto requestBody) {
        ResponseEntity<? super GetArtByHashResponseDto> response = mainService.getArtByHash(requestBody);
        log.info("[getArtByHash]");
        return response;
    }

    /**
     * tts 조히하기
     * @return validationFailed, ttsNotFound, databaseError, success
     */
    @GetMapping("/get-tts")
    public ResponseEntity<? super GetTtsResponseDto> getTts(@ModelAttribute @Valid GetTtsRequestDto requestDto) {
        ResponseEntity<? super GetTtsResponseDto> response = mainService.getTts(requestDto);
        log.info("[getTts]");
        return response;
    }

    /**
     * qrcode 조회하기
     * @return validationFailed, databaseError, qrcodeNotFound, success
     */
    @GetMapping("/get-qrcode")
    public ResponseEntity<? super GetQrcodeResponseDto> getQrcode(@ModelAttribute @Valid GetQrcodeRequestDto requestDto) {
        ResponseEntity<? super GetQrcodeResponseDto> response = mainService.getQrcode(requestDto);
        log.info("[getQrcode]");
        return response;
    }
    /**
     * qrcode 조회하기
     * @return validationFailed, databaseError, museumNotFound, success
     */
    @GetMapping("/get-viewing-rate")
    public ResponseEntity<? super GetViewingRateResponseDto> getViewingRate(@ModelAttribute @Valid GetViewingRateRequestDto requestDto) {
        ResponseEntity<? super GetViewingRateResponseDto> response = mainService.getViewingRate(requestDto);
        log.info("[getViewingRate]");
        return response;
    }
    /**
     * 북마크하기
     * @return validationFailed, databaseError, artNotFound, success
     */
    @GetMapping("/bookmarking")
    public ResponseEntity<? super BookmarkingResponseDto> getViewingRate(@ModelAttribute @Valid BookmarkingRequestDto requestDto) {
        ResponseEntity<? super BookmarkingResponseDto> response = mainService.bookmarking(requestDto);
        log.info("[bookmarking]");
        return response;
    }
    /**
     * 북마크리스트 반환
     * @return validationFailed, databaseError, success
     */
    @GetMapping("/get-bookmarks")
    public ResponseEntity<? super GetBookmarkListResponseDto> getBookmarkList(@ModelAttribute @Valid GetBookmarkListRequestDto requestDto) {
        ResponseEntity<? super GetBookmarkListResponseDto> response = mainService.getBookmarkList(requestDto);
        log.info("[getBookmarkList]");
        return response;
    }
    /**
     * 북마크 여부 반환
     * @return validationFailed, databaseError, success
     */
    @GetMapping("/check-bookmark")
    public ResponseEntity<? super CheckBookmarkResponseDto> getBookmarkList(@ModelAttribute @Valid CheckBookmarkRequestDto requestDto) {
        ResponseEntity<? super CheckBookmarkResponseDto> response = mainService.checkBookmark(requestDto);
        log.info("[checkBookmark]");
        return response;
    }



}

