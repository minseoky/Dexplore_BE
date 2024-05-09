package info.dexplore.dexplore.controller;


import info.dexplore.dexplore.dto.request.main.GetMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.SaveMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.UpdateMuseumRequestDto;
import info.dexplore.dexplore.dto.response.main.GetMuseumListResponseDto;
import info.dexplore.dexplore.dto.response.main.GetMuseumResponseDto;
import info.dexplore.dexplore.dto.response.main.SaveMuseumResponseDto;
import info.dexplore.dexplore.dto.response.main.UpdateMuseumResponseDto;
import info.dexplore.dexplore.service.MainService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<? super SaveMuseumResponseDto> saveMuseum(@RequestBody @Valid SaveMuseumRequestDto requestBody){
        ResponseEntity<? super SaveMuseumResponseDto> response = mainService.saveMuseum(requestBody);
        log.info("[saveMuseum]: {museumName: {}}",  requestBody.getMuseumName());
        return response;
    }

    /**
     * 박물관 정보 수
     * @param requestBody
     * @returnvalidationFailed, idNotFound, museumNotFound, databaseError, success
     */
    @PostMapping("/update-museum")
    public ResponseEntity<? super UpdateMuseumResponseDto> updateMuseum(@RequestBody @Valid UpdateMuseumRequestDto requestBody){
        ResponseEntity<? super UpdateMuseumResponseDto> response = mainService.updateMuseum(requestBody);
        log.info("[updateMuseum]: {museumName: {}}",  requestBody.getMuseumName());
        return response;
    }

    /**
     * 박물관 정보 가져오기
     * @return validationFailed, museumNotFound, IdNotMatching, databaseError, success
     */
    @GetMapping("/get-museum")
    public ResponseEntity<? super GetMuseumResponseDto> getMuseum(@RequestBody @Valid GetMuseumRequestDto requestBody) {
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



}
