package com.bicap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "activity_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_type_id")
    private Long activityTypeId;

    @Column(name = "activity_name", nullable = false, unique = true, length = 100)
    private String activityName;

}