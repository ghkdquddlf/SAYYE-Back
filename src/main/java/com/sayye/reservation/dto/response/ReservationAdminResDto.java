package com.sayye.reservation.dto.response;

import com.sayye.reservation.entity.Reservation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationAdminResDto {

    private Long id;
    private String roomName;
    private String courseName;
    private String userName;
    private String phoneLastNumber;
    private String status;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate reservationDate;
    private LocalDateTime createdAt;


    public static ReservationAdminResDto from(Reservation reservation) {
        return ReservationAdminResDto.builder()
            .id(reservation.getId())
            .roomName(reservation.getRoom().getRoomName())
            .courseName(reservation.getCourse() != null ? reservation.getCourse().getCourseName()
                : "관리자 예약")
            .userName(reservation.getUserName())
            .phoneLastNumber(reservation.getPhoneLastNumber())
            .status(reservation.getStatus().getDescription())
            .startTime(reservation.getStartTime())
            .endTime(reservation.getEndTime())
            .reservationDate(reservation.getReservationDate())
            .createdAt(reservation.getCreatedAt())
            .build();
    }

}