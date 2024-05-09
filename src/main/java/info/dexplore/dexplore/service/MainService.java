package info.dexplore.dexplore.service;

import info.dexplore.dexplore.dto.request.main.GetMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.UpdateMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.SaveMuseumRequestDto;
import info.dexplore.dexplore.dto.response.main.GetMuseumListResponseDto;
import info.dexplore.dexplore.dto.response.main.GetMuseumResponseDto;
import info.dexplore.dexplore.dto.response.main.UpdateMuseumResponseDto;
import info.dexplore.dexplore.dto.response.main.SaveMuseumResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MainService {

    ResponseEntity<? super SaveMuseumResponseDto> saveMuseum(MultipartFile imageFile, SaveMuseumRequestDto requestDto);
    ResponseEntity<? super UpdateMuseumResponseDto> updateMuseum(MultipartFile imageFile, UpdateMuseumRequestDto requestDto);
    ResponseEntity<? super GetMuseumResponseDto> getMuseum(GetMuseumRequestDto requestDto);
    ResponseEntity<? super GetMuseumListResponseDto> getMuseumList();
}
