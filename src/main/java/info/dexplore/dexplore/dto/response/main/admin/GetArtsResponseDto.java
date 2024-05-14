package info.dexplore.dexplore.dto.response.main.admin;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.ArtEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetArtsResponseDto extends ResponseDto {

    private ArtEntity art;

    public GetArtsResponseDto(ArtEntity art) {
        super();
        this.art = art;
    }


    public static ResponseEntity<ResponseDto> museumNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.MUSEUM_NOT_FOUND, ResponseMessage.MUSEUM_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> idNotMatching() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.ID_NOT_MATCHING, ResponseMessage.ID_NOT_MATCHING);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> success(ArtEntity art) {
        GetArtsResponseDto responseBody = new GetArtsResponseDto(art);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}
