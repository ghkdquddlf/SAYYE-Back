package com.sayye.reservation.service;

import com.sayye.reservation.dto.request.AdminReservationReqDto;
import com.sayye.reservation.dto.response.ReservationResDto;
import com.sayye.reservation.entity.Reservation;
import com.sayye.reservation.repository.ReservationRepository;
import com.sayye.room.entity.Room;
import com.sayye.room.repository.RoomRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AdminReservationService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;


    @Transactional
    public ReservationResDto createAdminReservation(Long roomId, AdminReservationReqDto reqDto, String adminId) {
        // Todo 회의실 존재 여부 검증
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException());

        List<Reservation> conflictingReservations = reservationRepository.findConflictingReservations(
            roomId,
            reqDto.getReservationDate(),
            reqDto.getStartTime(),
            reqDto.getEndTime()
        );

        // 이미 존재하는 예약을 관리자에 의한 취소 상태로 변경
        conflictingReservations.forEach(Reservation::cancelByAdmin);

        Reservation adminReservation = reservationRepository.save(reqDto.toEntity(room, adminId));

        return ReservationResDto.from(adminReservation);
    }


}
