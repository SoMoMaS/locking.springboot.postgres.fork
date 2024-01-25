package org.distributed.consensus.controller;

import org.distributed.consensus.Dtos.BookingDto;
import org.distributed.consensus.model.Booking;
import org.distributed.consensus.model.RoomLockEntry;
import org.distributed.consensus.repository.BookingRepository;
import org.distributed.consensus.repository.RoomLockEntryRepository;
import org.distributed.consensus.services.BookingService;
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
    BookingService bookingService;

    @PostMapping("/booking")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingDto booking) {
       return this.bookingService.CreateBookingWithPessimisticLocking(booking);
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
