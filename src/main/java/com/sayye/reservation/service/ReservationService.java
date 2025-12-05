package com.sayye.reservation.service;

import com.sayye.exception.ApiException;
import com.sayye.exception.ErrorCode;
import com.sayye.reservation.dto.CancelReservationReqDto;
import com.sayye.reservation.entity.Reservation;
import com.sayye.reservation.dto.ReservationAdminResDto;
import com.sayye.reservation.repository.ReservationRepository;
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
public class ReservationService {

    private static final int DEFAULT_SIZE = 10;
    private static final String SORT_BY = "createdAt";

    private final ReservationRepository reservationRepository;

    @Transactional
    public void cancelReservation(Long reservationId, CancelReservationReqDto reqDto) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ApiException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.isOwner(reqDto.getUserName(), reqDto.getPhoneLastNumber())) {
            throw new ApiException(ErrorCode.RESERVATION_CANCEL_UNAUTHORIZED);
        }

        if (!reservation.isCancelable()) {
            throw new ApiException(ErrorCode.RESERVATION_CANCEL_TIME_EXCEEDED);
        }

        reservationRepository.delete(reservation);
    }

    public Page<ReservationAdminResDto> getAllReservations(int page) {
        Pageable pageable = PageRequest.of(page - 1, DEFAULT_SIZE,
            Sort.by(Direction.DESC, SORT_BY));

        return reservationRepository.findAll(pageable).map(ReservationAdminResDto::from);
    }
}
