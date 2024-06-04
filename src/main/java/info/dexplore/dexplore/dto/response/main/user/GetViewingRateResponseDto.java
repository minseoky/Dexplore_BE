package info.dexplore.dexplore.dto.response.main.user;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class GetViewingRateResponseDto extends ResponseDto {

    private double percentage;

    public GetViewingRateResponseDto(double percentage) {
        super();
        this.percentage = percentage;
    }

    public static ResponseEntity<ResponseDto> museumNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.MUSEUM_NOT_FOUND, ResponseMessage.MUSEUM_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> success(double percentage) {
        GetViewingRateResponseDto responseBody = new GetViewingRateResponseDto(percentage);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

}
