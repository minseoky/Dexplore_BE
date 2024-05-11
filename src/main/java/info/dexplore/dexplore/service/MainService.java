package info.dexplore.dexplore.service;

import info.dexplore.dexplore.dto.request.main.admin.GetMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.admin.SaveArtRequestDto;
import info.dexplore.dexplore.dto.request.main.admin.UpdateMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.admin.SaveMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.user.GetNearestMuseumRequestDto;
import info.dexplore.dexplore.dto.response.main.admin.*;
import info.dexplore.dexplore.dto.response.main.user.GetNearestMuseumResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MainService {

    ResponseEntity<? super SaveMuseumResponseDto> saveMuseum(MultipartFile imageFile, SaveMuseumRequestDto requestDto);
    ResponseEntity<? super UpdateMuseumResponseDto> updateMuseum(MultipartFile imageFile, UpdateMuseumRequestDto requestDto);
    ResponseEntity<? super GetMuseumResponseDto> getMuseum(GetMuseumRequestDto requestDto);
    ResponseEntity<? super GetMuseumListResponseDto> getMuseumList();
    ResponseEntity<? super SaveArtResponseDto> saveArt(MultipartFile imageFile, SaveArtRequestDto requestDto);
    ResponseEntity<? super GetNearestMuseumResponseDto> getNearestMuseum(GetNearestMuseumRequestDto requestDto);
}
