package com.bicap.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import com.bicap.common.enums.RoleName;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    private final JwtAccessDeniedHandler accessDeniedHandler;

        private static final String API_CROPS = "/api/crops/**";
        private static final String API_CROPS_ROOT = "/api/crops";

        private static final String API_ACCOUNTS = "/api/accounts/**";
        private static final String API_ACCOUNTS_ROOT = "/api/accounts";
        private static final String API_ACCOUNT_PASSWORD = "/api/accounts/me/password";

        private static final String API_FARMS = "/api/farms/**";
        private static final String API_FARM_ME = "/api/farms/me";

        private static final String API_RETAILERS = "/api/retailers/**";
        private static final String API_RETAILERS_ROOT = "/api/retailers";

        private static final String API_PRODUCTS = "/api/products/**";
        private static final String API_PRODUCT_BATCHES = "/api/product-batches/**";
        private static final String API_PRODUCT_BATCHES_BY_PRODUCT = "/api/products/*/batches/**";

        private static final String API_FARMING_SEASONS = "/api/farming-seasons/**";
        private static final String API_SEASON_ACTIVITIES = "/api/season-activities/**";
        private static final String API_PRODUCT_IMAGES = "/api/product-images/**";

        private static final String API_CART_ME = "/api/carts/me";
        private static final String API_CART_ITEMS = "/api/carts/items";
        private static final String API_CART_ITEMS_ALL = "/api/carts/items/**";

        private static final String API_ORDER_CHECKOUT = "/api/orders/checkout";
        private static final String API_ORDER_RETAILER = "/api/orders/retailer/**";
        private static final String API_ORDER_FARM = "/api/orders/farm/**";
        private static final String API_ORDER_CONFIRM = "/api/orders/*/confirm";
        private static final String API_ORDER_CANCEL = "/api/orders/*/cancel";
        private static final String API_ORDER_COMPLETE = "/api/orders/*/complete";

        private static final String API_NOTIFICATIONS = "/api/notifications/**";
        private static final String API_PRODUCT_BATCH_BLOCKCHAIN =
        "/api/product-batches/*/blockchain";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(customUserDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(Customizer.withDefaults())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth

        // =========================
        // PUBLIC
        // =========================
                .requestMatchers(
                        "/api/auth/**",

                        "/swagger-ui/**",
                        "/swagger-ui.html",

                        "/v3/api-docs/**",
                        "/v3/api-docs",

                        "/swagger-resources/**",
                        "/webjars/**"
                ).permitAll()

                /*
                * PUBLIC API
                */
                .requestMatchers("/api/public/**")
                .permitAll()

                // =========================
                // ACCOUNT, FARM, RETAILER
                // =========================
                .requestMatchers(HttpMethod.PUT, API_ACCOUNT_PASSWORD)
                .hasAnyRole(
                        RoleName.ADMIN.name(),
                        RoleName.FARM.name(),
                        RoleName.RETAILER.name()
                )

                .requestMatchers(API_ACCOUNTS_ROOT, API_ACCOUNTS)
                .hasRole(RoleName.ADMIN.name())

                .requestMatchers(HttpMethod.GET, API_FARM_ME)
                .hasRole(RoleName.FARM.name())

                .requestMatchers(HttpMethod.PUT, API_FARM_ME)
                .hasRole(RoleName.FARM.name())

                .requestMatchers(HttpMethod.GET, API_FARMS)
                .hasAnyRole(RoleName.ADMIN.name(), RoleName.RETAILER.name())

                .requestMatchers(API_RETAILERS_ROOT, API_RETAILERS)
                .hasRole(RoleName.RETAILER.name())

                // =========================
                // CROP
                // =========================

                // Ai cũng xem được Crop
                .requestMatchers(HttpMethod.GET, API_CROPS)
                .permitAll()

                // Chỉ Admin quản lý Crop
                .requestMatchers(HttpMethod.POST, API_CROPS_ROOT)
                .hasRole(RoleName.ADMIN.name())

                .requestMatchers(HttpMethod.PUT, API_CROPS)
                .hasRole(RoleName.ADMIN.name())

                .requestMatchers(HttpMethod.PATCH, API_CROPS)
                .hasRole(RoleName.ADMIN.name())

                // =========================
                // PRODUCT (Farmer)
                // =========================

                .requestMatchers(API_PRODUCTS)
                .hasRole(RoleName.FARM.name())

                // =========================
                // PRODUCT BATCH
                // =========================

                .requestMatchers(API_PRODUCT_BATCHES)
                .hasRole(RoleName.FARM.name())

                .requestMatchers(API_PRODUCT_BATCHES_BY_PRODUCT)
                .hasRole(RoleName.FARM.name())

                // =========================
                // FARMING SEASON
                // =========================

                .requestMatchers(API_FARMING_SEASONS)
                .hasRole(RoleName.FARM.name())

                // =========================
                // SEASON ACTIVITY
                // =========================

                .requestMatchers(API_SEASON_ACTIVITIES)
                .hasRole(RoleName.FARM.name())

                // =========================
                // PRODUCT IMAGE
                // =========================

                .requestMatchers(API_PRODUCT_IMAGES)
                .hasRole(RoleName.FARM.name())

                // =========================
                // CART (RETAILER)
                // =========================
                .requestMatchers(HttpMethod.GET,
                        API_CART_ME)
                .hasRole(RoleName.RETAILER.name())

                .requestMatchers(HttpMethod.POST,
                        API_CART_ITEMS)
                .hasRole(RoleName.RETAILER.name())

                .requestMatchers(HttpMethod.PUT,
                        API_CART_ITEMS_ALL)
                .hasRole(RoleName.RETAILER.name())

                .requestMatchers(HttpMethod.DELETE,
                        API_CART_ITEMS_ALL)
                .hasRole(RoleName.RETAILER.name())

                .requestMatchers(HttpMethod.DELETE,
                        API_CART_ME)
                .hasRole(RoleName.RETAILER.name())

                // =========================
                // ORDER
                // =========================

                // Checkout
                .requestMatchers(HttpMethod.POST,
                        API_ORDER_CHECKOUT)
                .hasRole(RoleName.RETAILER.name())

                // Retailer xem đơn
                .requestMatchers(HttpMethod.GET,
                        API_ORDER_RETAILER)
                .hasRole(RoleName.RETAILER.name())

                // Farm xem đơn
                .requestMatchers(HttpMethod.GET,
                        API_ORDER_FARM)
                .hasRole(RoleName.FARM.name())

                // Farm confirm
                .requestMatchers(HttpMethod.PATCH,
                        API_ORDER_CONFIRM)
                .hasRole(RoleName.FARM.name())

                // Farm hoặc Retailer đều được cancel
                .requestMatchers(HttpMethod.PATCH,
                        API_ORDER_CANCEL)
                .hasAnyRole(RoleName.FARM.name(), RoleName.RETAILER.name())

                // Admin complete
                .requestMatchers(HttpMethod.PATCH,
                        API_ORDER_COMPLETE)
                .hasRole(RoleName.ADMIN.name())
                
                // =========================
                // NOTIFICATION
                // =========================

                .requestMatchers(HttpMethod.GET,
                        API_NOTIFICATIONS)
                .hasAnyRole(
                        RoleName.ADMIN.name(),
                        RoleName.FARM.name(),
                        RoleName.RETAILER.name()
                )

                .requestMatchers(HttpMethod.PATCH,
                        API_NOTIFICATIONS)
                .hasAnyRole(
                        RoleName.ADMIN.name(),
                        RoleName.FARM.name(),
                        RoleName.RETAILER.name()
                )
                // =========================
                // PRODUCT BATCH BLOCKCHAIN
                .requestMatchers(
                        HttpMethod.POST,
                        API_PRODUCT_BATCH_BLOCKCHAIN
                )
                .hasRole(RoleName.FARM.name())
                // =========================
                // OTHERS
                // =========================

                .anyRequest().authenticated()
        )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

}
