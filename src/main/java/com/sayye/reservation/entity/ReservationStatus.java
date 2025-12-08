package com.sayye.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    RESERVED("예약"),
    CANCELED("취소"), // 취소됨
    FINISHED("완료");  // 사용 완료

    private final String description;
}
