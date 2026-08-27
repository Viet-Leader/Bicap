package com.bicap;

import com.bicap.controller.*;
import com.bicap.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Functional API tests - Controller/API layer.
 *
 * Mục tiêu:
 * - Kiểm tra endpoint tồn tại và map đúng HTTP method/path.
 * - Kiểm tra HTTP status do Controller quy định.
 * - Kiểm tra Controller gọi đúng service method.
 *
 * Lưu ý:
 * Đây là functional test ở API/controller layer với MockMvc + Mockito.
 * Nó không kiểm tra database hay business logic bên trong Service.
 * Các test integration với DB sẽ được làm ở bước tiếp theo.
 */
@ExtendWith(MockitoExtension.class)
class ApiFunctionalTest {

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

        mockMvc = MockMvcBuilders.standaloneSetup(
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
        ).setValidator(validator)
        .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver()
        )
        .build();
    }

    // ============================================================
    // AUTHENTICATION
    // ============================================================

    @Test
    void login_shouldReturnOk() throws Exception {
        String json = """
                {
                  "username": "admin",
                  "password": "password"
                }
                """;

        request(post("/api/auth/login"), json)
                .andExpect(status().isOk());

        verify(authService).login(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void register_shouldReturnCreated() throws Exception {
        String json = """
                {
                  "username": "retailer01",
                  "password": "password",
                  "fullName": "Nguyen Van A",
                  "email": "retailer01@example.com",
                  "phone": "0123456789",
                  "retailerName": "Store",
                  "address": "Address"
                }
                """;

        request(post("/api/auth/register"), json)
                .andExpect(status().isCreated());

        verify(authService).register(org.mockito.ArgumentMatchers.any());
    }

    // ============================================================
    // ACCOUNT
    // ============================================================

    @Test
    void getAllAccounts_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk());

        verify(accountService).getAllAccounts();
    }

    @Test
    void getAccountById_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk());

        verify(accountService).getAccountById(1L);
    }

    @Test
    void createAccount_shouldReturnCreated() throws Exception {
        String json = """
                {
                  "username": "farmer01",
                  "password": "password",
                  "fullName": "Nguyen Van B",
                  "email": "farmer01@example.com",
                  "phone": "0123456789",
                  "role": "FARM"
                }
                """;

        request(post("/api/accounts"), json)
                .andExpect(status().isCreated());

        verify(accountService).createAccount(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateAccount_shouldReturnOk() throws Exception {
        String json = """
                {
                  "fullName": "Nguyen Van B Updated",
                  "email": "farmer.updated@example.com",
                  "phone": "0123456788"
                }
                """;

        request(put("/api/accounts/1"), json)
                .andExpect(status().isOk());

        verify(accountService).updateAccount(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any()
        );
    }

    // ============================================================
    // CROP
    // ============================================================

    @Test
    void getAllCrops_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/crops"))
                .andExpect(status().isOk());

        verify(cropService).getAll();
    }

    @Test
    void getCropById_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/crops/1"))
                .andExpect(status().isOk());

        verify(cropService).getById(1L);
    }

    @Test
    void createCrop_shouldReturnCreated() throws Exception {
        String json = """
                {
                  "cropName": "Rice"
                }
                """;

        request(post("/api/crops"), json)
                .andExpect(status().isCreated());

        verify(cropService).create(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void updateCrop_shouldReturnOk() throws Exception {
        String json = """
                {
                  "cropName": "Updated Rice"
                }
                """;

        request(put("/api/crops/1"), json)
                .andExpect(status().isOk());

        verify(cropService).update(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void changeCropStatus_shouldReturnOk() throws Exception {
        mockMvc.perform(patch("/api/crops/1/status"))
                .andExpect(status().isOk());

        verify(cropService).changeStatus(1L);
    }

    // ============================================================
    // FARM
    // ============================================================

    @Test
    void getMyFarm_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/farms/me"))
                .andExpect(status().isOk());

        verify(farmService).getMyFarm();
    }

    @Test
    void updateMyFarm_shouldReturnOk() throws Exception {
        String json = """
                {
                  "farmName": "Farm A",
                  "address": "Address A",
                  "description": "Description"
                }
                """;

        request(put("/api/farms/me"), json)
                .andExpect(status().isOk());

        verify(farmService).updateMyFarm(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void getFarmById_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/farms/1"))
                .andExpect(status().isOk());

        verify(farmService).getFarmById(1L);
    }

    // ============================================================
    // PRODUCT
    // ============================================================

    @Test
    void getMyProducts_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());

        verify(productService).getMyProducts(
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void getMyProduct_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk());

        verify(productService).getMyProduct(1L);
    }

    @Test
    void createProduct_shouldReturnCreated() throws Exception {
        String json = """
                {
                  "cropId": 1,
                  "productName": "Rice",
                  "description": "Organic rice",
                  "unit": "kg"
                }
                """;

        request(post("/api/products"), json)
                .andExpect(status().isCreated());

        verify(productService).create(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void updateProduct_shouldReturnOk() throws Exception {
        String json = """
                {
                "cropId": 1,
                "productName": "Updated Rice",
                "description": "Updated description",
                "unit": "kg"
                }
                """;

        request(put("/api/products/1"), json)
                .andExpect(status().isOk());

        verify(productService).update(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void changeProductStatus_shouldReturnOk() throws Exception {
        mockMvc.perform(patch("/api/products/1/status"))
                .andExpect(status().isOk());

        verify(productService).changeStatus(1L);
    }

    // ============================================================
    // PRODUCT BATCH
    // ============================================================

    @Test
    void getProductBatches_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/products/1/batches"))
                .andExpect(status().isOk());

        verify(productBatchService).getByProduct(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void getProductBatchById_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/product-batches/1"))
                .andExpect(status().isOk());

        verify(productBatchService).getById(1L);
    }

    @Test
    void createProductBatch_shouldReturnCreated() throws Exception {
        String json = """
                {
                  "seasonId": 1,
                  "grade": "A",
                  "quantity": 10,
                  "unitPrice": 10000
                }
                """;

        request(post("/api/products/1/batches"), json)
                .andExpect(status().isCreated());

        verify(productBatchService).create(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void updateProductBatch_shouldReturnOk() throws Exception {
        String json = """
                {
                "grade": "A",
                "quantity": 20,
                "unitPrice": 12000
                }
                """;

        request(put("/api/product-batches/1"), json)
                .andExpect(status().isOk());

        verify(productBatchService).update(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void recordProductBatchToBlockchain_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/product-batches/1/blockchain"))
                .andExpect(status().isOk());

        verify(productBatchService).recordToBlockchain(1L);
    }

    // ============================================================
    // FARMING SEASON
    // ============================================================

    @Test
    void getMySeasons_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/farming-seasons"))
                .andExpect(status().isOk());

        verify(farmingSeasonService).getMySeasons(
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void getMySeason_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/farming-seasons/1"))
                .andExpect(status().isOk());

        verify(farmingSeasonService).getMySeason(1L);
    }

    @Test
    void createFarmingSeason_shouldReturnCreated() throws Exception {
        String json = """
                {
                  "seasonName": "Summer 2026",
                  "plantingDate": "2026-08-01",
                  "expectedHarvestDate": "2026-09-30"
                }
                """;

        request(post("/api/farming-seasons"), json)
                .andExpect(status().isCreated());

        verify(farmingSeasonService).create(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void updateFarmingSeason_shouldReturnOk() throws Exception {
        String json = """
                {
                  "seasonName": "Updated Season",
                  "plantingDate": "2026-08-01",
                  "expectedHarvestDate": "2026-10-01"
                }
                """;

        request(put("/api/farming-seasons/1"), json)
                .andExpect(status().isOk());

        verify(farmingSeasonService).update(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void startFarmingSeason_shouldReturnOk() throws Exception {
        mockMvc.perform(patch("/api/farming-seasons/1/start"))
                .andExpect(status().isOk());

        verify(farmingSeasonService).start(1L);
    }

    @Test
    void harvestFarmingSeason_shouldReturnOk() throws Exception {
        mockMvc.perform(patch("/api/farming-seasons/1/harvest"))
                .andExpect(status().isOk());

        verify(farmingSeasonService).harvest(1L);
    }

    @Test
    void finishFarmingSeason_shouldReturnOk() throws Exception {
        String json = """
                {
                  "actualHarvestDate": "2026-09-30"
                }
                """;

        request(patch("/api/farming-seasons/1/finish"), json)
                .andExpect(status().isOk());

        verify(farmingSeasonService).finish(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void cancelFarmingSeason_shouldReturnOk() throws Exception {
        mockMvc.perform(patch("/api/farming-seasons/1/cancel"))
                .andExpect(status().isOk());

        verify(farmingSeasonService).cancel(1L);
    }

    // ============================================================
    // CART
    // ============================================================

    @Test
    void getMyCart_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/carts/me"))
                .andExpect(status().isOk());

        verify(cartService).getMyCart();
    }

    @Test
    void addCartItem_shouldReturnCreated() throws Exception {
        String json = """
                {
                  "batchId": 1,
                  "quantity": 2
                }
                """;

        request(post("/api/carts/items"), json)
                .andExpect(status().isCreated());

        verify(cartService).addItem(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void updateCartItem_shouldReturnOk() throws Exception {
        String json = """
                {
                  "quantity": 3
                }
                """;

        request(put("/api/carts/items/1"), json)
                .andExpect(status().isOk());

        verify(cartService).updateItem(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void removeCartItem_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/carts/items/1"))
                .andExpect(status().isNoContent());

        verify(cartService).removeItem(1L);
    }

    @Test
    void clearCart_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/carts/me"))
                .andExpect(status().isNoContent());

        verify(cartService).clearCart();
    }

    // ============================================================
    // ORDER
    // ============================================================

    @Test
    void checkout_shouldReturnCreated() throws Exception {
        mockMvc.perform(post("/api/orders/checkout"))
                .andExpect(status().isCreated());

        verify(orderService).checkout();
    }

    @Test
    void getRetailerOrders_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/orders/retailer"))
                .andExpect(status().isOk());

        verify(orderService).getRetailerOrders();
    }

    @Test
    void getRetailerOrder_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/orders/retailer/1"))
                .andExpect(status().isOk());

        verify(orderService).getRetailerOrder(1L);
    }

    @Test
    void getFarmOrders_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/orders/farm"))
                .andExpect(status().isOk());

        verify(orderService).getFarmOrders();
    }

    @Test
    void getFarmOrder_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/orders/farm/1"))
                .andExpect(status().isOk());

        verify(orderService).getFarmOrder(1L);
    }

    @Test
    void confirmOrder_shouldReturnOk() throws Exception {
        mockMvc.perform(patch("/api/orders/1/confirm"))
                .andExpect(status().isOk());

        verify(orderService).confirmOrder(1L);
    }

    @Test
    void cancelOrder_shouldReturnOk() throws Exception {
        mockMvc.perform(patch("/api/orders/1/cancel"))
                .andExpect(status().isOk());

        verify(orderService).cancelOrder(1L);
    }

    @Test
    void completeOrder_shouldReturnOk() throws Exception {
        mockMvc.perform(patch("/api/orders/1/complete"))
                .andExpect(status().isOk());

        verify(orderService).completeOrder(1L);
    }

    // ============================================================
    // NOTIFICATION
    // ============================================================

    @Test
    void getMyNotifications_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk());

        verify(notificationService).getMyNotifications();
    }

    @Test
    void getNotification_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/notifications/1"))
                .andExpect(status().isOk());

        verify(notificationService).getNotification(1L);
    }

    @Test
    void markNotificationAsRead_shouldReturnOk() throws Exception {
        mockMvc.perform(patch("/api/notifications/1/read"))
                .andExpect(status().isOk());

        verify(notificationService).markAsRead(1L);
    }

    @Test
    void markAllNotificationsAsRead_shouldReturnNoContent() throws Exception {
        mockMvc.perform(patch("/api/notifications/read-all"))
                .andExpect(status().isNoContent());

        verify(notificationService).markAllAsRead();
    }

    // ============================================================
    // RETAILER
    // ============================================================

    @Test
    void getMyRetailer_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/retailers/me"))
                .andExpect(status().isOk());

        verify(retailerService).getMyRetailer();
    }

    @Test
    void updateMyRetailer_shouldReturnOk() throws Exception {
        String json = """
                {
                  "retailerName": "Store A",
                  "address": "Address A"
                }
                """;

        request(put("/api/retailers/me"), json)
                .andExpect(status().isOk());

        verify(retailerService).updateMyRetailer(
                org.mockito.ArgumentMatchers.any()
        );
    }

    // ============================================================
    // TRACEABILITY
    // ============================================================

    @Test
    void getTraceabilityByBatchId_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/public/traceability/batch/1"))
                .andExpect(status().isOk());

        verify(traceabilityService).getByBatchId(1L);
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private ResultActions request(
            MockHttpServletRequestBuilder builder,
            String json) throws Exception {

        return mockMvc.perform(
                builder
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        );
    }
}