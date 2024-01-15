package org.distributed.consensus.repository;

import org.distributed.consensus.model.Booking;
import org.distributed.consensus.model.BookingAttempt;
import org.distributed.consensus.model.RoomLockEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface BookingAttemptRepository extends JpaRepository<BookingAttempt, Long> {

    @Query("SELECT b FROM BookingAttempt b WHERE b.roomId = :roomId AND b.status IN ('PENDING', 'CONFIRMED') AND (b.start < :finish AND b.finish > :start)")
    @Transactional(readOnly = true)
    List<BookingAttempt> findConflictingBookingAttempts(Long roomId, Date start, Date finish);


}
