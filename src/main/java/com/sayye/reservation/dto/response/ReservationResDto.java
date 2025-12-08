package com.sayye.reservation.dto.response;

import com.sayye.reservation.entity.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationResDto {

    private Long id;
    private String roomName;
    private String userName;
    private String status;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate reservationDate;

    public static ReservationResDto from(Reservation reservation) {
        return ReservationResDto.builder()
            .id(reservation.getId())
            .roomName(reservation.getRoom().getRoomName())
            .userName(reservation.getUserName())
            .status(reservation.getStatus().getDescription())
            .startTime(reservation.getStartTime())
            .endTime(reservation.getEndTime())
            .reservationDate(reservation.getReservationDate())
            .build();
    }
}
