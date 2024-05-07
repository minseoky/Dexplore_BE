package info.dexplore.dexplore.service;

import info.dexplore.dexplore.dto.request.main.MuseumRequestDto;
import info.dexplore.dexplore.dto.response.main.MuseumResponseDto;
import org.springframework.http.ResponseEntity;

public interface MainService {
    ResponseEntity<? super MuseumResponseDto> getMuseum(MuseumRequestDto requestDto);
}
