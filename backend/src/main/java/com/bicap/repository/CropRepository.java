package com.bicap.repository;

import com.bicap.common.enums.AccountStatus;
import com.bicap.entity.Crop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CropRepository extends JpaRepository<Crop, Long> {

    Optional<Crop> findByCropNameIgnoreCase(String cropName);

    boolean existsByCropNameIgnoreCase(String cropName);

    List<Crop> findByStatus(AccountStatus status);

    Optional<Crop> findByCropIdAndStatus(
            Long cropId,
            AccountStatus status
    );
}