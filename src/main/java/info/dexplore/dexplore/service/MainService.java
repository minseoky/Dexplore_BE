package info.dexplore.dexplore.service;

import info.dexplore.dexplore.dto.request.main.admin.*;
import info.dexplore.dexplore.dto.request.main.user.GetArtByHashRequestDto;
import info.dexplore.dexplore.dto.request.main.user.GetArtRequestDto;
import info.dexplore.dexplore.dto.request.main.user.GetNearestMuseumRequestDto;
import info.dexplore.dexplore.dto.request.main.user.GetNearestNArtsRequestDto;
import info.dexplore.dexplore.dto.response.main.admin.*;
import info.dexplore.dexplore.dto.response.main.user.GetArtByHashResponseDto;
import info.dexplore.dexplore.dto.response.main.user.GetArtResponseDto;
import info.dexplore.dexplore.dto.response.main.user.GetNearestMuseumResponseDto;
import info.dexplore.dexplore.dto.response.main.user.GetNearestNArtsResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MainService {

    ResponseEntity<? super SaveMuseumResponseDto> saveMuseum(MultipartFile imageFile, SaveMuseumRequestDto requestDto);
    ResponseEntity<? super UpdateMuseumResponseDto> updateMuseum(MultipartFile imageFile, UpdateMuseumRequestDto requestDto);
    ResponseEntity<? super DeleteMuseumResponseDto> deleteMuseum(DeleteMuseumRequestDto requestDto);
    ResponseEntity<? super GetMuseumResponseDto> getMuseum(GetMuseumRequestDto requestDto);
    ResponseEntity<? super GetMuseumListResponseDto> getMuseumList();
    ResponseEntity<? super SaveArtResponseDto> saveArt(MultipartFile imageFile, SaveArtRequestDto requestDto);
    ResponseEntity<? super UpdateArtResponseDto> updateArt(MultipartFile imageFile, UpdateArtRequestDto requestDto);
    ResponseEntity<? super DeleteArtResponseDto> deleteArt(DeleteArtRequestDto requestDto);
    ResponseEntity<? super GetArtsResponseDto> getArtList(GetArtsRequestDto requestDto);
    //--이하 user--
    ResponseEntity<? super GetNearestMuseumResponseDto> getNearestMuseum(GetNearestMuseumRequestDto requestDto);
    ResponseEntity<? super GetNearestNArtsResponseDto> getNearestNArtList(GetNearestNArtsRequestDto requestDto);
    ResponseEntity<? super GetArtResponseDto> getArt(GetArtRequestDto requestDto);
    ResponseEntity<? super GetArtByHashResponseDto> getArtByHash(GetArtByHashRequestDto requestDto);
}
