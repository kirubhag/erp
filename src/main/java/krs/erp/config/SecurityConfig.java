package krs.erp.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

        @Autowired
        private CustomUserDetailsService customUserDetailsService;

        @Autowired
        private krs.erp.config.multitenant.TenantFilter tenantFilter;

        @Value("${app.cors.allowed-origins:http://localhost:4200,http://localhost:3000,http://localhost:8080,http://localhost:8081}")
        private String allowedOrigins;

        /**
         * Create a security filter chain with proper security controls.
         * - Public endpoints: /, /login, /register, /api/auth/**, /actuator/health
         * - Static resources: /static/**, /assets/**, /css/**, /js/**, /images/**,
         * /vendor/**
         * - Authenticated endpoints: Everything else
         * - CSRF enabled for all requests including APIs
         */
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher("/**")
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/api/auth/**",
                                                                "/webjars/**", "/actuator/**",
                                                                "/api/import/**", "/api/attachments/**",
                                                                "/api/subscriptions/**",
                                                                "/api/sections/**", "/api/promotions/**",
                                                                "/api/organizations/**", "/api/account/**",
                                                                "/api/migration/**",
                                                                "/api/v1/**",
                                                                "/api/sample-data/**",
                                                                "/api/user-settings/**",
                                                                "/api/custom-views/**",
                                                                "/api/iam/**",
                                                                "/api/staff/**",
                                                                "/api/parents/**",
                                                                "/api/students/**",
                                                                "/api/fields/**"))
                                .headers(headers -> headers
                                                .contentSecurityPolicy(csp -> csp
                                                                .policyDirectives(
                                                                                "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:;")))
                                .authorizeHttpRequests(authz -> authz
                                                .requestMatchers("/", "/error", "/favicon.ico")
                                                .permitAll()
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/api/import/**").permitAll() // Allow import endpoints
                                                                                               // for testing
                                                .requestMatchers("/api/attachments/**").permitAll() // Allow attachment
                                                                                                    // endpoints (avatar
                                                                                                    // upload)
                                                .requestMatchers("/api/subscriptions/**").permitAll() // Allow
                                                                                                      // subscription/pricing
                                                                                                      // endpoints
                                                                                                      // for public
                                                                                                      // access
                                                                                                      // endpoints
                                                                                                      // (avatar
                                                                                                      // upload)
                                                .requestMatchers("/api/subscriptions/**").permitAll() // Allow
                                                                                                      // subscription/pricing
                                                                                                      // endpoints
                                                                                                      // for public
                                                                                                      // access
                                                .requestMatchers("/api/promotions/**").permitAll() // Allow student
                                                                                                   // promotion
                                                                                                   // endpoints
                                                .requestMatchers("/api/account/**").permitAll() // Allow account closure
                                                                                                // endpoint for
                                                                                                // debugging
                                                .requestMatchers("/api/migration/**").permitAll() // Allow migration
                                                                                                  // endpoint
                                                .requestMatchers("/api/students/**").permitAll() // Allow students endpoints for dev/testing
                                                .requestMatchers("/api/staff/**").permitAll() // Allow staff endpoints for dev/testing
                                                .requestMatchers("/api/parents/**").permitAll() // Allow parent endpoints for dev/testing
                                                .requestMatchers("/api/subjects/**").permitAll() // Allow subject endpoints for dev/testing
                                                .requestMatchers("/api/fields/**").permitAll() // Allow entity fields metadata endpoints for dev/testing
                                                .requestMatchers("/api/module/**").permitAll() // Allow module/tab groups endpoints for dev/testing
                                                .requestMatchers("/api/library/**").permitAll() // Allow library management endpoints for dev/testing
                                                .requestMatchers("/api/leave/**").permitAll() // Allow leave management endpoints for dev/testing
                                                .requestMatchers("/api/hr/**").permitAll() // Allow HR management endpoints for dev/testing
                                                .requestMatchers("/api/inventory/**").permitAll() // Allow inventory management endpoints for dev/testing
                                                .requestMatchers("/api/finance/**").permitAll() // Allow finance management endpoints for dev/testing
                                                .requestMatchers("/api/iam/**").permitAll() // Allow IAM user management endpoints for dev/testing
                                                .requestMatchers("/api/lms/**").permitAll() // Allow LMS (Learning Management System) endpoints for dev/testing
                                                .requestMatchers("/api/tpd/**").permitAll() // Allow TPD (Training & Professional Development) endpoints for dev/testing
                                                .requestMatchers("/api/custom-views/**").permitAll() // Allow custom views endpoints for dev/testing
                                                .requestMatchers("/api/user-settings/**").permitAll() // Allow user settings endpoints for dev/testing
                                                .requestMatchers("/api/v1/sample-data/**").authenticated() // Sample data requires auth to populate owner_id
                                                .requestMatchers("/api/sample-data/**").authenticated() // Sample data requires auth to populate owner_id
                                                .requestMatchers("/api/v1/**").permitAll() // Allow other v1 API endpoints
                                                .requestMatchers("/api/organizations/**").authenticated() // Organizations
                                                                                                          // require
                                                                                                          // authentication
                                                .requestMatchers("/api/**").authenticated() // Other API endpoints
                                                                                            // require authentication
                                                .requestMatchers("/actuator/health", "/__healthcheck").permitAll()
                                                .requestMatchers("/static/**", "/assets/**", "/css/**", "/js/**",
                                                                "/images/**", "/vendor/**",
                                                                "/dist/**", "/angular/**")
                                                .permitAll()
                                                .requestMatchers("/main-*.js", "/polyfills-*.js", "/styles-*.css",
                                                                "/chunk-*.js", "/*.js.map", "/*.css.map",
                                                                "/erp-app-*.js")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        // For API requests, return 401 JSON instead of redirecting to login
                                                        if (request.getRequestURI().startsWith("/api/")) {
                                                                response.setStatus(401);
                                                                response.setContentType("application/json");
                                                                response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
                                                        } else {
                                                                response.sendRedirect("/login");
                                                        }
                                                }))
                                .httpBasic(basic -> basic.disable())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .permitAll()
                                                .defaultSuccessUrl("/dashboard", true))
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login")
                                                .permitAll())
                                .addFilterAfter(tenantFilter,
                                                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                // Use allowedOriginPatterns instead of allowedOrigins when using
                // allowCredentials
                configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "http://127.0.0.1:*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-CSRF-TOKEN"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public UserDetailsService userDetailsService() {
                return customUserDetailsService;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public HttpFirewall httpFirewall() {
                StrictHttpFirewall firewall = new StrictHttpFirewall();
                firewall.setAllowSemicolon(true);
                firewall.setAllowUrlEncodedSlash(true);
                firewall.setAllowUrlEncodedPercent(true);
                return firewall;
        }
}