package info.dexplore.dexplore.provider;

import info.dexplore.dexplore.repository.TtsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TtsProvider {

    private final TtsRepository ttsRepository;

    /**
     * artDescription 기반으로 tts 생성 및 버킷에 저장, tts DB에 저장(TtsEntity 이용)
     * @return 저장된 tts의 id 반환
     */
    public Long generateTts(String artDescription) {
        return null;
    }
}
