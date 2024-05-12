package info.dexplore.dexplore.provider;

import info.dexplore.dexplore.entity.QrcodeEntity;
import info.dexplore.dexplore.repository.QrcodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
@RequiredArgsConstructor
public class QrcodeProvider {

    private final QrcodeRepository qrcodeRepository;

    /**
     * 유일성 보장되는 qr코드 hash를 통해 생성하여 DB에 삽입(QrcodeEntity 이용) 후 해당 qr코드 id 반환, qrcodeName에는 artName과 같은 값 삽입
     * @return DB에 저장한 qr코드의 id 반환
     */
    public Long generateQrcode(String qrcodeHashkey) {
        return qrcodeRepository.findByQrcodeHashkey(qrcodeHashkey).getQrcodeId();
    }

    //artName을 바탕으로 QRCodeEntity의 요소를을 지정하고 저장
    public Long generateQrHash(String artName) {
        String hashKey = createHashKey(artName);

        QrcodeEntity qrcodeEntity = new QrcodeEntity(null, artName, hashKey);

        QrcodeEntity savedQrcode = qrcodeRepository.save(qrcodeEntity);
        return savedQrcode.getQrcodeId();
    }

    //해시값 생성함수
    public String createHashKey(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // 예외 처리 필요
            return null;
        }
    }

}
