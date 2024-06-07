package info.dexplore.dexplore.dto.response.main.user;

import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.ArtEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
public class GetBookmarkedArtsResponseDto extends ResponseDto {

    private List<ArtEntity> artList;

    public GetBookmarkedArtsResponseDto(List<ArtEntity> artList) {
        super();
        this.artList = artList;
    }

    public static ResponseEntity<ResponseDto> success(List<ArtEntity> artList) {
        GetBookmarkedArtsResponseDto responseBody = new GetBookmarkedArtsResponseDto(artList);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

}
