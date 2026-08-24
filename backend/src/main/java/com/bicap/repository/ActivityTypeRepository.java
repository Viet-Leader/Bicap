package com.bicap.repository;

import com.bicap.entity.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityTypeRepository
        extends JpaRepository<ActivityType, Long> {

    List<ActivityType> findAllByOrderByActivityNameAsc();

    Optional<ActivityType> findByActivityTypeId(
            Long activityTypeId
    );

    Optional<ActivityType> findByActivityNameIgnoreCase(
            String activityName
    );

    boolean existsByActivityNameIgnoreCase(
            String activityName
    );
}