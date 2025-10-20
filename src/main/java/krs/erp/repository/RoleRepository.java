package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByName(String name);
    
    List<Role> findBySystemRoleTrue();
    
    List<Role> findBySystemRoleFalse();
    
    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.name = :permissionName")
    List<Role> findRolesByPermissionName(@Param("permissionName") String permissionName);
    
    @Query("SELECT r FROM Role r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Role> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.id = :roleId")
    Long countUsersByRoleId(@Param("roleId") Long roleId);
    
    boolean existsByName(String name);
}