package org.distributed.consensus.controller;

import jakarta.persistence.OptimisticLockException;
import org.distributed.consensus.Dtos.BookingDto;
import org.distributed.consensus.model.Booking;
import org.distributed.consensus.model.BookingAttempt;
import org.distributed.consensus.repository.BookingAttemptRepository;
import org.distributed.consensus.repository.BookingRepository;
import org.distributed.consensus.services.BookingService;
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
    BookingService bookingService;

    @PostMapping("/booking")
    @Transactional
    public ResponseEntity<Booking> createBooking(@RequestBody BookingDto bookingDto) {

        Booking savedBooking =  this.bookingService.CreateBookingWithOptimisticLocking(bookingDto);

        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }


    @PutMapping("/booking/{id}")
    @Transactional(readOnly = false)
    public ResponseEntity<Booking> updateBooking(@PathVariable("id") long id, @RequestBody BookingDto bookingDetails) {

       return this.bookingService.UpdateBooking(bookingDetails, id);
    }

    @DeleteMapping("/booking/{id}")
    public ResponseEntity<HttpStatus> deleteBooking(@PathVariable("id") long id) {
        boolean isSuccess = this.bookingService.DeleteBooking(id);

        if(isSuccess){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }else{
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
