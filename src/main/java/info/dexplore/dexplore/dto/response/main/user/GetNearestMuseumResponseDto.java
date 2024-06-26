package info.dexplore.dexplore.dto.response.main.user;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.MuseumEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class GetNearestMuseumResponseDto extends ResponseDto {

    private MuseumEntity museum;

    public GetNearestMuseumResponseDto(MuseumEntity museum) {
        super();
        this.museum = museum;
    }

    public static ResponseEntity<ResponseDto> museumNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.MUSEUM_NOT_FOUND, ResponseMessage.MUSEUM_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> success(MuseumEntity museum) {
        GetNearestMuseumResponseDto responseBody = new GetNearestMuseumResponseDto(museum);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }


}
