package krs.erp.scheduler;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import krs.erp.service.RecycleBinService;

/**
 * Unit tests for RecycleBinScheduler
 */
@ExtendWith(MockitoExtension.class)
class RecycleBinSchedulerTest {
    
    @Mock
    private RecycleBinService recycleBinService;
    
    @InjectMocks
    private RecycleBinScheduler recycleBinScheduler;
    
    @BeforeEach
    void setUp() {
        // Setup is handled by @InjectMocks
    }
    
    @Test
    void testCleanupOldRecycleBinRecords_WithRecords() {
        // Arrange
        int expectedDeletedCount = 5;
        when(recycleBinService.permanentlyDeleteOldRecords(any(LocalDateTime.class)))
                .thenReturn(expectedDeletedCount);
        
        // Act
        recycleBinScheduler.cleanupOldRecycleBinRecords();
        
        // Assert
        verify(recycleBinService, times(1)).permanentlyDeleteOldRecords(any(LocalDateTime.class));
    }
    
    @Test
    void testCleanupOldRecycleBinRecords_NoRecords() {
        // Arrange
        when(recycleBinService.permanentlyDeleteOldRecords(any(LocalDateTime.class)))
                .thenReturn(0);
        
        // Act
        recycleBinScheduler.cleanupOldRecycleBinRecords();
        
        // Assert
        verify(recycleBinService, times(1)).permanentlyDeleteOldRecords(any(LocalDateTime.class));
    }
    
    @Test
    void testCleanupOldRecycleBinRecords_WithException() {
        // Arrange
        when(recycleBinService.permanentlyDeleteOldRecords(any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("Database error"));
        
        // Act - should not throw exception due to error handling in the scheduler
        assertDoesNotThrow(() -> recycleBinScheduler.cleanupOldRecycleBinRecords());
        
        // Assert
        verify(recycleBinService, times(1)).permanentlyDeleteOldRecords(any(LocalDateTime.class));
    }
    
    @Test
    void testCleanupWithCustomRetention() {
        // Arrange
        int retentionDays = 15;
        int expectedDeletedCount = 3;
        when(recycleBinService.permanentlyDeleteOldRecords(any(LocalDateTime.class)))
                .thenReturn(expectedDeletedCount);
        
        // Act
        int result = recycleBinScheduler.cleanupWithCustomRetention(retentionDays);
        
        // Assert
        assertEquals(expectedDeletedCount, result);
        verify(recycleBinService, times(1)).permanentlyDeleteOldRecords(any(LocalDateTime.class));
    }
    
    @Test
    void testCleanupWithCustomRetention_ThrowsException() {
        // Arrange
        int retentionDays = 15;
        when(recycleBinService.permanentlyDeleteOldRecords(any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("Database error"));
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            recycleBinScheduler.cleanupWithCustomRetention(retentionDays);
        });
        
        verify(recycleBinService, times(1)).permanentlyDeleteOldRecords(any(LocalDateTime.class));
    }
    
    @Test
    void testGetRetentionDays() {
        // Act
        int retentionDays = recycleBinScheduler.getRetentionDays();
        
        // Assert
        assertEquals(30, retentionDays);
    }
}
