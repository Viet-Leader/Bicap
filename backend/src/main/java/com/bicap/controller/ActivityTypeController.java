package com.bicap.controller;

import com.bicap.entity.ActivityType;
import com.bicap.repository.ActivityTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/activity-types")
public class ActivityTypeController {

    private final ActivityTypeRepository activityTypeRepository;

    /**
     * Danh sach loai hoat dong (Gieo trong, Bon phan, Phun thuoc, Thu hoach...).
     * Frontend dung de hien thi dropdown khi tao su kien.
     */
    @GetMapping
    public ResponseEntity<List<ActivityType>> getAll() {
        return ResponseEntity.ok(
                activityTypeRepository.findAllByOrderByActivityNameAsc()
        );
    }
}
