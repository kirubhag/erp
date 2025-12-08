package krs.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.model.Room;
import krs.erp.model.Room.RoomType;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // Find by room name
    Room findByRoomName(String roomName);

    // Find by room type
    List<Room> findByRoomType(RoomType roomType);

    // Find by building
    List<Room> findByBuilding(String building);

    // Find active rooms
    List<Room> findByIsActiveTrue();
}
