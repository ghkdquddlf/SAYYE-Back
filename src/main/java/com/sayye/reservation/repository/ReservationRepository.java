package com.sayye.reservation.repository;

import com.sayye.reservation.entity.Reservation;
import com.sayye.reservation.entity.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Todo 데이터가 많을 때 응답 속도 비교해보기
    @Override
    @EntityGraph(attributePaths = {"room", "course"})
    Page<Reservation> findAll(Pageable pageable);

    boolean existsByUserNameAndPhoneLastNumberAndReservationDateAndStatusNot(String userName,
        String phoneLastNumber, LocalDate reservationDate, ReservationStatus status);

    @Query("""
            select count(r)
            from  Reservation r
            where r.room.id = :roomId
            and r.reservationDate = :date
            and r.status <> :cancelledStatus
            and (r.startTime < :endTime and r.endTime > :startTime)
            and (:excludeId is null or r.id <> :roomId)
        """)
    Long existsOverlap(@Param("roomId") Long roomId, @Param("date") LocalDate date,
        @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime,
        @Param("cancelledStatus") ReservationStatus cancelledStatus,
        @Param("excludeId") Long excludeId);

    List<Reservation> findAllByRoomIdAndReservationDateAndStatusNotOrderByStartTimeAsc(Long roomId,
        LocalDate date, ReservationStatus status);

    List<Reservation> findByUserNameAndPhoneLastNumberOrderByCreatedAtDesc(String userName,
        String phoneLastNumber);
}
