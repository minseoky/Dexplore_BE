package info.dexplore.dexplore.dto.response.main.user;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.ArtEntity;
import info.dexplore.dexplore.entity.SpotEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class GetArtResponseDto extends ResponseDto {

    private ArtEntity art;
    private SpotEntity spot;

    public GetArtResponseDto(ArtEntity art, SpotEntity spot) {
        super();
        this.art = art;
        this.spot = spot;
    }

    public static ResponseEntity<ResponseDto> artNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.ART_NOT_FOUND, ResponseMessage.ART_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> success(ArtEntity art, SpotEntity spot) {
        GetArtResponseDto responseBody = new GetArtResponseDto(art, spot);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

}
