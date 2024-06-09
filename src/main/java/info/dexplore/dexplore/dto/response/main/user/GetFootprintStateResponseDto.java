package info.dexplore.dexplore.dto.response.main.user;

import info.dexplore.dexplore.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Getter
public class GetFootprintStateResponseDto extends ResponseDto {

    private boolean isVisited;

    public GetFootprintStateResponseDto(boolean isVisited) {
        super();
        this.isVisited = isVisited;
    }

    public static ResponseEntity<ResponseDto> success(boolean isVisited) {
        GetFootprintStateResponseDto responseBody = new GetFootprintStateResponseDto(isVisited);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

}
