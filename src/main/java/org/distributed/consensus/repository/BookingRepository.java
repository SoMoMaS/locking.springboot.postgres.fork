package org.distributed.consensus.repository;

import jakarta.persistence.LockModeType;
import org.distributed.consensus.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<Booking> findByIdOPTIMISTIC(Long id);

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    Optional<Booking> findByIdPESSIMISTIC(Long id);
}
