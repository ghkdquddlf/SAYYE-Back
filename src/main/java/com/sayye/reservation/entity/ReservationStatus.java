package com.sayye.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    RESERVED("예약"),
    CANCELED("취소"), // 취소됨
    UNAVAILABLE("예약 불가"), // 예약 불가
    CANCELLED_BY_ADMIN("관리자에 의한 취소"),
    FINISHED("완료");  // 사용 완료

    private final String description;
}
