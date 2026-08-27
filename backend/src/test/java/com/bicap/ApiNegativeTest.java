package com.bicap;

import com.bicap.controller.*;
import com.bicap.exception.*;
import com.bicap.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Negative API tests.
 *
 * Mục tiêu:
 * - Kiểm tra API chuyển các lỗi nghiệp vụ/exception thành HTTP status đúng.
 * - Kiểm tra resource không tồn tại -> 404.
 * - Kiểm tra dữ liệu/nghiệp vụ không hợp lệ -> 400.
 * - Kiểm tra unauthorized -> 401.
 * - Kiểm tra forbidden -> 403.
 *
 * Đây là Controller/API negative test.
 * Business state thật (DB) sẽ được kiểm tra ở Integration Test sau.
 */
@ExtendWith(MockitoExtension.class)
class ApiNegativeTest {

    private MockMvc mockMvc;

    @Mock private AccountService accountService;
    @Mock private AuthService authService;
    @Mock private CartService cartService;
    @Mock private CropService cropService;
    @Mock private FarmService farmService;
    @Mock private FarmingSeasonService farmingSeasonService;
    @Mock private NotificationService notificationService;
    @Mock private OrderService orderService;
    @Mock private ProductBatchService productBatchService;
    @Mock private ProductService productService;
    @Mock private ProductImageService productImageService;
    @Mock private RetailerService retailerService;
    @Mock private SeasonActivityService seasonActivityService;
    @Mock private TraceabilityService traceabilityService;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new AccountController(accountService),
                        new AuthController(authService),
                        new CartController(cartService),
                        new CropController(cropService),
                        new FarmController(farmService),
                        new FarmingSeasonController(farmingSeasonService),
                        new NotificationController(notificationService),
                        new OrderController(orderService),
                        new ProductBatchController(productBatchService),
                        new ProductController(productService),
                        new ProductImageController(productImageService),
                        new RetailerController(retailerService),
                        new SeasonActivityController(seasonActivityService),
                        new TraceabilityController(traceabilityService)
                )
                .setValidator(validator)
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ============================================================
    // ACCOUNT
    // ============================================================

    @Test
    void getAccountById_notFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Account not found."))
                .when(accountService).getAccountById(999999L);

        request(get("/api/accounts/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Account not found."));

        verify(accountService).getAccountById(999999L);
    }

    @Test
    void createAccount_duplicateUsername_shouldReturn400() throws Exception {
        doThrow(new BadRequestException("Username already exists."))
                .when(accountService).createAccount(any());

        request(post("/api/accounts"), accountJson(
                "existing_user",
                "password",
                "Nguyen Van A",
                "unique@example.com",
                "0900000001",
                "FARM"
        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Username already exists."));
    }

    @Test
    void createAccount_duplicateEmail_shouldReturn400() throws Exception {
        doThrow(new BadRequestException("Email already exists."))
                .when(accountService).createAccount(any());

        request(post("/api/accounts"), accountJson(
                "unique_user",
                "password",
                "Nguyen Van A",
                "existing@example.com",
                "0900000002",
                "FARM"
        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists."));
    }

    @Test
    void createAccount_duplicatePhone_shouldReturn400() throws Exception {
        doThrow(new BadRequestException("Phone already exists."))
                .when(accountService).createAccount(any());

        request(post("/api/accounts"), accountJson(
                "unique_user",
                "password",
                "Nguyen Van A",
                "unique@example.com",
                "0900000000",
                "FARM"
        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Phone already exists."));
    }

    @Test
    void changePassword_wrongOldPassword_shouldReturn400() throws Exception {
        doThrow(new BadRequestException("Old password is incorrect."))
                .when(accountService).changePassword(any());

        request(put("/api/accounts/me/password"), """
                {
                  "oldPassword":"wrong-password",
                  "newPassword":"new-password"
                }
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Old password is incorrect."));
    }

    @Test
    void changeAccountStatus_invalidBusinessStatus_shouldReturn400() throws Exception {
        doThrow(new BadRequestException("Invalid account status."))
                .when(accountService).changeAccountStatus(eq(1L), any());

        request(patch("/api/accounts/1/status"), """
                {
                  "status":"INACTIVE"
                }
                """)
                .andExpect(status().isBadRequest());
    }

    // ============================================================
    // CROP
    // ============================================================

    @Test
    void getCropById_notFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Crop not found."))
                .when(cropService).getById(999999L);

        request(get("/api/crops/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Crop not found."));
    }

    @Test
    void updateCrop_notFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Crop not found."))
                .when(cropService).update(eq(999999L), any());

        request(put("/api/crops/999999"), """
                {
                  "cropName":"Updated Rice"
                }
                """)
                .andExpect(status().isNotFound());
    }

    // ============================================================
    // FARM
    // ============================================================

    @Test
    void getFarmById_notFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Farm not found."))
                .when(farmService).getFarmById(999999L);

        request(get("/api/farms/999999"))
                .andExpect(status().isNotFound());
    }

    // ============================================================
    // PRODUCT
    // ============================================================

    @Test
    void getProduct_notFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Product not found."))
                .when(productService).getMyProduct(999999L);

        request(get("/api/products/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found."));
    }

    @Test
    void createProduct_cropNotFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Crop not found."))
                .when(productService).create(any());

        request(post("/api/products"), """
                {
                  "cropId":999999,
                  "productName":"Rice",
                  "description":"Organic rice",
                  "unit":"kg"
                }
                """)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Crop not found."));
    }

    @Test
    void updateProduct_notFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Product not found."))
                .when(productService).update(eq(999999L), any());

        request(put("/api/products/999999"), """
                {
                  "cropId":1,
                  "productName":"Updated Rice",
                  "description":"Updated",
                  "unit":"kg"
                }
                """)
                .andExpect(status().isNotFound());
    }

    // ============================================================
    // PRODUCT BATCH
    // ============================================================

    @Test
    void getProductBatch_notFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Product batch not found."))
                .when(productBatchService).getById(999999L);

        request(get("/api/product-batches/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProductBatch_seasonNotFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Season not found."))
                .when(productBatchService).create(eq(1L), any());

        request(post("/api/products/1/batches"), """
                {
                  "seasonId":999999,
                  "grade":"A",
                  "quantity":10,
                  "unitPrice":10000
                }
                """)
                .andExpect(status().isNotFound());
    }

    // ============================================================
    // FARMING SEASON
    // ============================================================

    @Test
    void getFarmingSeason_notFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Farming season not found."))
                .when(farmingSeasonService).getMySeason(999999L);

        request(get("/api/farming-seasons/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void startFarmingSeason_invalidStatus_shouldReturn400() throws Exception {
        doThrow(new BadRequestException("Invalid farming season status."))
                .when(farmingSeasonService).start(1L);

        request(patch("/api/farming-seasons/1/start"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Invalid farming season status."
                ));
    }

    @Test
    void harvestFarmingSeason_invalidStatus_shouldReturn400() throws Exception {
        doThrow(new BadRequestException("Invalid farming season status."))
                .when(farmingSeasonService).harvest(1L);

        request(patch("/api/farming-seasons/1/harvest"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void finishFarmingSeason_invalidStatus_shouldReturn400() throws Exception {
        doThrow(new BadRequestException("Invalid farming season status."))
                .when(farmingSeasonService).finish(eq(1L), any());

        request(patch("/api/farming-seasons/1/finish"), """
                {
                  "actualHarvestDate":"2026-09-30"
                }
                """)
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelFarmingSeason_invalidStatus_shouldReturn400() throws Exception {
        doThrow(new BadRequestException("Cannot cancel farming season."))
                .when(farmingSeasonService).cancel(1L);

        request(patch("/api/farming-seasons/1/cancel"))
                .andExpect(status().isBadRequest());
    }

    // ============================================================
    // CART
    // ============================================================

    @Test
    void getMyCart_notFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Cart not found."))
                .when(cartService).getMyCart();

        request(get("/api/carts/me"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addCartItem_batchNotFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Product batch not found."))
                .when(cartService).addItem(any());

        request(post("/api/carts/items"), """
                {
                  "batchId":999999,
                  "quantity":1
                }
                """)
                .andExpect(status().isNotFound());
    }

    @Test
    void addCartItem_unavailableBatch_shouldReturn400() throws Exception {
        doThrow(new BadRequestException("Product batch is unavailable."))
                .when(cartService).addItem(any());

        request(post("/api/carts/items"), """
                {
                  "batchId":1,
                  "quantity":1
                }
                """)
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCartItem_notFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Cart item not found."))
                .when(cartService).updateItem(eq(999999L), any());

        request(put("/api/carts/items/999999"), """
                {
                  "quantity":2
                }
                """)
                .andExpect(status().isNotFound());
    }

    // ============================================================
    // ORDER
    // ============================================================

    @Test
    void checkout_emptyCart_shouldReturn400() throws Exception {
        doThrow(new BadRequestException("Cart is empty."))
                .when(orderService).checkout();

        request(post("/api/orders/checkout"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cart is empty."));
    }

    @Test
    void checkout_unavailableBatch_shouldReturn400() throws Exception {
        doThrow(new BadRequestException("Product batch is unavailable."))
                .when(orderService).checkout();

        request(post("/api/orders/checkout"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkout_exceedsRemainingQuantity_shouldReturn400() throws Exception {
        doThrow(new BadRequestException(
                "Quantity exceeds remaining quantity of batch B001"
        )).when(orderService).checkout();

        request(post("/api/orders/checkout"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRetailerOrder_notFoundOrNotOwned_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Order not found."))
                .when(orderService).getRetailerOrder(999999L);

        request(get("/api/orders/retailer/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFarmOrder_notFoundOrNotOwned_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Order not found."))
                .when(orderService).getFarmOrder(999999L);

        request(get("/api/orders/farm/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void confirmOrder_invalidStatus_shouldReturn400() throws Exception {
        doThrow(new BadRequestException("Invalid order status."))
                .when(orderService).confirmOrder(1L);

        request(patch("/api/orders/1/confirm"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid order status."));
    }

    @Test
    void cancelOrder_invalidStatus_shouldReturn400() throws Exception {
        doThrow(new BadRequestException("Invalid order status."))
                .when(orderService).cancelOrder(1L);

        request(patch("/api/orders/1/cancel"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void completeOrder_invalidStatus_shouldReturn400() throws Exception {
        doThrow(new BadRequestException("Invalid order status."))
                .when(orderService).completeOrder(1L);

        request(patch("/api/orders/1/complete"))
                .andExpect(status().isBadRequest());
    }

    // ============================================================
    // NOTIFICATION
    // ============================================================

    @Test
    void getNotification_notFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Notification not found."))
                .when(notificationService).getNotification(999999L);

        request(get("/api/notifications/999999"))
                .andExpect(status().isNotFound());
    }

    // ============================================================
    // RETAILER
    // ============================================================

    @Test
    void getMyRetailer_notFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Retailer not found."))
                .when(retailerService).getMyRetailer();

        request(get("/api/retailers/me"))
                .andExpect(status().isNotFound());
    }

    // ============================================================
    // SEASON ACTIVITY
    // ============================================================

    @Test
    void createSeasonActivity_seasonNotFound_shouldReturn404()
            throws Exception {

        doThrow(new ResourceNotFoundException("Season not found."))
                .when(seasonActivityService).create(eq(1L), any());

        request(post("/api/season-activities/season/1"), """
                {
                  "activityTypeId":1,
                  "activityTime":"2026-08-27T10:00:00",
                  "description":"Fertilize rice"
                }
                """)
                .andExpect(status().isNotFound());
    }

    // ============================================================
    // TRACEABILITY
    // ============================================================

    @Test
    void getTraceability_notFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Product batch not found."))
                .when(traceabilityService).getByBatchId(999999L);

        request(get("/api/public/traceability/batch/999999"))
                .andExpect(status().isNotFound());
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private ResultActions request(
            MockHttpServletRequestBuilder builder) throws Exception {

        return mockMvc.perform(builder);
    }

    private ResultActions request(
            MockHttpServletRequestBuilder builder,
            String json) throws Exception {

        return mockMvc.perform(
                builder
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        );
    }

    private String accountJson(
            String username,
            String password,
            String fullName,
            String email,
            String phone,
            String role) {

        return """
                {
                  "username":"%s",
                  "password":"%s",
                  "fullName":"%s",
                  "email":"%s",
                  "phone":"%s",
                  "role":"%s"
                }
                """.formatted(
                username,
                password,
                fullName,
                email,
                phone,
                role
        );
    }
}
