package com.sayye.domain.reservation.dto.response;

import com.sayye.domain.reservation.entity.ReservationReq;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationRequestStatusResDto {

    private Long requestId;
    private String status;
    private Long reservationId;
    private String failureMessage;

    public static ReservationRequestStatusResDto from(ReservationReq req) {
        return ReservationRequestStatusResDto.builder()
            .requestId(req.getId())
            .status(req.getStatus().name())
            .reservationId(req.getReservationId())
            .failureMessage(req.getFailureMessage())
            .build();
    }
}
