package com.sayye.reservation.dto.request;

import com.sayye.course.entity.Course;
import com.sayye.reservation.entity.Reservation;
import com.sayye.room.entity.Room;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;

@Getter
public class ReservationReqDto {

    @NotNull(message = "클래스 Id는 필수입니다.")
    @Positive(message = "클래스 Id는 양수여야 합니다.")
    private Long courseId;

    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, message = "이름은 최소 2글자 이상이어야 합니다.")
    private String userName;

    @NotBlank(message = "휴대폰 뒷자리는 필수입니다.")
    @Pattern(regexp = "^[0-9]{4}$", message = "휴대폰 뒷번호는 4자리 숫자여야 합니다.")
    private String phoneLastNumber;

    @NotNull(message = "예약 시작 시간은 필수입니다.")
    private LocalTime startTime;

    @NotNull(message = "예약 종료 시간은 필수입니다.")
    private LocalTime endTime;

    @NotNull(message = "예약 날짜는 필수입니다.")
    @FutureOrPresent(message = "과거 날짜는 선택할 수 없습니다.")
    private LocalDate reservationDate;

    // 스프링이 메서드 실행해서 검증해줌
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

    public Reservation toEntity(Room room, Course course) {
        return Reservation.of(room, course, userName, phoneLastNumber, startTime, endTime,
            reservationDate);
    }

}
