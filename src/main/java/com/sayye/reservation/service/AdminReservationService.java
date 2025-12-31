package com.sayye.reservation.service;

import com.sayye.exception.ApiException;
import com.sayye.exception.ErrorCode;
import com.sayye.reservation.dto.request.AdminReservationReqDto;
import com.sayye.reservation.dto.response.ReservationAdminResDto;
import com.sayye.reservation.dto.response.ReservationResDto;
import com.sayye.reservation.entity.Reservation;
import com.sayye.reservation.repository.ReservationRepository;
import com.sayye.room.entity.Room;
import com.sayye.room.repository.RoomRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AdminReservationService {

    private static final int DEFAULT_SIZE = 10;
    private static final String SORT_BY = "createdAt";

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;


    @Transactional
    public ReservationResDto createAdminReservation(Long roomId, AdminReservationReqDto reqDto,
        String adminId) {
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


    public Page<ReservationAdminResDto> getAllReservations(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, DEFAULT_SIZE,
            Sort.by(Direction.DESC, SORT_BY));

        return reservationRepository.findAll(pageable).map(ReservationAdminResDto::from);
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ApiException(ErrorCode.RESERVATION_NOT_FOUND));

        reservation.cancelByAdmin();
    }
}
