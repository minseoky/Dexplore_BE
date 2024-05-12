package info.dexplore.dexplore.provider;

import info.dexplore.dexplore.entity.QrcodeEntity;
import info.dexplore.dexplore.repository.QrcodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class QrcodeProvider {

    private final QrcodeRepository qrcodeRepository;

    /**
     * 유일성 보장되는 qr코드 hash를 통해 생성하여 DB에 삽입(QrcodeEntity 이용) 후 해당 qr코드 id 반환, qrcodeName에는 artName과 같은 값 삽입
     * @return DB에 저장한 qr코드의 id 반환
     */
    public Long generateQrcode(String artName) {
        String hashKey = createHashKey(artName);

        QrcodeEntity qrcodeEntity = new QrcodeEntity(null, artName, hashKey);
        qrcodeRepository.save(qrcodeEntity);
        return qrcodeEntity.getQrcodeId();
    }

    /**
     * UUID를 이용하여 해시 값을 생성하는 메서드
     * @param input 입력 문자열
     * @return 생성된 해시 값
     */
    public String createHashKey(String input) {
        // UUID 생성
        UUID uuid = UUID.randomUUID();

        // UUID -> String
        String uuidString = uuid.toString();

        //input+uuid
        String hashKey = input + uuidString;
        return hashKey;
    }

}
