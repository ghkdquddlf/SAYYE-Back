package com.sayye.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다."),

    // 관리자



    // 클래스



    // 회의실



    // 예약
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다."),
    RESERVATION_CANCEL_UNAUTHORIZED(HttpStatus.FORBIDDEN, "예약자 정보가 일치하지 않습니다."),
    RESERVATION_CANCEL_TIME_EXCEEDED(HttpStatus.FORBIDDEN, "예약 시작 1시간 전에는 취소할 수 없습니다.");

    private final HttpStatus status;
    private final String message;

}
