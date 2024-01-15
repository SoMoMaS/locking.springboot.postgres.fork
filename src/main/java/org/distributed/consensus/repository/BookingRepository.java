package org.distributed.consensus.repository;

import jakarta.persistence.LockModeType;
import org.distributed.consensus.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.id = :id")
    @Transactional(readOnly = true)
    Optional<Booking> findByIdOPTIMISTIC(Long id);

    @Query("SELECT b FROM Booking b WHERE b.id = :id")
    @Transactional(readOnly = true)
    Optional<Booking> findByIdPESSIMISTIC(Long id);

    @Query("SELECT b FROM Booking b WHERE b.roomId = :roomId AND ((b.start <= :finish AND b.finish >= :start) OR (b.start >= :start AND b.finish <= :finish))")
    @Transactional(readOnly = true)
    List<Booking> findConflictingBookings(Long roomId, Date start, Date finish);




}
