package info.dexplore.dexplore.dto.response.main.user;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.ArtEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class GetNearestNArtsResponseDto extends ResponseDto {

    private List<ArtEntity> artList;

    public GetNearestNArtsResponseDto(List<ArtEntity> artList) {
        super();
        this.artList = artList;
    }

    public static ResponseEntity<ResponseDto> museumNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.MUSEUM_NOT_FOUND, ResponseMessage.MUSEUM_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> success(List<ArtEntity> artList) {
        GetNearestNArtsResponseDto responseBody = new GetNearestNArtsResponseDto(artList);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }



}
