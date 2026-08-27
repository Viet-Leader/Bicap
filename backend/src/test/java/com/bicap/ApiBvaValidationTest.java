package com.bicap;

import com.bicap.controller.*;
import com.bicap.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ApiBvaValidationTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Mock private AccountService accountService;
    @Mock private AuthService authService;
    @Mock private CartService cartService;
    @Mock private CropService cropService;
    @Mock private FarmService farmService;
    @Mock private FarmingSeasonService farmingSeasonService;
    @Mock private ProductService productService;
    @Mock private ProductBatchService productBatchService;
    @Mock private ProductImageService productImageService;
    @Mock private RetailerService retailerService;
    @Mock private SeasonActivityService seasonActivityService;

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
                new ProductController(productService),
                new ProductBatchController(productBatchService),
                new ProductImageController(productImageService),
                new RetailerController(retailerService),
                new SeasonActivityController(seasonActivityService)
        ).setValidator(validator).build();
    }

    // ============================================================
    // ACCOUNT CREATE
    // String max-boundaries:
    // username 50, password 6..255, fullName 100, email 100, phone 20
    // ============================================================

    @ParameterizedTest(name = "username length={0}")
    @ValueSource(ints = {0, 1, 2, 49, 50, 51})
    void createAccount_username_bva(int length) throws Exception {
        String username = "u".repeat(Math.max(0, length));
        String json = """
                {
                  "username":"%s",
                  "password":"password",
                  "fullName":"Nguyen Van A",
                  "email":"a@example.com",
                  "phone":"0123456789",
                  "role":"RETAILER"
                }
                """.formatted(username);

        int expected = (length >= 1 && length <= 50) ? 201 : 400;
        request(post("/api/accounts"), json).andExpect(status().is(expected));
    }

    @ParameterizedTest(name = "password length={0}")
    @ValueSource(ints = {5, 6, 7, 254, 255, 256})
    void createAccount_password_bva(int length) throws Exception {
        String password = "p".repeat(length);
        String json = """
                {
                  "username":"user01",
                  "password":"%s",
                  "fullName":"Nguyen Van A",
                  "email":"a@example.com",
                  "phone":"0123456789",
                  "role":"RETAILER"
                }
                """.formatted(password);

        int expected = (length >= 6 && length <= 255) ? 201 : 400;
        request(post("/api/accounts"), json).andExpect(status().is(expected));
    }

    @ParameterizedTest(name = "fullName length={0}")
    @ValueSource(ints = {0, 1, 2, 99, 100, 101})
    void createAccount_fullName_bva(int length) throws Exception {
        String fullName = "N".repeat(Math.max(0, length));
        String json = """
                {
                  "username":"user01",
                  "password":"password",
                  "fullName":"%s",
                  "email":"a@example.com",
                  "phone":"0123456789",
                  "role":"RETAILER"
                }
                """.formatted(fullName);

        int expected = (length >= 1 && length <= 100) ? 201 : 400;
        request(post("/api/accounts"), json).andExpect(status().is(expected));
    }

    @ParameterizedTest(name = "email length={0}")
    @ValueSource(ints = {99, 100, 101})
    void createAccount_email_size_bva(int length) throws Exception {

        String email = emailOfLength(length);

        assertEquals(length, email.length());

        String json = """
                {
                "username":"user01",
                "password":"password",
                "fullName":"Nguyen Van A",
                "email":"%s",
                "phone":"0123456789",
                "role":"RETAILER"
                }
                """.formatted(email);

        int expected = length <= 100 ? 201 : 400;

        request(post("/api/accounts"), json)
                .andExpect(status().is(expected));
    }

    @ParameterizedTest(name = "phone length={0}")
    @ValueSource(ints = {0, 1, 2, 19, 20, 21})
    void createAccount_phone_bva(int length) throws Exception {
        String phone = "1".repeat(Math.max(0, length));
        String json = """
                {
                  "username":"user01",
                  "password":"password",
                  "fullName":"Nguyen Van A",
                  "email":"a@example.com",
                  "phone":"%s",
                  "role":"RETAILER"
                }
                """.formatted(phone);

        int expected = (length >= 1 && length <= 20) ? 201 : 400;
        request(post("/api/accounts"), json).andExpect(status().is(expected));
    }

    // ============================================================
    // AUTH REGISTER
    // ============================================================

    @ParameterizedTest(name = "retailerName length={0}")
    @ValueSource(ints = {0, 1, 2, 149, 150, 151})
    void register_retailerName_bva(int length) throws Exception {
        String value = "R".repeat(Math.max(0, length));
        String json = """
                {
                  "username":"user01",
                  "password":"password",
                  "fullName":"Nguyen Van A",
                  "email":"a@example.com",
                  "phone":"0123456789",
                  "retailerName":"%s",
                  "address":"12 Nguyen Trai"
                }
                """.formatted(value);

        int expected = (length >= 1 && length <= 150) ? 201 : 400;
        request(post("/api/auth/register"), json).andExpect(status().is(expected));
    }

    @ParameterizedTest(name = "address length={0}")
    @ValueSource(ints = {0, 1, 2, 254, 255, 256})
    void register_address_bva(int length) throws Exception {
        String value = "A".repeat(Math.max(0, length));
        String json = """
                {
                  "username":"user01",
                  "password":"password",
                  "fullName":"Nguyen Van A",
                  "email":"a@example.com",
                  "phone":"0123456789",
                  "retailerName":"Store",
                  "address":"%s"
                }
                """.formatted(value);

        int expected = (length >= 1 && length <= 255) ? 201 : 400;
        request(post("/api/auth/register"), json).andExpect(status().is(expected));
    }

    // ============================================================
    // CART
    // DecimalMin 0.01 inclusive => 0.00, 0.01, 0.02
    // ============================================================

    @ParameterizedTest(name = "cart quantity={0}")
    @ValueSource(strings = {"-0.01", "0.00", "0.01", "0.02"})
    void addCartItem_quantity_bva(String quantity) throws Exception {
        String json = """
                {"batchId":1,"quantity":%s}
                """.formatted(quantity);

        int expected = new BigDecimal(quantity).compareTo(new BigDecimal("0.01")) >= 0 ? 201 : 400;
        request(post("/api/carts/items"), json).andExpect(status().is(expected));
    }

    @ParameterizedTest(name = "cart update quantity={0}")
    @ValueSource(strings = {"-0.01", "0.00", "0.01", "0.02"})
    void updateCartItem_quantity_bva(String quantity) throws Exception {
        String json = """
                {"quantity":%s}
                """.formatted(quantity);

        int expected = new BigDecimal(quantity).compareTo(new BigDecimal("0.01")) >= 0 ? 200 : 400;
        request(put("/api/carts/items/1"), json).andExpect(status().is(expected));
    }

    // ============================================================
    // CROP
    // ============================================================

    @ParameterizedTest(name = "cropName length={0}")
    @ValueSource(ints = {0, 1, 2, 99, 100, 101})
    void createCrop_cropName_bva(int length) throws Exception {
        String value = "C".repeat(Math.max(0, length));
        int expected = (length >= 1 && length <= 100) ? 201 : 400;
        request(post("/api/crops"), """
                {"cropName":"%s"}
                """.formatted(value)).andExpect(status().is(expected));
    }

    @ParameterizedTest(name = "cropName update length={0}")
    @ValueSource(ints = {0, 1, 2, 99, 100, 101})
    void updateCrop_cropName_bva(int length) throws Exception {
        String value = "C".repeat(Math.max(0, length));
        int expected = (length >= 1 && length <= 100) ? 200 : 400;
        request(put("/api/crops/1"), """
                {"cropName":"%s"}
                """.formatted(value)).andExpect(status().is(expected));
    }

    // ============================================================
    // FARM
    // ============================================================

    @ParameterizedTest(name = "farmName length={0}")
    @ValueSource(ints = {0, 1, 2, 149, 150, 151})
    void updateFarm_farmName_bva(int length) throws Exception {
        String value = "F".repeat(Math.max(0, length));
        int expected = (length >= 1 && length <= 150) ? 200 : 400;
        request(put("/api/farms/me"), """
                {"farmName":"%s","address":"Address","description":"Desc"}
                """.formatted(value)).andExpect(status().is(expected));
    }

    @ParameterizedTest(name = "address length={0}")
    @ValueSource(ints = {0, 1, 2, 254, 255, 256})
    void updateFarm_address_bva(int length) throws Exception {
        String value = "A".repeat(Math.max(0, length));
        int expected = (length >= 1 && length <= 255) ? 200 : 400;
        request(put("/api/farms/me"), """
                {"farmName":"Farm","address":"%s","description":"Desc"}
                """.formatted(value)).andExpect(status().is(expected));
    }

    @ParameterizedTest(name = "description length={0}")
    @ValueSource(ints = {0, 1, 2, 999, 1000, 1001})
    void updateFarm_description_bva(int length) throws Exception {
        String value = "D".repeat(length);
        int expected = length <= 1000 ? 200 : 400;
        request(put("/api/farms/me"), """
                {"farmName":"Farm","address":"Address","description":"%s"}
                """.formatted(value)).andExpect(status().is(expected));
    }

    // ============================================================
    // PRODUCT
    // ============================================================

    @ParameterizedTest(name = "productName length={0}")
    @ValueSource(ints = {0, 1, 2, 149, 150, 151})
    void createProduct_productName_bva(int length) throws Exception {
        String value = "P".repeat(Math.max(0, length));
        int expected = (length >= 1 && length <= 150) ? 201 : 400;
        request(post("/api/products"), """
                {"cropId":1,"productName":"%s","description":"Desc","unit":"kg"}
                """.formatted(value)).andExpect(status().is(expected));
    }

    @ParameterizedTest(name = "description length={0}")
    @ValueSource(ints = {0, 1, 2, 999, 1000, 1001})
    void createProduct_description_bva(int length) throws Exception {
        String value = "D".repeat(length);
        int expected = length <= 1000 ? 201 : 400;
        request(post("/api/products"), """
                {"cropId":1,"productName":"Product","description":"%s","unit":"kg"}
                """.formatted(value)).andExpect(status().is(expected));
    }

    @ParameterizedTest(name = "unit length={0}")
    @ValueSource(ints = {0, 1, 2, 19, 20, 21})
    void createProduct_unit_bva(int length) throws Exception {
        String value = "U".repeat(Math.max(0, length));
        int expected = (length >= 1 && length <= 20) ? 201 : 400;
        request(post("/api/products"), """
                {"cropId":1,"productName":"Product","description":"Desc","unit":"%s"}
                """.formatted(value)).andExpect(status().is(expected));
    }

    // ============================================================
    // PRODUCT BATCH
    // @Positive => boundary at zero; no upper bound exists in source.
    // ============================================================

    @ParameterizedTest(name = "quantity={0}")
    @ValueSource(strings = {"-0.01", "0", "0.01"})
    void createProductBatch_quantity_bva(String quantity) throws Exception {
        int expected = new BigDecimal(quantity).compareTo(BigDecimal.ZERO) > 0 ? 201 : 400;
        request(post("/api/products/1/batches"), """
                {"seasonId":1,"grade":"A","quantity":%s,"unitPrice":1000}
                """.formatted(quantity)).andExpect(status().is(expected));
    }

    @ParameterizedTest(name = "unitPrice={0}")
    @ValueSource(strings = {"-0.01", "0", "0.01"})
    void createProductBatch_unitPrice_bva(String price) throws Exception {
        int expected = new BigDecimal(price).compareTo(BigDecimal.ZERO) > 0 ? 201 : 400;
        request(post("/api/products/1/batches"), """
                {"seasonId":1,"grade":"A","quantity":1,"unitPrice":%s}
                """.formatted(price)).andExpect(status().is(expected));
    }

    @ParameterizedTest(name = "quantity update={0}")
    @ValueSource(strings = {"-0.01", "0", "0.01"})
    void updateProductBatch_quantity_bva(String quantity) throws Exception {
        int expected = new BigDecimal(quantity).compareTo(BigDecimal.ZERO) > 0 ? 200 : 400;
        request(put("/api/product-batches/1"), """
                {"grade":"A","quantity":%s,"unitPrice":1000}
                """.formatted(quantity)).andExpect(status().is(expected));
    }

    @ParameterizedTest(name = "unitPrice update={0}")
    @ValueSource(strings = {"-0.01", "0", "0.01"})
    void updateProductBatch_unitPrice_bva(String price) throws Exception {
        int expected = new BigDecimal(price).compareTo(BigDecimal.ZERO) > 0 ? 200 : 400;
        request(put("/api/product-batches/1"), """
                {"grade":"A","quantity":1,"unitPrice":%s}
                """.formatted(price)).andExpect(status().is(expected));
    }

    // ============================================================
    // FARMING SEASON
    // @Future expectedHarvestDate => today is invalid, tomorrow valid.
    // ============================================================

    @Test
    void createFarmingSeason_expectedHarvestDate_today_bva() throws Exception {
        LocalDate today = LocalDate.now();
        String json = """
                {"seasonName":"Season","plantingDate":"%s","expectedHarvestDate":"%s"}
                """.formatted(today.minusDays(10), today);
        request(post("/api/farming-seasons"), json).andExpect(status().isBadRequest());
    }

    @Test
    void createFarmingSeason_expectedHarvestDate_tomorrow_bva() throws Exception {
        LocalDate today = LocalDate.now();
        String json = """
                {"seasonName":"Season","plantingDate":"%s","expectedHarvestDate":"%s"}
                """.formatted(today.minusDays(10), today.plusDays(1));
        request(post("/api/farming-seasons"), json).andExpect(status().isCreated());
    }

    // ============================================================
    // RETAILER
    // ============================================================

    @ParameterizedTest(name = "retailerName length={0}")
    @ValueSource(ints = {0, 1, 2, 149, 150, 151})
    void updateRetailer_retailerName_bva(int length) throws Exception {
        String value = "R".repeat(Math.max(0, length));
        int expected = (length >= 1 && length <= 150) ? 200 : 400;
        request(put("/api/retailers/me"), """
                {"retailerName":"%s","address":"Address"}
                """.formatted(value)).andExpect(status().is(expected));
    }

    @ParameterizedTest(name = "retailer address length={0}")
    @ValueSource(ints = {0, 1, 2, 254, 255, 256})
    void updateRetailer_address_bva(int length) throws Exception {
        String value = "A".repeat(Math.max(0, length));
        int expected = (length >= 1 && length <= 255) ? 200 : 400;
        request(put("/api/retailers/me"), """
                {"retailerName":"Store","address":"%s"}
                """.formatted(value)).andExpect(status().is(expected));
    }

    // ============================================================
    // PRODUCT IMAGE REORDER
    // @NotEmpty => empty list invalid, one element valid.
    // ============================================================

    @Test
    void reorderProductImage_emptyList_bva() throws Exception {
        request(patch("/api/product-images/batch/1/reorder"), """
                {"imageIds":[]}
                """).andExpect(status().isBadRequest());
    }

    @Test
    void reorderProductImage_oneElement_bva() throws Exception {
        request(patch("/api/product-images/batch/1/reorder"), """
                {"imageIds":[1]}
                """).andExpect(status().isOk());
    }

    // ============================================================
    // SEASON ACTIVITY
    // @NotBlank description => empty invalid, one non-space char valid.
    // ============================================================

    @Test
    void createSeasonActivity_description_empty_bva() throws Exception {
        request(post("/api/season-activities/season/1"), """
                {"activityTypeId":1,"activityTime":"2026-08-27T10:00:00","description":""}
                """).andExpect(status().isBadRequest());
    }

    @Test
    void createSeasonActivity_description_oneChar_bva() throws Exception {
        request(post("/api/season-activities/season/1"), """
                {"activityTypeId":1,"activityTime":"2026-08-27T10:00:00","description":"A"}
                """).andExpect(status().isCreated());
    }

    // ============================================================
    // Helpers
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

   private String emailOfLength(int length) {
    if (length < 6) {
        throw new IllegalArgumentException(
                "Length must be at least 6 characters");
    }

    // Local-part tối đa 64 ký tự.
    // Dùng local-part ngắn để có thể tạo email dài đến 100+ ký tự
    // bằng cách mở rộng domain.
    String localPart = "test";

    int remaining = length - localPart.length() - 1; // bỏ "@"

    StringBuilder domain = new StringBuilder();

    while (remaining > 0) {
        if (domain.length() > 0) {
            domain.append(".");
            remaining--;
        }

        int labelLength = Math.min(63, remaining);

        // Label cuối không được rỗng
        if (labelLength <= 0) {
            break;
        }

        domain.append("x".repeat(labelLength));
        remaining -= labelLength;
    }

    return localPart + "@" + domain;
}
}
