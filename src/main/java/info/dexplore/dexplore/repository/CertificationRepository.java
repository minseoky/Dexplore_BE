package info.dexplore.dexplore.repository;

import info.dexplore.dexplore.entity.CertificationEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificationRepository extends JpaRepository<CertificationEntity, String> {

    CertificationEntity findByUserId(String userId);

    @Transactional
    void deleteByUserId(String userId);

}
