package com.sayye.reservation.service;

import com.sayye.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor
public class ReservationTest {

    private final ReservationService reservationService;

    @Test
    @DisplayName("동시성 예약 테스트")
    void reservationTest(){

    }
}
