package org.distributed.consensus.services;

import jakarta.persistence.OptimisticLockException;
import org.distributed.consensus.Dtos.BookingDto;
import org.distributed.consensus.model.Booking;
import org.distributed.consensus.model.RoomLockEntry;
import org.distributed.consensus.repository.BookingRepository;
import org.distributed.consensus.repository.RoomLockEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    RoomLockEntryRepository roomLockEntryRepository;

    public ResponseEntity CreateBookingWithPessimisticLocking(BookingDto bookingDto){
        boolean isLockAcquired = false;
        try {
            // Attempt to acquire a room lock
            isLockAcquired = AcquireRoomLock(bookingDto.getRoomId());

            // If lock is not acquired, return LOCKED status
            if (!isLockAcquired) {
                return new ResponseEntity<>(HttpStatus.LOCKED);
            }

            // Check for conflicting bookings
            List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(bookingDto.getRoomId(), bookingDto.getStart(), bookingDto.getFinish());
            if (!conflictingBookings.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            // Save the booking
            Booking _booking = bookingRepository.save(new Booking(bookingDto.getName(), bookingDto.getRoomId(), bookingDto.getStart(), bookingDto.getFinish()));
            return new ResponseEntity<>(_booking, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            // Release the room lock if it was acquired
            if (isLockAcquired) {
                releaseRoomLock(bookingDto.getRoomId());
            }
        }
    }

    public Booking CreateBookingWithOptimisticLocking(BookingDto bookingDto){
        // Überprüfen Sie, ob eine Buchung für diesen Raum und Zeitraum existiert
        List<Booking> conflictList = bookingRepository.findConflictingBookings(bookingDto.getRoomId(), bookingDto.getStart(), bookingDto.getFinish());

        if (!conflictList.isEmpty()) {
            // Konflikt gefunden, geben Sie null zurück
            return null;
        }

        // Kein Konflikt, speichern Sie die neue Buchung
        Booking booking = new Booking(bookingDto.getName(), bookingDto.getRoomId(), bookingDto.getStart(), bookingDto.getFinish());
        return bookingRepository.save(booking);
    }

    public ResponseEntity UpdateBooking(BookingDto bookingDto, long id){
        Optional<Booking> bookingData = bookingRepository.findByIdOPTIMISTIC(id);

        System.out.println("Booking found with ID: " + id);

        if (((Optional<?>) bookingData).isPresent()) {
            Booking _booking = bookingData.get();
            _booking.setName(bookingDto.getName());
            _booking.setRoomId(bookingDto.getRoomId());
            _booking.setStart(bookingDto.getStart());
            _booking.setFinish(bookingDto.getFinish());
            // Weitere Felder nach Bedarf aktualisieren

            try {

                Booking updatedBooking = bookingRepository.save(_booking);
                System.out.println("Updated Booking with ID: " + id);
                return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
            } catch (OptimisticLockException e) {
                return new ResponseEntity<>(null, HttpStatus.CONFLICT); // Konflikt bei gleichzeitigen Updates
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    public List<Booking> FindConflictingBookings(Long roomID, Date start, Date finish){
        return bookingRepository.findConflictingBookings(roomID, start, finish);
    }

    public boolean AcquireRoomLock(Long roomId) {
        try {
            roomLockEntryRepository.save(new RoomLockEntry(roomId));
            return true;
        } catch (DataIntegrityViolationException e) {
            // Handle unique constraint violation
            return false;
        }
    }

    public void releaseRoomLock(Long roomId) {
        roomLockEntryRepository.deleteByRoomId(roomId);
    }

    public boolean DeleteBooking(long id){
        try {
            bookingRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
