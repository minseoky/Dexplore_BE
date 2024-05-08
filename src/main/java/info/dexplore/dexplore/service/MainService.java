package info.dexplore.dexplore.service;

import info.dexplore.dexplore.dto.request.main.GetMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.SaveMuseumRequestDto;
import info.dexplore.dexplore.dto.response.main.GetMuseumResponseDto;
import info.dexplore.dexplore.dto.response.main.SaveMuseumResponseDto;
import org.springframework.http.ResponseEntity;

public interface MainService {

    ResponseEntity<? super SaveMuseumResponseDto> saveMuseum(SaveMuseumRequestDto requestDto);
    ResponseEntity<? super GetMuseumResponseDto> getMuseum(GetMuseumRequestDto requestDto);
}
