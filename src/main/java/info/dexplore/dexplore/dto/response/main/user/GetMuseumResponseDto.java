package info.dexplore.dexplore.dto.response.main.user;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.LocationEntity;
import info.dexplore.dexplore.entity.MuseumEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class GetMuseumResponseDto extends ResponseDto {

    private MuseumEntity museum;
    private LocationEntity location;

    public GetMuseumResponseDto(MuseumEntity museum, LocationEntity location) {
        super();
        this.museum = museum;
        this.location = location;
    }

    public static ResponseEntity<ResponseDto> museumNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.MUSEUM_NOT_FOUND, ResponseMessage.MUSEUM_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> success(MuseumEntity museum, LocationEntity location) {
        GetMuseumResponseDto responseBody = new GetMuseumResponseDto(museum, location);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

}
