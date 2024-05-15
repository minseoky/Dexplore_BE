package info.dexplore.dexplore.dto.response.main.user;

import info.dexplore.dexplore.common.ResponseCode;
import info.dexplore.dexplore.common.ResponseMessage;
import info.dexplore.dexplore.dto.response.ResponseDto;
import info.dexplore.dexplore.entity.ArtEntity;
import info.dexplore.dexplore.entity.TtsEntity;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class GetTtsResponseDto extends ResponseDto {

    TtsEntity tts;

    public GetTtsResponseDto(TtsEntity tts) {
        super();
        this.tts = tts;
    }

    public static ResponseEntity<ResponseDto> ttsNotFound() {
        ResponseDto responseBody = new ResponseDto(ResponseCode.TTS_NOT_FOUND, ResponseMessage.TTS_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }

    public static ResponseEntity<ResponseDto> success(TtsEntity tts) {
        GetTtsResponseDto responseBody = new GetTtsResponseDto(tts);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}
