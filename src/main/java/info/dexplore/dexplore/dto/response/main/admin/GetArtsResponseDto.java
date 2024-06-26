package info.dexplore.dexplore.dto.response.main.admin;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.ArtEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class GetArtsResponseDto extends ResponseDto {

    private List<ArtEntity> arts;

    public GetArtsResponseDto(List<ArtEntity> arts) {
        super();
        this.arts = arts;
    }


    public static ResponseEntity<ResponseDto> museumNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.MUSEUM_NOT_FOUND, ResponseMessage.MUSEUM_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> idNotMatching() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.ID_NOT_MATCHING, ResponseMessage.ID_NOT_MATCHING);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> success(List<ArtEntity> arts) {
        GetArtsResponseDto responseBody = new GetArtsResponseDto(arts);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}
