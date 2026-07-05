package com.sayye.domain.reservation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationAcceptResDto {

    private Long requestId;
    private String message;

    public static ReservationAcceptResDto of(Long requestId) {
        return ReservationAcceptResDto.builder()
            .requestId(requestId)
            .message("예약 요청 처리 중입니다.")
            .build();
    }
}
