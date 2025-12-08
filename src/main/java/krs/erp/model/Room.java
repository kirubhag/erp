package krs.erp.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "erp_rooms")
@AttributeOverride(name = "id", column = @Column(name = "room_id"))
public class Room extends BaseEntity {

    @NotBlank(message = "Room name is required")
    @Size(max = 100, message = "Room name must be less than 100 characters")
    @Column(name = "room_name", nullable = false, length = 100)
    private String roomName;

    @NotNull(message = "Capacity is required")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @NotNull(message = "Room type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false, length = 50)
    private RoomType roomType;

    @Size(max = 100, message = "Building name must be less than 100 characters")
    @Column(name = "building", length = 100)
    private String building;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    public enum RoomType {
        CLASSROOM,
        LAB,
        COMPUTER_LAB,
        HALL,
        LIBRARY,
        AUDITORIUM,
        OTHER
    }

    // Getters and Setters

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
