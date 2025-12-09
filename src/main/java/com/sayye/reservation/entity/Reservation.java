package com.sayye.reservation.entity;

import com.sayye.baseEntity.BaseEntity;
import com.sayye.course.entity.Course;
import com.sayye.room.entity.Room;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classes_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String phoneLastNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Builder(access = AccessLevel.PRIVATE)
    private Reservation(Course course, Room room, String userName, String phoneLastNumber,
        LocalTime startTime, LocalTime endTime, LocalDate reservationDate,
        ReservationStatus status) {
        this.course = course;
        this.room = room;
        this.userName = userName;
        this.phoneLastNumber = phoneLastNumber;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservationDate = reservationDate;
    }

    public static Reservation of(Room room, Course course, String userName, String phoneLastNumber,
        LocalTime startTime, LocalTime endTime, LocalDate reservationDate) {
        return Reservation.builder()
            .course(course)
            .room(room)
            .userName(userName)
            .phoneLastNumber(phoneLastNumber)
            .startTime(startTime)
            .status(ReservationStatus.RESERVED)
            .endTime(endTime)
            .reservationDate(reservationDate)
            .build();
    }

    public void updateReservationTime(LocalTime startTime, LocalTime endTime, LocalDate reservationDate) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservationDate = reservationDate;
    }

    public boolean isOwner(String userName, String phoneLastNumber) {
        return this.userName.equals(userName) && this.phoneLastNumber.equals(phoneLastNumber);
    }

    public boolean isModifiable(LocalDateTime now) {
        LocalDateTime reservationDateTime = LocalDateTime.of(reservationDate, startTime);
        // 현재 시간이 예약 시작 시간보다 1시간 전인지
        return now.isBefore(reservationDateTime.minusHours(1));
    }

    public boolean isDateChanged(LocalDate newDate) {
        return !this.reservationDate.equals(newDate);
    }
}
