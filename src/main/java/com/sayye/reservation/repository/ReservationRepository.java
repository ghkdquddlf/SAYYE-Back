package com.sayye.reservation.repository;

import com.sayye.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Todo 데이터가 많을 때 응답 속도 비교해보기
    @Override
    @EntityGraph(attributePaths = {"room", "course"})
    Page<Reservation> findAll(Pageable pageable);

}
