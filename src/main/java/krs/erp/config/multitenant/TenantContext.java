package krs.erp.config.multitenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds the current tenant identifier in a ThreadLocal.
 * This allows the application to access the tenant ID anywhere in the code
 * without passing it as a parameter.
 */
public class TenantContext {
    private static final Logger logger = LoggerFactory.getLogger(TenantContext.class);
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setCurrentTenant(String tenantId) {
        logger.debug("Setting current tenant to: {}", tenantId);
        currentTenant.set(tenantId);
    }

    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    public static void clear() {
        logger.debug("Clearing current tenant");
        currentTenant.remove();
    }
}
