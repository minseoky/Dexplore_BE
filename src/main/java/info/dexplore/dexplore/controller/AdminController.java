package info.dexplore.dexplore.controller;


import info.dexplore.dexplore.dto.request.main.GetMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.SaveMuseumRequestDto;
import info.dexplore.dexplore.dto.response.main.GetMuseumResponseDto;
import info.dexplore.dexplore.dto.response.main.SaveMuseumResponseDto;
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

    @GetMapping("/get-museum")
    public ResponseEntity<? super GetMuseumResponseDto> getMuseum(@RequestBody @Valid GetMuseumRequestDto requestBody) {
        ResponseEntity<? super GetMuseumResponseDto> response = mainService.getMuseum(requestBody);
        log.info("[getMuseum]: {museumId: {}}", requestBody.getMuseumId());
        return response;
    }



}
