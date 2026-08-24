package com.bicap.entity;

import com.bicap.common.enums.ProductBatchStatus;
import com.bicap.common.enums.ProductGrade;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "product_batch",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_product_batch_batch_code",
                        columnNames = "batch_code"
                ),
                @UniqueConstraint(
                        name = "uq_product_batch_qr_code",
                        columnNames = "qr_code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long batchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private FarmingSeason farmingSeason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "batch_code", nullable = false, length = 50)
    private String batchCode;

    @Enumerated(EnumType.STRING)
        @Column(
                name = "grade",
                nullable = false,
                columnDefinition = "VARCHAR(1)"
        )
        private ProductGrade grade;

    @Column(name = "quantity", nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "qr_code", nullable = false, length = 255)
    private String qrCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProductBatchStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "remaining_quantity", nullable = false, precision = 15, scale = 2)
    private BigDecimal remainingQuantity;

    @OneToMany(mappedBy = "productBatch")
        @Builder.Default
        private List<CartItem> cartItems = new ArrayList<>();
        
    @OneToMany(mappedBy = "productBatch")
        @Builder.Default
        private List<ProductImage> productImages = new ArrayList<>();


}