package info.dexplore.dexplore.dto.response.main.user;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.ArtEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class GetArtByHashResponseDto extends ResponseDto {

    private ArtEntity art;

    public GetArtByHashResponseDto(ArtEntity art) {
        super();
        this.art = art;
    }

    public static ResponseEntity<ResponseDto> artNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.ART_NOT_FOUND, ResponseMessage.ART_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> success(ArtEntity art) {
        GetArtResponseDto responseBody = new GetArtResponseDto(art);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}
