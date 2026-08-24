package com.bicap.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "farm")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "farm_id")
    private Long farmId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    @Column(name = "farm_name", nullable = false, length = 150)
    private String farmName;

    @Column(name = "business_license", nullable = false, unique = true, length = 100)
    private String businessLicense;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "farm")
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "farm")
    @Builder.Default
    private List<FarmingSeason> farmingSeasons = new ArrayList<>();

    @OneToMany(mappedBy = "farm")
    @Builder.Default
    private List<Orders> orders = new ArrayList<>();

    @OneToMany(mappedBy = "farm")
    @Builder.Default
    private List<Cart> carts = new ArrayList<>();
}