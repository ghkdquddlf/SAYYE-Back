package com.sayye.reservation.controller;

import com.sayye.reservation.dto.request.CancelReservationReqDto;
import com.sayye.reservation.dto.request.ReservationReqDto;
import com.sayye.reservation.dto.request.UpdateReservationReqDto;
import com.sayye.reservation.dto.response.ReservationAdminResDto;
import com.sayye.reservation.dto.response.ReservationResDto;
import com.sayye.reservation.service.ReservationService;
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

    // 관리자 조회용
    @GetMapping
    public ResponseEntity<Page<ReservationAdminResDto>> getAllReservations(
        @RequestParam(defaultValue = "1") int page) {
        int pageNumber = (page <= 0) ? 1 : page;

        return ResponseEntity.ok(reservationService.getAllReservations(pageNumber));
    }

    // Todo 예약자 예약 내역 조회 필요

    @PostMapping("/{roomId}")
    public ResponseEntity<ReservationResDto> createReservation(@PathVariable Long roomId,
        @Valid @RequestBody ReservationReqDto reqDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(reservationService.createReservation(roomId, reqDto));
    }

    // Todo RoomController로 이동 필요할듯 (/rooms/{roomId}/reservations)
    @GetMapping("/{roomId}")
    public ResponseEntity<List<ReservationResDto>> getReservationsByRoomId(
        @PathVariable Long roomId, @RequestParam LocalDate reservationDate) {
        return ResponseEntity.ok(
            reservationService.getReservationsByRoomId(roomId, reservationDate));
    }

    @PatchMapping("/{reservationId}")
    public ResponseEntity<ReservationResDto> updateReservation(@PathVariable Long reservationId,
        @Valid @RequestBody UpdateReservationReqDto reqDto) {
        return ResponseEntity.ok(reservationService.updateReservation(reservationId, reqDto));
    }


}

