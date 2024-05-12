package info.dexplore.dexplore.dto.response.main.user.test;

import info.dexplore.dexplore.dto.response.ResponseDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class TestResponseDto extends ResponseDto {

    private String msg;

    public TestResponseDto(String msg) {
        super();
        this.msg = msg;
    }

    public static ResponseEntity<TestResponseDto> success(String msg) {
        TestResponseDto responseBody = new TestResponseDto(msg);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}
