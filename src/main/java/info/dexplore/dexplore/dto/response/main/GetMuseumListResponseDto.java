package info.dexplore.dexplore.dto.response.main;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.MuseumEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class GetMuseumListResponseDto extends ResponseDto {

    private List<MuseumEntity> museumList;

    public GetMuseumListResponseDto(List<MuseumEntity> museumList) {
        super();
        this.museumList = museumList;
    }

    public static ResponseEntity<ResponseDto> success(List<MuseumEntity> museumList) {
        GetMuseumListResponseDto responseBody = new GetMuseumListResponseDto(museumList);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}
