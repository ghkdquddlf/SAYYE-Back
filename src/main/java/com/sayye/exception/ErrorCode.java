package com.sayye.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다."),

    // 공지
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 공지사항입니다."),

    // 관리자
    ADMIN_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "존재하지 않는 관리자입니다."),
    ADMIN_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "기존 비밀번호가 일치하지 않습니다."),
    ADMIN_LOGIN_PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "비밀번호를 다시 입력해주세요."),
    ADMIN_PASSWORD_SAME(HttpStatus.BAD_REQUEST, "기존 비밀번호와 새 비밀번호가 동일합니다."),
    ADMIN_USER_ID_DUPLICATED(HttpStatus.CONFLICT, "존재하는 유저입니다."),
    ADMIN_EMAIL_DUPLICATED(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    ADMIN_NAME_DUPLICATED(HttpStatus.CONFLICT, "이미 존재하는 관리자 이름입니다."),
    ADMIN_ALREADY_LOGGED_OUT(HttpStatus.BAD_REQUEST, "이미 로그아웃된 유저입니다."),
    ADMIN_ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 토큰
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "지원하지 않는 JWT 토큰입니다."),
    TOKEN_MALFORMED(HttpStatus.UNAUTHORIZED, "잘못된 형식의 JWT 토큰입니다."),
    TOKEN_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "JWT 서명 검증에 실패했습니다."),
    TOKEN_ILLEGAL_ARGUMENT(HttpStatus.UNAUTHORIZED, "JWT 토큰이 비어있거나 잘못된 인자입니다."),
    TOKEN_BLACKLISTED(HttpStatus.UNAUTHORIZED, "블랙리스트에 등록된 토큰입니다."),
    TOKEN_ALREADY_LOGGED_OUT(HttpStatus.BAD_REQUEST, "이미 사용 완료된 토큰입니다."),
    TOKEN_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "토큰 타입이 일치하지 않습니다."),

    // 코스
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 코스입니다."),
    COURSE_NAME_DUPLICATED(HttpStatus.CONFLICT, "이미 존재하는 코스 이름입니다."),


    // 회의실
    ROOM_NAME_DUPLICATED(HttpStatus.CONFLICT, "이미 존재하는 회의실 이름입니다."),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회의실 입니다."),


    // 예약
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다."),
    RESERVATION_UNAUTHORIZED(HttpStatus.FORBIDDEN, "예약자 정보가 일치하지 않습니다."),
    RESERVATION_TIME_LIMIT_EXCEEDED(HttpStatus.FORBIDDEN, "예약 시작 1시간 전에는 취소/변경할 수 없습니다."),
    RESERVATION_INVALID_START_TIME(HttpStatus.BAD_REQUEST, "10시 전에는 예약을 할 수 없습니다."),
    RESERVATION_SYSTEM_NOT_OPEN_YET(HttpStatus.BAD_REQUEST, "예약은 오전 10시부터 가능합니다."),
    RESERVATION_EXCEED_MAX_DURATION(HttpStatus.BAD_REQUEST, "예약 이용 시간은 최대 2시간입니다."),
    RESERVATION_USER_DUPLICATED(HttpStatus.CONFLICT, "해당 예약자 정보로 동일한 날짜에 예약이 존재합니다."),
    RESERVATION_TIME_OVERLAPPED(HttpStatus.CONFLICT, "이미 예약되어있습니다."),
    INVALID_RESERVATION_DATE(HttpStatus.BAD_REQUEST, "예약은 오늘과 내일 날짜만 가능합니다.");

    private final HttpStatus status;
    private final String message;

}
