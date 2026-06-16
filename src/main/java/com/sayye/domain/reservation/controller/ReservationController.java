package com.sayye.domain.reservation.controller;

import com.sayye.domain.reservation.dto.request.CancelReservationReqDto;
import com.sayye.domain.reservation.dto.response.ReservationAdminResDto;
import com.sayye.domain.reservation.dto.request.ReservationReqDto;
import com.sayye.domain.reservation.dto.response.ReservationResDto;
import com.sayye.domain.reservation.dto.request.ReadReservationReqDto;
import com.sayye.domain.reservation.dto.request.UpdateReservationReqDto;
import com.sayye.domain.reservation.service.ReservationService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/reservations")
@RequiredArgsConstructor
@RestController
public class ReservationController {

    private final ReservationService reservationService;

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId,
        @Valid @RequestBody CancelReservationReqDto reqDto) {
        reservationService.cancelReservation(reservationId, reqDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<List<ReservationResDto>> getAllReservations(
        @Valid @RequestBody ReadReservationReqDto reqDto) {
        return ResponseEntity.ok(reservationService.getAllReservations(reqDto));
    }

    @PostMapping("/{reservationId}")
    public ResponseEntity<ReservationResDto> getReservationDetail(
        @PathVariable Long reservationId, @Valid @RequestBody ReadReservationReqDto reqDto) {
        return ResponseEntity.ok(reservationService.getReservationDetail(reservationId, reqDto));
    }


    @PatchMapping("/{reservationId}")
    public ResponseEntity<ReservationResDto> updateReservation(@PathVariable Long reservationId,
        @Valid @RequestBody UpdateReservationReqDto reqDto) {
        return ResponseEntity.ok(reservationService.updateReservation(reservationId, reqDto));
    }


}