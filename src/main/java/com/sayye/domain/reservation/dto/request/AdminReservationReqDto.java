package com.sayye.domain.reservation.dto.request;

import com.sayye.domain.reservation.entity.Reservation;
import com.sayye.domain.room.entity.Room;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;

@Getter
public class AdminReservationReqDto {

    @NotNull(message = "예약 시작 시간은 필수입니다.")
    private LocalTime startTime;

    @NotNull(message = "예약 종료 시간은 필수입니다.")
    private LocalTime endTime;

    @NotNull(message = "예약 날짜는 필수입니다.")
    @FutureOrPresent(message = "과거 날짜는 선택할 수 없습니다.")
    private LocalDate reservationDate;

    @AssertTrue(message = "종료 시간은 시작 시간보다 늦어야 합니다.")
    public boolean isTimeValid() {
        if (startTime == null || endTime == null) {
            return false;
        }
        return endTime.isAfter(startTime);
    }

    @AssertTrue(message = "이미 지난 시간은 예약할 수 없습니다.")
    public boolean isFutureTime() {
        if (startTime == null || endTime == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // 날짜가 오늘이면
        if (reservationDate.isEqual(today)) {
            return startTime.isAfter(now); // 시작 시간이 지금 시간 이후여야 함 (과거 시간 불가)
        }

        return true;
    }

    public Reservation toEntity(Room room, String adminId) {
        return Reservation.createByAdmin(room, adminId, startTime, endTime, reservationDate);
    }

}
