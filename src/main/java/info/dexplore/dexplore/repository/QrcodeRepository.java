package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.QrcodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QrcodeRepository extends JpaRepository<QrcodeEntity, String> {
    QrcodeEntity findByQrcodeHashkey(String qrcodeHashKey);
    QrcodeEntity findByQrcodeName(String qrcodeName);
    void deleteByQrcodeId(Long qrcodeId);
    boolean existsByQrcodeId(Long qrcodeId);
    QrcodeEntity findByQrcodeId(Long qrcodeId);

}
