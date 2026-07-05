package com.sayye.domain.reservation.entity;

import com.sayye.domain.reservation.dto.request.ReservationReqDto;
import com.sayye.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "reservation_requests")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationReq extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long courseId;
    private Long roomId;
    private String userName;
    private String phoneLastNumber;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private Long reservationId;
    private String failureMessage;

    public static ReservationReq toEntity(ReservationReqDto reservation, Long roomId) {
        return ReservationReq.builder()
            .roomId(roomId)
            .courseId(reservation.getCourseId())
            .userName(reservation.getUserName())
            .phoneLastNumber(reservation.getPhoneLastNumber())
            .reservationDate(reservation.getReservationDate())
            .startTime(reservation.getStartTime())
            .endTime(reservation.getEndTime())
            .status(RequestStatus.PENDING)
            .build();
    }

    public ReservationReqDto toDto() {
        return ReservationReqDto.builder()
            .courseId(this.courseId)
            .userName(this.userName)
            .phoneLastNumber(this.phoneLastNumber)
            .reservationDate(this.reservationDate)
            .startTime(this.startTime)
            .endTime(this.endTime)
            .build();
    }

    public void markProcessing() {
        this.status = RequestStatus.PROCESSING;
    }

    public void markConfirmed(Long reservationId) {

        this.status = RequestStatus.CONFIRMED;
        this.reservationId = reservationId;
    }

    public void markFailed(String failureMessage) {
        this.status = RequestStatus.FAILED;
        this.failureMessage = failureMessage;
    }
}
