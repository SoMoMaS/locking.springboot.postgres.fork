package org.distributed.consensus.controller;

import org.distributed.consensus.model.Booking;
import org.distributed.consensus.model.RoomLockEntry;
import org.distributed.consensus.repository.BookingRepository;
import org.distributed.consensus.repository.RoomLockEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pessimistic")
public class PessimisticBookingController {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    RoomLockEntryRepository roomLockEntryRepository;

    @PostMapping("/booking")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        boolean isLockAcquired = false;
        try {
            // Attempt to acquire a room lock
            isLockAcquired = acquireRoomLock(booking.getRoomId());

            // If lock is not acquired, return LOCKED status
            if (!isLockAcquired) {
                return new ResponseEntity<>(HttpStatus.LOCKED);
            }

            // Check for conflicting bookings
            List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(booking.getRoomId(), booking.getStart(), booking.getFinish());
            if (!conflictingBookings.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            // Save the booking
            Booking _booking = bookingRepository.save(new Booking(booking.getName(), booking.getRoomId(), booking.getStart(), booking.getFinish()));
            return new ResponseEntity<>(_booking, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            // Release the room lock if it was acquired
            if (isLockAcquired) {
                releaseRoomLock(booking.getRoomId());
            }
        }
    }

    @Transactional
    public boolean acquireRoomLock(Long roomId) {
        try {
            roomLockEntryRepository.save(new RoomLockEntry(roomId));
            return true;
        } catch (DataIntegrityViolationException e) {
            // Handle unique constraint violation
            return false;
        }
    }

    @Transactional
    public void releaseRoomLock(Long roomId) {
        roomLockEntryRepository.deleteByRoomId(roomId);
    }


    @DeleteMapping("/booking/{id}")
    public ResponseEntity<HttpStatus> deleteBooking(@PathVariable("id") long id) {
        try {
            bookingRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
