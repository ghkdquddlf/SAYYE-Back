package com.sayye.domain.room.controller;


import com.sayye.domain.reservation.dto.request.ReservationReqDto;
import com.sayye.domain.reservation.dto.response.ReservationAcceptResDto;
import com.sayye.domain.reservation.dto.response.ReservationResDto;
import com.sayye.domain.reservation.entity.ReservationReq;
import com.sayye.domain.reservation.service.ReservationService;
import com.sayye.domain.room.dto.request.RoomReqDto;
import com.sayye.domain.room.dto.response.RoomResDto;
import com.sayye.domain.room.service.RoomService;
import com.sayye.global.config.RoomQueueManager;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final ReservationService reservationService;
    private final RoomQueueManager roomQueueManager;

    @PostMapping
    public ResponseEntity<RoomResDto> createRoom(
        @Valid @RequestBody RoomReqDto roomReqDto
    ) {

        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(roomReqDto));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResDto> getRoomById(
        @PathVariable Long roomId
    ) {

        return ResponseEntity.status(HttpStatus.OK).body(roomService.getRoomById(roomId));
    }

    @GetMapping
    public ResponseEntity<List<RoomResDto>> getAllRooms() {

        return ResponseEntity.status(HttpStatus.OK).body(roomService.getAllRooms());
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long roomId) {

        roomService.deleteRoom(roomId);
        return ResponseEntity.status(HttpStatus.OK).body("회의실 정보가 삭제되었습니다.");

    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomResDto> updateRoom(
        @PathVariable Long roomId,
        @Valid @RequestBody RoomReqDto roomReqDto
    ) {

        return ResponseEntity.status(HttpStatus.OK)
            .body(roomService.updateRoom(roomId, roomReqDto));
    }

    // Reservation에서 이동
    @PostMapping("/{roomId}/reservations")
    public ResponseEntity<ReservationAcceptResDto> createReservation(@PathVariable Long roomId,
        @Valid @RequestBody ReservationReqDto reqDto) {
        ReservationReq req = reservationService.submitReservation(roomId, reqDto);
        roomQueueManager.enqueue(roomId, req.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(ReservationAcceptResDto.of(req.getId()));
    }

    // Reservation에서 이동
    @GetMapping("/{roomId}/reservations")
    public ResponseEntity<List<ReservationResDto>> getReservationsByRoomId(
        @PathVariable Long roomId, @RequestParam LocalDate reservationDate) {
        return ResponseEntity.ok(
            reservationService.getReservationsByRoomId(roomId, reservationDate));
    }

}
