package info.dexplore.dexplore.controller;

import info.dexplore.dexplore.dto.request.main.admin.GetMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.user.GetNearestMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.user.test.TestRequestDto;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.dto.response.main.admin.GetMuseumResponseDto;
import info.dexplore.dexplore.dto.response.main.user.GetNearestMuseumResponseDto;
import info.dexplore.dexplore.dto.response.main.user.test.TestResponseDto;
import info.dexplore.dexplore.service.MainService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

}
