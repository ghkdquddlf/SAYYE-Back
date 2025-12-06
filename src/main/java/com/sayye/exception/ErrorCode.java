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
    ROOM_NAME_DUPLICATED(HttpStatus.CONFLICT,"이미 존재하는 회의실 이름입니다."),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 회의실 입니다." );


    // 예약

    private final HttpStatus status;
    private final String message;

}
