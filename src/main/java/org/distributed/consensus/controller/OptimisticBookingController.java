package org.distributed.consensus.controller;

import org.distributed.consensus.Dtos.BookingDto;
import org.distributed.consensus.model.Booking;
import org.distributed.consensus.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/optimistic")
public class OptimisticBookingController {

    @Autowired
    BookingService bookingService;

    @PostMapping("/booking")
    @Transactional
    public ResponseEntity<Booking> createBooking(@RequestBody BookingDto bookingDto) {
        return this.bookingService.CreateBookingWithOptimisticLocking(bookingDto);
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
