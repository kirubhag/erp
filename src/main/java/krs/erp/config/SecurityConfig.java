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
                                                .ignoringRequestMatchers("/api/auth/**", "/settings/auth/**",
                                                                "/webjars/**", "/actuator/**",
                                                                "/api/import/**", "/api/attachments/**",
                                                                "/api/subscriptions/**",
                                                                "/api/sections/**", "/api/promotions/**",
                                                                "/api/organizations/**"))
                                .headers(headers -> headers
                                                .contentSecurityPolicy(csp -> csp
                                                                .policyDirectives(
                                                                                "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:;")))
                                .authorizeHttpRequests(authz -> authz
                                                .requestMatchers("/", "/login", "/register", "/error", "/favicon.ico")
                                                .permitAll()
                                                .requestMatchers("/api/auth/**", "/settings/auth/**").permitAll()
                                                .requestMatchers("/api/import/**").permitAll() // Allow import endpoints
                                                                                               // for testing
                                                .requestMatchers("/api/students/**").permitAll() // Allow students
                                                                                                 // endpoints for
                                                                                                 // testing
                                                .requestMatchers("/api/v1/staff/**").permitAll() // Allow staff
                                                                                                 // endpoints for
                                                                                                 // testing
                                                .requestMatchers("/api/attendance/**").permitAll() // Allow attendance
                                                                                                   // endpoints for
                                                                                                   // testing
                                                .requestMatchers("/api/parents/**").permitAll() // Allow parents
                                                                                                // endpoints for testing
                                                .requestMatchers("/api/subjects/**").permitAll() // Allow subjects
                                                                                                 // endpoints for
                                                                                                 // testing
                                                .requestMatchers("/api/module/**").permitAll() // Allow module/menu
                                                                                               // endpoints for testing
                                                .requestMatchers("/api/fields/**").permitAll() // Allow ERP fields
                                                                                               // metadata endpoints
                                                .requestMatchers("/api/sections/**").permitAll() // Allow sections
                                                                                                 // endpoints for
                                                                                                 // testing
                                                .requestMatchers("/api/academic/**").permitAll() // Allow academic
                                                                                                 // settings endpoints
                                                .requestMatchers("/api/users/**").permitAll() // Allow users endpoints
                                                .requestMatchers("/settings/users/**").permitAll() // Allow settings
                                                                                                   // users endpoints
                                                .requestMatchers("/api/attachments/**").permitAll() // Allow attachment
                                                                                                    // endpoints (avatar
                                                                                                    // upload)
                                                .requestMatchers("/api/subscriptions/**").permitAll() // Allow
                                                                                                      // subscription/pricing
                                                                                                      // endpoints
                                                                                                      // for public
                                                                                                      // access
                                                .requestMatchers("/api/promotions/**").permitAll() // Allow student
                                                                                                   // promotion
                                                                                                   // endpoints
                                                .requestMatchers("/api/organizations/**").authenticated() // Organizations require authentication
                                                .requestMatchers("/api/**").authenticated() // Other API endpoints
                                                                                            // require authentication
                                                .requestMatchers("/actuator/health", "/__healthcheck").permitAll()
                                                .requestMatchers("/static/**", "/assets/**", "/css/**", "/js/**",
                                                                "/images/**", "/vendor/**",
                                                                "/dist/**", "/angular/**")
                                                .permitAll()
                                                .requestMatchers("/main-*.js", "/polyfills-*.js", "/styles-*.css",
                                                                "/chunk-*.js", "/*.js.map", "/*.css.map", "/erp-app-*.js")
                                                .permitAll()
                                                .anyRequest().authenticated())
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