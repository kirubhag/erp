package krs.erp.config.multitenant;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter to set the TenantContext based on the authenticated user.
 * This ensures that subsequent database calls use the correct tenant database.
 */
@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        boolean tenantWasSet = false;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof krs.erp.config.CustomUserDetails) {

                krs.erp.config.CustomUserDetails userDetails = (krs.erp.config.CustomUserDetails) authentication
                        .getPrincipal();
                Long tenantId = userDetails.getTenantId();

                if (tenantId != null) {
                    TenantContext.setCurrentTenant(tenantId.toString());
                    tenantWasSet = true;
                } else {
                    // Fallback to header for testing or if user has no tenant
                    String tenantIdHeader = request.getHeader("X-Tenant-ID");
                    if (tenantIdHeader != null) {
                        TenantContext.setCurrentTenant(tenantIdHeader);
                        tenantWasSet = true;
                    }
                }
            } else {
                // For unauthenticated requests (like sample data loading), check header
                String tenantIdHeader = request.getHeader("X-Tenant-ID");
                if (tenantIdHeader != null) {
                    TenantContext.setCurrentTenant(tenantIdHeader);
                    tenantWasSet = true;
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            // If a transaction is active, register a callback to clear tenant context after commit
            // Otherwise clear immediately
            if (tenantWasSet && TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        TenantContext.clear();
                    }
                });
            } else if (tenantWasSet) {
                TenantContext.clear();
            }
        }
    }
}
