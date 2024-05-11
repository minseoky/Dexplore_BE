package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.QrcodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QrcodeRepository extends JpaRepository<QrcodeEntity, String> {
}
