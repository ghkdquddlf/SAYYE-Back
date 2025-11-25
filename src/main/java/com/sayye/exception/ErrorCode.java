package com.sayye.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다."),

    // 관리자
    ADMIN_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "존재하지 않는 관리자입니다.");


    // 클래스



    // 회의실



    // 예약

    private final HttpStatus status;
    private final String message;

}
