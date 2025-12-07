package krs.erp.config.multitenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to set the TenantContext based on the authenticated user.
 * This ensures that subsequent database calls use the correct tenant database.
 */
@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof krs.erp.config.CustomUserDetails) {

                krs.erp.config.CustomUserDetails userDetails = (krs.erp.config.CustomUserDetails) authentication
                        .getPrincipal();
                Long tenantId = userDetails.getTenantId();

                if (tenantId != null) {
                    TenantContext.setCurrentTenant(tenantId.toString());
                } else {
                    // Fallback to header for testing or if user has no tenant
                    String tenantIdHeader = request.getHeader("X-Tenant-ID");
                    if (tenantIdHeader != null) {
                        TenantContext.setCurrentTenant(tenantIdHeader);
                    }
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
