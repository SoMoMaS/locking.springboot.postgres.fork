package org.distributed.consensus.controller;

import jakarta.persistence.OptimisticLockException;
import org.distributed.consensus.model.Booking;
import org.distributed.consensus.model.BookingAttempt;
import org.distributed.consensus.repository.BookingAttemptRepository;
import org.distributed.consensus.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/optimistic")
public class OptimisticBookingController {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    BookingAttemptRepository bookingAttemptRepository;

    @PostMapping("/booking")
    @Transactional
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {

        Booking savedBooking =  tryCreateBooking(booking.getName(), booking.getRoomId(),booking.getStart(),booking.getFinish());

        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }

    @Transactional
    public Booking tryCreateBooking(String name, Long roomId, Date start, Date finish) {
        // Überprüfen Sie, ob eine Buchung für diesen Raum und Zeitraum existiert
        List<Booking> conflictList = bookingRepository.findConflictingBookings(roomId, start, finish);

        if (!conflictList.isEmpty()) {
            // Konflikt gefunden, geben Sie null zurück
            return null;
        }

        // Kein Konflikt, speichern Sie die neue Buchung
        Booking booking = new Booking(name, roomId, start, finish);
        return bookingRepository.save(booking);
    }

    @PutMapping("/booking/{id}")
    @Transactional(readOnly = false)
    public ResponseEntity<Booking> updateBooking(@PathVariable("id") long id, @RequestBody Booking bookingDetails) {

        Optional<Booking> bookingData = bookingRepository.findByIdOPTIMISTIC(id);

        System.out.println("Booking found with ID: " + id);

        if (((Optional<?>) bookingData).isPresent()) {
            Booking _booking = bookingData.get();
            _booking.setName(bookingDetails.getName());
            _booking.setRoomId(bookingDetails.getRoomId());
            _booking.setStart(bookingDetails.getStart());
            _booking.setFinish(bookingDetails.getFinish());
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
