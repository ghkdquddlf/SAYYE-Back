package com.sayye.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다."),

    // 관리자


    // 코스
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 코스입니다."),
    COURSE_NAME_DUPLICATED(HttpStatus.CONFLICT, "이미 존재하는 코스 이름입니다."),


    // 회의실



    // 예약
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다."),
    RESERVATION_CANCEL_UNAUTHORIZED(HttpStatus.FORBIDDEN, "예약자 정보가 일치하지 않습니다."),
    RESERVATION_CANCEL_TIME_EXCEEDED(HttpStatus.FORBIDDEN, "예약 시작 1시간 전에는 취소할 수 없습니다."),
    RESERVATION_INVALID_START_TIME(HttpStatus.BAD_REQUEST, "10시 전에는 예약을 할 수 없습니다."),
    RESERVATION_EXCEED_MAX_DURATION(HttpStatus.BAD_REQUEST, "예약 이용 시간은 최대 2시간입니다."),
    RESERVATION_USER_DUPLICATED(HttpStatus.CONFLICT, "해당 예약자 정보로 동일한 날짜에 예약이 존재합니다."),
    RESERVATION_TIME_OVERLAPPED(HttpStatus.CONFLICT,"이미 예약되어있습니다.");

    private final HttpStatus status;
    private final String message;

}
