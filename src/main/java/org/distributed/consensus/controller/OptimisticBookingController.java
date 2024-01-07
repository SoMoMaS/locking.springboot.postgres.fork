package org.distributed.consensus.controller;

import jakarta.persistence.OptimisticLockException;
import org.distributed.consensus.model.Booking;
import org.distributed.consensus.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/optimistic")
public class OptimisticBookingController {

    @Autowired
    BookingRepository bookingRepository;

    @PostMapping("/booking")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {

        try {
            Booking _booking = bookingRepository.save(new Booking(booking.getName(), booking.getRoomId(), booking.getStart(), booking.getFinish()));
            return new ResponseEntity<>(_booking, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/booking/{id}")
    @Transactional(readOnly = false)
    public ResponseEntity<Booking> updateBooking(@PathVariable("id") long id, @RequestBody Booking bookingDetails) {

        Optional<Booking> bookingData = bookingRepository.findByIdOPTIMISTIC(id);

        System.out.println("Booking found with ID: " + id +" version: " + bookingData.get().getVersion());

        if (((Optional<?>) bookingData).isPresent()) {
            Booking _booking = bookingData.get();
            _booking.setName(bookingDetails.getName());
            _booking.setRoomId(bookingDetails.getRoomId());
            _booking.setStart(bookingDetails.getStart());
            _booking.setFinish(bookingDetails.getFinish());
            // Weitere Felder nach Bedarf aktualisieren

            try {

                Booking updatedBooking = bookingRepository.save(_booking);
                System.out.println("Updated Booking with ID: " + id +" version: " + bookingData.get().getVersion());
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
