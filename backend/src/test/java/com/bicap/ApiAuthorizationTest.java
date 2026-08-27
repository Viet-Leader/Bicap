package com.bicap;

import com.bicap.service.AccountService;
import com.bicap.service.AuthService;
import com.bicap.service.CartService;
import com.bicap.service.CropService;
import com.bicap.service.FarmService;
import com.bicap.service.FarmingSeasonService;
import com.bicap.service.NotificationService;
import com.bicap.service.OrderService;
import com.bicap.service.ProductBatchService;
import com.bicap.service.ProductImageService;
import com.bicap.service.ProductService;
import com.bicap.service.RetailerService;
import com.bicap.service.SeasonActivityService;
import com.bicap.service.TraceabilityService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@SpringBootTest
class ApiAuthorizationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private CropService cropService;

    @MockitoBean
    private FarmService farmService;

    @MockitoBean
    private FarmingSeasonService farmingSeasonService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private ProductBatchService productBatchService;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductImageService productImageService;

    @MockitoBean
    private RetailerService retailerService;

    @MockitoBean
    private SeasonActivityService seasonActivityService;

    @MockitoBean
    private TraceabilityService traceabilityService;

    // ============================================================
    // UNAUTHENTICATED
    // ============================================================

    @Test
    void accountApi_withoutAuthentication_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void farmApi_withoutAuthentication_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/farms/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void retailerApi_withoutAuthentication_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/retailers/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cartApi_withoutAuthentication_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/carts/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void orderApi_withoutAuthentication_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/orders/retailer"))
                .andExpect(status().isUnauthorized());
    }

    // ============================================================
    // ACCOUNT - ADMIN ONLY
    // ============================================================

    @Test
    void accountApi_farmer_shouldReturn403() throws Exception {
        mockMvc.perform(
                get("/api/accounts")
                        .with(user("farmer").roles("FARM"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void accountApi_retailer_shouldReturn403() throws Exception {
        mockMvc.perform(
                get("/api/accounts")
                        .with(user("retailer").roles("RETAILER"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void createAccount_farmer_shouldReturn403() throws Exception {
        mockMvc.perform(
                post("/api/accounts")
                        .with(user("farmer").roles("FARM"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void createAccount_retailer_shouldReturn403() throws Exception {
        mockMvc.perform(
                post("/api/accounts")
                        .with(user("retailer").roles("RETAILER"))
        ).andExpect(status().isForbidden());
    }

    // ============================================================
    // FARM - FARM ONLY
    // ============================================================

    @Test
    void farmMe_retailer_shouldReturn403() throws Exception {
        mockMvc.perform(
                get("/api/farms/me")
                        .with(user("retailer").roles("RETAILER"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void farmMe_admin_shouldReturn403() throws Exception {
        mockMvc.perform(
                get("/api/farms/me")
                        .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void farmUpdate_retailer_shouldReturn403() throws Exception {
        mockMvc.perform(
                put("/api/farms/me")
                        .with(user("retailer").roles("RETAILER"))
        ).andExpect(status().isForbidden());
    }

    // ============================================================
    // RETAILER - RETAILER ONLY
    // ============================================================

    @Test
    void retailerMe_farmer_shouldReturn403() throws Exception {
        mockMvc.perform(
                get("/api/retailers/me")
                        .with(user("farmer").roles("FARM"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void retailerMe_admin_shouldReturn403() throws Exception {
        mockMvc.perform(
                get("/api/retailers/me")
                        .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isForbidden());
    }

    // ============================================================
    // PRODUCT - FARM ONLY
    //
    // SecurityConfig uses /api/products/**, while the actual
    // ProductController uses /products/**.
    //
    // These tests intentionally verify the intended authorization.
    // If they fail with 200/other status, inspect the URL mismatch.
    // ============================================================

    @Test
    void productApi_retailer_shouldReturn403() throws Exception {
        mockMvc.perform(
                get("/api/products")
                        .with(user("retailer").roles("RETAILER"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void productCreate_retailer_shouldReturn403() throws Exception {
        mockMvc.perform(
                post("/api/products")
                        .with(user("retailer").roles("RETAILER"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void productApi_admin_shouldReturn403() throws Exception {
        mockMvc.perform(
                get("/api/products")
                        .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isForbidden());
    }

    // ============================================================
    // FARMING SEASON - FARM ONLY
    // ============================================================

    @Test
    void farmingSeasonApi_retailer_shouldReturn403() throws Exception {
        mockMvc.perform(
                get("/api/farming-seasons")
                        .with(user("retailer").roles("RETAILER"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void farmingSeasonApi_admin_shouldReturn403() throws Exception {
        mockMvc.perform(
                get("/api/farming-seasons")
                        .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isForbidden());
    }

    // ============================================================
    // PRODUCT BATCH - FARM ONLY
    // ============================================================

    @Test
    void productBatchApi_retailer_shouldReturn403() throws Exception {
        mockMvc.perform(
                get("/api/product-batches/1")
                        .with(user("retailer").roles("RETAILER"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void productBatchApi_admin_shouldReturn403() throws Exception {
        mockMvc.perform(
                get("/api/product-batches/1")
                        .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isForbidden());
    }

    // ============================================================
    // CART - RETAILER ONLY
    // ============================================================

    @Test
    void cartApi_farmer_shouldReturn403() throws Exception {
        mockMvc.perform(
                get("/api/carts/me")
                        .with(user("farmer").roles("FARM"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void cartApi_admin_shouldReturn403() throws Exception {
        mockMvc.perform(
                get("/api/carts/me")
                        .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isForbidden());
    }

    // ============================================================
    // ORDER
    // ============================================================

    @Test
    void checkout_farmer_shouldReturn403() throws Exception {
        mockMvc.perform(
                post("/api/orders/checkout")
                        .with(user("farmer").roles("FARM"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void checkout_admin_shouldReturn403() throws Exception {
        mockMvc.perform(
                post("/api/orders/checkout")
                        .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void confirmOrder_retailer_shouldReturn403() throws Exception {
        mockMvc.perform(
                patch("/api/orders/1/confirm")
                        .with(user("retailer").roles("RETAILER"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void confirmOrder_admin_shouldReturn403() throws Exception {
        mockMvc.perform(
                patch("/api/orders/1/confirm")
                        .with(user("admin").roles("ADMIN"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void completeOrder_farmer_shouldReturn403() throws Exception {
        mockMvc.perform(
                patch("/api/orders/1/complete")
                        .with(user("farmer").roles("FARM"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void completeOrder_retailer_shouldReturn403() throws Exception {
        mockMvc.perform(
                patch("/api/orders/1/complete")
                        .with(user("retailer").roles("RETAILER"))
        ).andExpect(status().isForbidden());
    }

    // ============================================================
    // NOTIFICATION
    // ============================================================

    @Test
    void notificationApi_unauthorizedRole_shouldReturn403() throws Exception {
        mockMvc.perform(
                get("/api/notifications")
                        .with(user("guest").roles("GUEST"))
        ).andExpect(status().isForbidden());
    }

    // ============================================================
    // PUBLIC API
    // ============================================================

    @Test
    void publicTraceability_withoutAuthentication_shouldNotBe401()
            throws Exception {

        mockMvc.perform(
                get("/api/public/traceability/batch/999999")
        )
        /*
         * Public endpoint must not be rejected by authentication.
         * The actual business result may be 404 because batch 999999
         * does not exist; that is acceptable for this authorization test.
         */
        .andExpect(result -> {
            int code = result.getResponse().getStatus();
            if (code == 401) {
                throw new AssertionError(
                        "Public traceability endpoint unexpectedly returned 401"
                );
            }
        });
    }
}
