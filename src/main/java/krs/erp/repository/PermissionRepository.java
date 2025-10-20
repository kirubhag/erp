package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    Optional<Permission> findByName(String name);
    
    List<Permission> findByResource(String resource);
    
    List<Permission> findByAction(String action);
    
    List<Permission> findByResourceAndAction(String resource, String action);
    
    List<Permission> findBySystemPermissionTrue();
    
    List<Permission> findBySystemPermissionFalse();
    
    @Query("SELECT p FROM Permission p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Permission> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT COUNT(r) FROM Role r JOIN r.permissions p WHERE p.id = :permissionId")
    Long countRolesByPermissionId(@Param("permissionId") Long permissionId);
    
    boolean existsByName(String name);
    
    boolean existsByResourceAndAction(String resource, String action);
}