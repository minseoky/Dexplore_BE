package info.dexplore.dexplore.controller;


import info.dexplore.dexplore.dto.request.main.admin.*;
import info.dexplore.dexplore.dto.response.main.admin.*;
import info.dexplore.dexplore.service.MainService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MainService mainService;

    /**
     * 박물관 저장(등록)
     * @return validationFailed, idNotFound, duplicatedMuseumName, databaseError, success
     */
    @PostMapping("/save-museum")
    public ResponseEntity<? super SaveMuseumResponseDto> saveMuseum(@RequestParam("imageFile") MultipartFile imageFile,
                                                                    @ModelAttribute @Valid SaveMuseumRequestDto requestDto){
        ResponseEntity<? super SaveMuseumResponseDto> response = mainService.saveMuseum(imageFile, requestDto);
        log.info("[saveMuseum]: {museumName: {}}",  requestDto.getMuseumName());
        return response;
    }

    /**
     * 박물관 정보 수정
     * @returnvalidationFailed, idNotFound, museumNotFound, databaseError, success
     */
    @PostMapping("/update-museum")
    public ResponseEntity<? super UpdateMuseumResponseDto> updateMuseum(@RequestParam("imageFile") MultipartFile imageFile,
                                                                        @ModelAttribute @Valid UpdateMuseumRequestDto requestBody){
        ResponseEntity<? super UpdateMuseumResponseDto> response = mainService.updateMuseum(imageFile, requestBody);
        log.info("[updateMuseum]: {museumName: {}}",  requestBody.getMuseumName());
        return response;
    }

    /**
     * 박물관 삭제(아래 위치정보 및 작품정보 등도 모두 삭제)
     * @return validationFailed, databaseError, museumNotFound, idNotMatching, success
     */
    @PostMapping("/delete-museum")
    public ResponseEntity<? super DeleteMuseumResponseDto> deleteMuseum(@RequestBody @Valid DeleteMuseumRequestDto requestBody) {
        ResponseEntity<? super DeleteMuseumResponseDto> response = mainService.deleteMuseum(requestBody);
        log.info("[deleteMuseum]");
        return response;
    }

    /**
     * 박물관 정보 가져오기
     * @return validationFailed, museumNotFound, IdNotMatching, databaseError, success
     */
    @GetMapping("/get-museum")
    public ResponseEntity<? super GetMuseumResponseDto> getMuseum(@ModelAttribute @Valid GetMuseumRequestDto requestBody) {
        ResponseEntity<? super GetMuseumResponseDto> response = mainService.getMuseum(requestBody);
        log.info("[getMuseum]: {museumId: {}}", requestBody.getMuseumId());
        return response;
    }

    /**
     * 모든 박물관 정보 가져오기
     * @return validationFailed, databaseError, success
     */
    @GetMapping("/get-museums")
    public ResponseEntity<? super GetMuseumListResponseDto> getMuseums() {
        ResponseEntity<? super GetMuseumListResponseDto> response = mainService.getMuseumList();
        log.info("[getMuseums]");
        return response;
    }

    /**
     * 작품정보 등록하기
     * @return validationFailed, databaseError, idNotFound, museumNotFound, success
     */
    @PostMapping("/save-art")
    public ResponseEntity<? super SaveArtResponseDto> saveArt(@RequestParam("imageFile") MultipartFile imageFile,
                                                              @ModelAttribute @Valid SaveArtRequestDto requestBody) {
        ResponseEntity<? super SaveArtResponseDto> response = mainService.saveArt(imageFile, requestBody);
        log.info("[saveArt]");
        return response;
    }

    /**
     * 작품정보 수정하기
     * @return validationFailed, databaseError, idNotFound, artNotFound, museumNotFound, success
     */
    @PostMapping("/update-art")
    public ResponseEntity<? super UpdateArtResponseDto> updateArt(@RequestParam("imageFile") MultipartFile imageFile,
                                                                  @ModelAttribute @Valid UpdateArtRequestDto requestBody) {
        ResponseEntity<? super UpdateArtResponseDto> response = mainService.updateArt(imageFile, requestBody);
        log.info("[updateArt]");
        return response;
    }

    /**
     * 작품정보 삭제하기
     * @return validationFailed, databaseError, artNotFound, success
     */
    @PostMapping("/delete-art")
    public ResponseEntity<? super DeleteArtResponseDto> deleteArt(@RequestBody @Valid DeleteArtRequestDto requestBody) {
        ResponseEntity<? super DeleteArtResponseDto> response = mainService.deleteArt(requestBody);
        log.info("[deleteArt");
        return response;
    }

    /**
     * 작품정보 조회하기
     * @return validationFailed, databaseError, museumNotFound, idNotMatching, success
     */
    @GetMapping("/get-arts")
    public ResponseEntity<? super GetArtsResponseDto> getArtList(@ModelAttribute @Valid GetArtsRequestDto requestBody) {
        ResponseEntity<? super GetArtsResponseDto> response = mainService.getArtList(requestBody);
        log.info("[getArtList]");
        return response;
    }

    /**
     * 박물관 id에 속하는 모든 작품의 qrcode를 리스트로 반환
     * @return validationFailed, databaseError, museumNotFound, idNotMatching, success
     */
    @GetMapping("/get-qrcode-list")
    public ResponseEntity<? super GetQrcodeListByMuseumIdResponseDto> getQrcodeListByMuseumId(@ModelAttribute @Valid GetQrcodeListByMuseumIdRequestDto requestBody) {
        ResponseEntity<? super GetQrcodeListByMuseumIdResponseDto> response = mainService.getQrcodeListByMuseumId(requestBody);
        log.info("[getQrcodeListByMuseumId]");
        return response;
    }


}
