package com.bicap.entity;

import java.util.ArrayList;
import java.util.List;

import com.bicap.common.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "crop")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crop_id")
    private Long cropId;

    @Column(name = "crop_name", nullable = false, length = 100, unique = true)
    private String cropName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AccountStatus status;

    @OneToMany(mappedBy = "crop")
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}