package com.sayye.reservation.controller;

import com.sayye.reservation.dto.CancelReservationReqDto;
import com.sayye.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/reservations")
@RequiredArgsConstructor
@RestController
public class ReservationController {

    private final ReservationService reservationService;

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId, @Valid @RequestBody
    CancelReservationReqDto reqDto) {
        reservationService.cancelReservation(reservationId, reqDto);
        return ResponseEntity.noContent().build();
    }

}

