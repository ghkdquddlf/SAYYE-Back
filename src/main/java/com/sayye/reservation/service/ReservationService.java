package com.sayye.reservation.service;

import com.sayye.exception.ApiException;
import com.sayye.exception.ErrorCode;
import com.sayye.reservation.dto.CancelReservationReqDto;
import com.sayye.reservation.entity.Reservation;
import com.sayye.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReservationService {

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
}
