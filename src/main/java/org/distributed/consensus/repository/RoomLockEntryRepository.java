package org.distributed.consensus.repository;

import org.springframework.transaction.annotation.Transactional;
import org.distributed.consensus.model.Booking;
import org.distributed.consensus.model.RoomLockEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomLockEntryRepository extends JpaRepository<RoomLockEntry, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM RoomLockEntry r WHERE r.roomId = :roomId")
    void deleteByRoomId(@Param("roomId") Long roomId);
}
