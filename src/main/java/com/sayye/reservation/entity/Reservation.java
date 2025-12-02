package com.sayye.reservation.entity;

import com.sayye.baseEntity.BaseEntity;
import com.sayye.course.entity.Course;
import com.sayye.room.entity.Room;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private LocalDate reservationDate;

    public boolean isOwner(String userName, String phoneLastNumber) {
        return this.userName.equals(userName) && this.phoneLastNumber.equals(phoneLastNumber);
    }

    public boolean isCancelable() {
        LocalDateTime reservationDateTime = LocalDateTime.of(reservationDate, startTime);
        // 현재 시간이 예약 시작 시간보다 1시간 전인지
        return LocalDateTime.now().isBefore(reservationDateTime.minusHours(1));
    }
}
