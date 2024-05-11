package info.dexplore.dexplore.provider;

import info.dexplore.dexplore.repository.QrcodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QrcodeProvider {

    private final QrcodeRepository qrcodeRepository;

    /**
     * 유일성 보장되는 qr코드 hash를 통해 생성하여 DB에 삽입(QrcodeEntity 이용) 후 해당 qr코드 id 반환
     * @return DB에 저장한 qr코드의 id 반환
     */
    public Long generateQrcode() {

        return null;
    }
}
