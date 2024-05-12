package info.dexplore.dexplore.controller;


import info.dexplore.dexplore.dto.request.main.admin.GetMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.admin.SaveArtRequestDto;
import info.dexplore.dexplore.dto.request.main.admin.SaveMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.admin.UpdateMuseumRequestDto;
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

    @PostMapping("/save-art")
    public ResponseEntity<? super SaveArtResponseDto> saveArt(@RequestParam("imageFile") MultipartFile imageFile,
                                                              @ModelAttribute @Valid SaveArtRequestDto requestBody) {
        ResponseEntity<? super SaveArtResponseDto> response = mainService.saveArt(imageFile, requestBody);
        log.info("[saveArt]");
        return response;
    }



}
