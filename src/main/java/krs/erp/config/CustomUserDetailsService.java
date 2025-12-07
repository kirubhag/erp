package krs.erp.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import krs.erp.model.User;
import krs.erp.repository.UserRepository;

/**
 * Custom UserDetailsService for multi-tenant authentication.
 * Queries IAM_MasterDB directly during login to get user details and tenant_id.
 * After authentication, TenantFilter sets the tenant context for subsequent requests.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    @Autowired
    private UserRepository userRepository;

    /**
     * Load user by username for authentication.
     * Queries IAM_MasterDB.iam_users directly to avoid tenant routing issues during login.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
        
        // Query IAM_MasterDB directly for user credentials and tenant_id
        String sql = "SELECT user_id, username, password_hash, email, first_name, last_name, " +
                     "phone, user_type, enabled, tenant_id, organization_id " +
                     "FROM IAM_MasterDB.iam_users " +
                     "WHERE username = ? OR email = ?";
        
        List<User> users = jdbcTemplate.query(sql, new Object[]{username, username}, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setUsername(rs.getString("username"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setEmail(rs.getString("email"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setPhone(rs.getString("phone"));
            user.setUserType(User.UserType.valueOf(rs.getString("user_type")));
            user.setEnabled(rs.getBoolean("enabled"));
            user.setTenantId(rs.getString("tenant_id"));
            user.setOrganizationId(rs.getLong("organization_id"));
            return user;
        });

        if (users.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        User user = users.get(0);

        // Extract authorities - for now, use user type as role
        // In future, could query IAM_MasterDB for user roles
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getUserType().name()));

        return new CustomUserDetails(user, authorities);
    }

    /**
     * Get user details for authenticated user (returns custom User object).
     * This method uses the tenant-aware repository for post-authentication queries.
     */
    public User getUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
