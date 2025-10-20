package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    List<User> findByUserType(User.UserType userType);
    
    List<User> findByEnabledTrue();
    
    List<User> findByEnabledFalse();
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
    
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = :userType AND u.enabled = true")
    Long countActiveUsersByType(@Param("userType") User.UserType userType);
    
    @Query("SELECT u FROM User u WHERE u.accountNonLocked = false")
    List<User> findLockedUsers();
    
    @Query("SELECT u FROM User u WHERE u.credentialsNonExpired = false")
    List<User> findUsersWithExpiredCredentials();
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}