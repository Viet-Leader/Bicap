package com.bicap;

import com.bicap.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Equivalence Partitioning (EP) tests for BICAP API request validation.
 *
 * EP divides input data into equivalent classes:
 * - Valid
 * - Invalid
 * - Blank
 * - Null
 * - Invalid format
 * - Out of allowed range
 */
@ExtendWith(MockitoExtension.class)
class ApiEpValidationTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @Mock
    private AuthService authService;

    @Mock
    private CartService cartService;

    @Mock
    private CropService cropService;

    @Mock
    private FarmService farmService;

    @Mock
    private FarmingSeasonService farmingSeasonService;

    @Mock
    private ProductService productService;

    @Mock
    private ProductBatchService productBatchService;

    @Mock
    private ProductImageService productImageService;

    @Mock
    private RetailerService retailerService;

    @Mock
    private SeasonActivityService seasonActivityService;

    @BeforeEach
    void setUp() {

        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(

                new com.bicap.controller.AccountController(accountService),

                new com.bicap.controller.AuthController(authService),

                new com.bicap.controller.CartController(cartService),

                new com.bicap.controller.CropController(cropService),

                new com.bicap.controller.FarmController(farmService),

                new com.bicap.controller.FarmingSeasonController(
                        farmingSeasonService),

                new com.bicap.controller.ProductController(
                        productService),

                new com.bicap.controller.ProductBatchController(
                        productBatchService),

                new com.bicap.controller.ProductImageController(
                        productImageService),

                new com.bicap.controller.RetailerController(
                        retailerService),

                new com.bicap.controller.SeasonActivityController(
                        seasonActivityService)

        ).setValidator(validator).build();
    }

    // ============================================================
    // ACCOUNT
    // ============================================================

    @ParameterizedTest(name = "Username partition: {0}")
    @MethodSource("usernamePartitions")
    void createAccount_username_ep(
            String partition,
            String username,
            int expectedStatus) throws Exception {

        String json = accountJson(
                username,
                "password",
                "Nguyen Van A",
                "user@example.com",
                "0123456789",
                "FARM"
        );

        request(post("/api/accounts"), json)
                .andExpect(status().is(expectedStatus));
    }

    static Stream<Arguments> usernamePartitions() {

        return Stream.of(

                Arguments.of(
                        "blank",
                        "",
                        400
                ),

                Arguments.of(
                        "valid",
                        "user01",
                        201
                ),

                Arguments.of(
                        "too long",
                        "u".repeat(51),
                        400
                )
        );
    }


    @ParameterizedTest(name = "Password partition: {0}")
    @MethodSource("passwordPartitions")
    void createAccount_password_ep(
            String partition,
            String password,
            int expectedStatus) throws Exception {

        String json = accountJson(
                "user01",
                password,
                "Nguyen Van A",
                "user@example.com",
                "0123456789",
                "FARM"
        );

        request(post("/api/accounts"), json)
                .andExpect(status().is(expectedStatus));
    }

    static Stream<Arguments> passwordPartitions() {

        return Stream.of(

                Arguments.of(
                        "blank",
                        "",
                        400
                ),

                Arguments.of(
                        "too short",
                        "12345",
                        400
                ),

                Arguments.of(
                        "valid",
                        "123456",
                        201
                ),

                Arguments.of(
                        "too long",
                        "p".repeat(256),
                        400
                )
        );
    }


    @ParameterizedTest(name = "Email partition: {0}")
    @MethodSource("emailPartitions")
    void createAccount_email_ep(
            String partition,
            String email,
            int expectedStatus) throws Exception {

        String json = accountJson(
                "user01",
                "password",
                "Nguyen Van A",
                email,
                "0123456789",
                "FARM"
        );

        request(post("/api/accounts"), json)
                .andExpect(status().is(expectedStatus));
    }


    static Stream<Arguments> emailPartitions() {

        return Stream.of(

                Arguments.of(
                        "blank",
                        "",
                        400
                ),

                Arguments.of(
                        "valid format",
                        "user@example.com",
                        201
                ),

                Arguments.of(
                        "invalid format",
                        "not-an-email",
                        400
                ),

                Arguments.of(
                        "too long",
                        "a".repeat(91) + "@x.com",
                        400
                )
        );
    }


    @ParameterizedTest(name = "Role partition: {0}")
    @MethodSource("rolePartitions")
    void createAccount_role_ep(
            String partition,
            String role,
            int expectedStatus) throws Exception {

        String roleJson =
                role == null
                        ? "null"
                        : "\"" + role + "\"";

        String json = """
                {
                  "username":"user01",
                  "password":"password",
                  "fullName":"Nguyen Van A",
                  "email":"user@example.com",
                  "phone":"0123456789",
                  "role":%s
                }
                """.formatted(roleJson);

        request(post("/api/accounts"), json)
                .andExpect(status().is(expectedStatus));
    }


    static Stream<Arguments> rolePartitions() {

        return Stream.of(

                Arguments.of(
                        "null",
                        null,
                        400
                ),

                Arguments.of(
                        "valid FARM",
                        "FARM",
                        201
                ),

                Arguments.of(
                        "valid RETAILER",
                        "RETAILER",
                        201
                ),

                Arguments.of(
                        "invalid enum",
                        "CUSTOMER",
                        400
                )
        );
    }


    // ============================================================
    // AUTH REGISTER
    // ============================================================

    @ParameterizedTest(name = "Register email partition: {0}")
    @MethodSource("emailPartitions")
    void register_email_ep(
            String partition,
            String email,
            int expectedStatus) throws Exception {

        String json = """
                {
                  "username":"retailer01",
                  "password":"password",
                  "fullName":"Nguyen Van A",
                  "email":"%s",
                  "phone":"0123456789",
                  "retailerName":"Store",
                  "address":"Address"
                }
                """.formatted(email);

        request(post("/api/auth/register"), json)
                .andExpect(status().is(expectedStatus));
    }


    // ============================================================
    // CART
    // ============================================================

    @ParameterizedTest(name = "Cart quantity partition: {0}")
    @MethodSource("cartQuantityPartitions")
    void addCartItem_quantity_ep(
            String partition,
            String quantity,
            int expectedStatus) throws Exception {

        String json = """
                {
                  "batchId":1,
                  "quantity":%s
                }
                """.formatted(quantity);

        request(post("/api/carts/items"), json)
                .andExpect(status().is(expectedStatus));
    }


    static Stream<Arguments> cartQuantityPartitions() {

        return Stream.of(

                Arguments.of(
                        "null",
                        "null",
                        400
                ),

                Arguments.of(
                        "non-positive",
                        "0",
                        400
                ),

                Arguments.of(
                        "valid positive",
                        "1",
                        201
                ),

                Arguments.of(
                        "fractional positive",
                        "0.01",
                        201
                )
        );
    }


    // ============================================================
    // CROP
    // ============================================================

    @ParameterizedTest(name = "Crop name partition: {0}")
    @MethodSource("namePartitions")
    void createCrop_name_ep(
            String partition,
            String name,
            int expectedStatus) throws Exception {

        String json = """
                {
                  "cropName":"%s"
                }
                """.formatted(name);

        request(post("/api/crops"), json)
                .andExpect(status().is(expectedStatus));
    }


    static Stream<Arguments> namePartitions() {

        return Stream.of(

                Arguments.of(
                        "blank",
                        "",
                        400
                ),

                Arguments.of(
                        "whitespace",
                        "   ",
                        400
                ),

                Arguments.of(
                        "valid",
                        "Rice",
                        201
                ),

                Arguments.of(
                        "too long",
                        "C".repeat(101),
                        400
                )
        );
    }


    // ============================================================
    // FARM
    // ============================================================

    @ParameterizedTest(name = "Farm field partition: {0}")
    @MethodSource("farmFieldPartitions")
    void updateFarm_field_ep(
            String field,
            String value,
            int expectedStatus) throws Exception {

        String json;

        if (field.equals("farmName")) {

            json = """
                    {
                      "farmName":"%s",
                      "address":"Address",
                      "description":"Desc"
                    }
                    """.formatted(value);

        } else {

            json = """
                    {
                      "farmName":"Farm",
                      "address":"%s",
                      "description":"Desc"
                    }
                    """.formatted(value);
        }

        request(put("/api/farms/me"), json)
                .andExpect(status().is(expectedStatus));
    }


    static Stream<Arguments> farmFieldPartitions() {

        return Stream.of(

                Arguments.of(
                        "farmName",
                        "",
                        400
                ),

                Arguments.of(
                        "farmName",
                        "Farm A",
                        200
                ),

                Arguments.of(
                        "farmName",
                        "F".repeat(151),
                        400
                ),

                Arguments.of(
                        "address",
                        "",
                        400
                ),

                Arguments.of(
                        "address",
                        "12 Nguyen Trai",
                        200
                ),

                Arguments.of(
                        "address",
                        "A".repeat(256),
                        400
                )
        );
    }


    // ============================================================
    // RETAILER
    // ============================================================

    @ParameterizedTest(name = "Retailer field partition: {0}")
    @MethodSource("retailerFieldPartitions")
    void updateRetailer_field_ep(
            String field,
            String value,
            int expectedStatus) throws Exception {

        String json;

        if (field.equals("retailerName")) {

            json = """
                    {
                      "retailerName":"%s",
                      "address":"Address"
                    }
                    """.formatted(value);

        } else {

            json = """
                    {
                      "retailerName":"Store",
                      "address":"%s"
                    }
                    """.formatted(value);
        }

        request(put("/api/retailers/me"), json)
                .andExpect(status().is(expectedStatus));
    }


    static Stream<Arguments> retailerFieldPartitions() {

        return Stream.of(

                Arguments.of(
                        "retailerName",
                        "",
                        400
                ),

                Arguments.of(
                        "retailerName",
                        "Store",
                        200
                ),

                Arguments.of(
                        "retailerName",
                        "R".repeat(151),
                        400
                ),

                Arguments.of(
                        "address",
                        "",
                        400
                ),

                Arguments.of(
                        "address",
                        "Address",
                        200
                ),

                Arguments.of(
                        "address",
                        "A".repeat(256),
                        400
                )
        );
    }


    // ============================================================
    // PRODUCT
    // ============================================================

    @ParameterizedTest(name = "Product field partition: {0}")
    @MethodSource("productFieldPartitions")
    void createProduct_field_ep(
            String field,
            String value,
            int expectedStatus) throws Exception {

        String json;

        switch (field) {

            case "productName":

                json = """
                        {
                          "cropId":1,
                          "productName":"%s",
                          "description":"Desc",
                          "unit":"kg"
                        }
                        """.formatted(value);

                break;

            case "description":

                json = """
                        {
                          "cropId":1,
                          "productName":"Product",
                          "description":"%s",
                          "unit":"kg"
                        }
                        """.formatted(value);

                break;

            default:

                json = """
                        {
                          "cropId":1,
                          "productName":"Product",
                          "description":"Desc",
                          "unit":"%s"
                        }
                        """.formatted(value);
        }

        request(post("/api/products"), json)
                .andExpect(status().is(expectedStatus));
    }


    static Stream<Arguments> productFieldPartitions() {

        return Stream.of(

                Arguments.of(
                        "productName",
                        "",
                        400
                ),

                Arguments.of(
                        "productName",
                        "Rice",
                        201
                ),

                Arguments.of(
                        "productName",
                        "P".repeat(151),
                        400
                ),

                Arguments.of(
                        "description",
                        "",
                        201
                ),

                Arguments.of(
                        "description",
                        "Organic rice",
                        201
                ),

                Arguments.of(
                        "description",
                        "D".repeat(1001),
                        400
                ),

                Arguments.of(
                        "unit",
                        "",
                        400
                ),

                Arguments.of(
                        "unit",
                        "kg",
                        201
                ),

                Arguments.of(
                        "unit",
                        "U".repeat(21),
                        400
                )
        );
    }


    // ============================================================
    // PRODUCT BATCH
    // ============================================================

    @ParameterizedTest(name = "Product batch numeric partition: {0}")
    @MethodSource("positiveDecimalPartitions")
    void createProductBatch_positive_fields_ep(
            String field,
            String value,
            int expectedStatus) throws Exception {

        String quantity =
                field.equals("quantity")
                        ? value
                        : "1";

        String price =
                field.equals("unitPrice")
                        ? value
                        : "1000";

        String json = """
                {
                  "seasonId":1,
                  "grade":"A",
                  "quantity":%s,
                  "unitPrice":%s
                }
                """.formatted(quantity, price);

        request(post("/api/products/1/batches"), json)
                .andExpect(status().is(expectedStatus));
    }


    static Stream<Arguments> positiveDecimalPartitions() {

        return Stream.of(

                Arguments.of(
                        "quantity",
                        "-1",
                        400
                ),

                Arguments.of(
                        "quantity",
                        "0",
                        400
                ),

                Arguments.of(
                        "quantity",
                        "1",
                        201
                ),

                Arguments.of(
                        "unitPrice",
                        "-1",
                        400
                ),

                Arguments.of(
                        "unitPrice",
                        "0",
                        400
                ),

                Arguments.of(
                        "unitPrice",
                        "1000",
                        201
                )
        );
    }


    @ParameterizedTest(name = "Product batch grade partition: {0}")
    @MethodSource("gradePartitions")
    void createProductBatch_grade_ep(
            String partition,
            String grade,
            int expectedStatus) throws Exception {

        String gradeJson =
                grade == null
                        ? "null"
                        : "\"" + grade + "\"";

        String json = """
                {
                  "seasonId":1,
                  "grade":%s,
                  "quantity":1,
                  "unitPrice":1000
                }
                """.formatted(gradeJson);

        request(post("/api/products/1/batches"), json)
                .andExpect(status().is(expectedStatus));
    }


    static Stream<Arguments> gradePartitions() {

        return Stream.of(

                Arguments.of(
                        "null",
                        null,
                        400
                ),

                Arguments.of(
                        "valid A",
                        "A",
                        201
                ),

                Arguments.of(
                        "valid B",
                        "B",
                        201
                ),

                Arguments.of(
                        "valid C",
                        "C",
                        201
                ),

                Arguments.of(
                        "invalid enum",
                        "D",
                        400
                )
        );
    }


    // ============================================================
    // FARMING SEASON
    // ============================================================

    @Test
    void createFarmingSeason_expectedHarvestDate_past_partition()
            throws Exception {

        LocalDate today = LocalDate.now();

        String json = """
                {
                  "seasonName":"Season",
                  "plantingDate":"%s",
                  "expectedHarvestDate":"%s"
                }
                """.formatted(
                today.minusDays(10),
                today.minusDays(1)
        );

        request(post("/api/farming-seasons"), json)
                .andExpect(status().isBadRequest());
    }


    @Test
    void createFarmingSeason_expectedHarvestDate_future_partition()
            throws Exception {

        LocalDate today = LocalDate.now();

        String json = """
                {
                  "seasonName":"Season",
                  "plantingDate":"%s",
                  "expectedHarvestDate":"%s"
                }
                """.formatted(
                today.minusDays(10),
                today.plusDays(1)
        );

        request(post("/api/farming-seasons"), json)
                .andExpect(status().isCreated());
    }


    // ============================================================
    // PRODUCT IMAGE
    // ============================================================

    @Test
    void reorderProductImage_empty_partition()
            throws Exception {

        request(
                patch("/api/product-images/batch/1/reorder"),
                """
                {
                  "imageIds":[]
                }
                """
        ).andExpect(status().isBadRequest());
    }


    @Test
    void reorderProductImage_nonEmpty_partition()
            throws Exception {

        request(
                patch("/api/product-images/batch/1/reorder"),
                """
                {
                  "imageIds":[1,2]
                }
                """
        ).andExpect(status().isOk());
    }


    // ============================================================
    // SEASON ACTIVITY
    // ============================================================

    @ParameterizedTest(name = "Season activity description partition: {0}")
    @MethodSource("descriptionPartitions")
    void createSeasonActivity_description_ep(
            String partition,
            String description,
            int expectedStatus) throws Exception {

        String json = """
                {
                  "activityTypeId":1,
                  "activityTime":"2026-08-27T10:00:00",
                  "description":"%s"
                }
                """.formatted(description);

        request(post("/api/season-activities/season/1"), json)
                .andExpect(status().is(expectedStatus));
    }


    static Stream<Arguments> descriptionPartitions() {

        return Stream.of(

                Arguments.of(
                        "blank",
                        "",
                        400
                ),

                Arguments.of(
                        "whitespace",
                        "   ",
                        400
                ),

                Arguments.of(
                        "valid",
                        "Fertilize rice",
                        201
                )
        );
    }


    // ============================================================
    // HELPER
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
