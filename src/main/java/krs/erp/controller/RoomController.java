package krs.erp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import krs.erp.model.Room;
import krs.erp.repository.RoomRepository;

/**
 * REST Controller for managing rooms
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    /**
     * Get all rooms with pagination and sorting
     * GET /api/rooms
     */
    @GetMapping
    public ResponseEntity<Page<Room>> getAllRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Room> rooms = roomRepository.findAll(pageable);
        
        return ResponseEntity.ok(rooms);
    }

    /**
     * Get active rooms only
     * GET /api/rooms/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<Room>> getActiveRooms() {
        List<Room> rooms = roomRepository.findByIsActiveTrue();
        return ResponseEntity.ok(rooms);
    }

    /**
     * Get room by ID
     * GET /api/rooms/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        Optional<Room> room = roomRepository.findById(id);
        return room.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get rooms by type
     * GET /api/rooms/type/{roomType}
     */
    @GetMapping("/type/{roomType}")
    public ResponseEntity<List<Room>> getRoomsByType(@PathVariable Room.RoomType roomType) {
        List<Room> rooms = roomRepository.findByRoomType(roomType);
        return ResponseEntity.ok(rooms);
    }

    /**
     * Get rooms by building
     * GET /api/rooms/building/{building}
     */
    @GetMapping("/building/{building}")
    public ResponseEntity<List<Room>> getRoomsByBuilding(@PathVariable String building) {
        List<Room> rooms = roomRepository.findByBuilding(building);
        return ResponseEntity.ok(rooms);
    }

    /**
     * Create a new room
     * POST /api/rooms
     */
    @PostMapping
    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) {
        // Check if room name already exists
        Room existing = roomRepository.findByRoomName(room.getRoomName());
        if (existing != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        Room savedRoom = roomRepository.save(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
    }

    /**
     * Update an existing room
     * PUT /api/rooms/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @Valid @RequestBody Room room) {
        if (!roomRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        room.setId(id);
        Room updatedRoom = roomRepository.save(room);
        return ResponseEntity.ok(updatedRoom);
    }

    /**
     * Delete a room
     * DELETE /api/rooms/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        if (!roomRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        roomRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
