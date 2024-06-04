package info.dexplore.dexplore.dto.response.main.user;

import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.MuseumEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class GetNearestNMuseumsResponseDto extends ResponseDto {

    private List<MuseumEntity> museumList;

    public GetNearestNMuseumsResponseDto(List<MuseumEntity> museumList) {
        super();
        this.museumList = museumList;
    }

    public static ResponseEntity<ResponseDto> success(List<MuseumEntity> museumList) {
        GetNearestNMuseumsResponseDto responseBody = new GetNearestNMuseumsResponseDto(museumList);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }


}
