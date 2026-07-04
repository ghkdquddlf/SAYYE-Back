package com.sayye.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sayye.domain.course.entity.Course;
import com.sayye.domain.course.repository.CourseRepository;
import com.sayye.domain.reservation.dto.request.ReservationReqDto;
import com.sayye.domain.reservation.entity.ReservationStatus;
import com.sayye.domain.reservation.repository.ReservationRepository;
import com.sayye.domain.reservation.service.ReservationService;
import com.sayye.domain.room.entity.Room;
import com.sayye.domain.room.repository.RoomRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@ActiveProfiles("test")
class ReservationConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Room room;
    private Course course;

    @BeforeEach
    void setUp() {
        room = roomRepository.save(Room.of("테스트 회의실", 1, 10, "동시성 테스트용"));

        course = courseRepository.save(Course.builder()
            .courseName("테스트 클래스")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusMonths(3))
            .build());
    }

    @AfterEach
    void cleanUp() {
        reservationRepository.deleteAll();
        courseRepository.deleteAll();
        roomRepository.deleteAll();
    }

    @Test
    @DisplayName("[정상 동작 확인] 같은 방에 겹치지 않는 시간대를 순차 예약하면 모두 성공한다")
    void 같은_방에_다른_시간대를_순차_예약하면_모두_성공한다() {
        // given
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<LocalTime[]> timeSlots = List.of(
            new LocalTime[]{LocalTime.of(9, 0),  LocalTime.of(10, 0)},
            new LocalTime[]{LocalTime.of(10, 0), LocalTime.of(11, 0)},
            new LocalTime[]{LocalTime.of(11, 0), LocalTime.of(12, 0)},
            new LocalTime[]{LocalTime.of(13, 0), LocalTime.of(14, 0)},
            new LocalTime[]{LocalTime.of(14, 0), LocalTime.of(15, 0)}
        );

        // when
        List<Exception> exceptions = new ArrayList<>();
        List<Long> successIds = new ArrayList<>();

        for (int i = 0; i < timeSlots.size(); i++) {
            try {
                ReservationReqDto reqDto = makeReqDto(
                    "사용자" + i,
                    String.format("%04d", i),
                    course.getId(),
                    tomorrow,
                    timeSlots.get(i)[0],
                    timeSlots.get(i)[1]
                );
                var result = reservationService.createReservation(room.getId(), reqDto);
                successIds.add(result.getId());
            } catch (Exception e) {
                exceptions.add(e);
            }
        }

        // then
        long savedCount = reservationRepository.findAll().stream()
            .filter(r -> r.getRoom().getId().equals(room.getId()))
            .filter(r -> r.getReservationDate().equals(tomorrow))
            .filter(r -> r.getStatus() == ReservationStatus.RESERVED)
            .count();

        System.out.println("==============================");
        System.out.println(" 순차 예약 테스트 결과 (다른 시간대)");
        System.out.println("==============================");
        System.out.println(" 시도한 요청 수    : " + timeSlots.size());
        System.out.println(" 성공한 예약 수    : " + successIds.size());
        System.out.println(" 예외 발생 수      : " + exceptions.size());
        System.out.println(" DB에 저장된 예약  : " + savedCount);
        System.out.println("------------------------------");
        exceptions.forEach(e -> System.out.println(" 예외 내용: " + e.getMessage()));
        System.out.println("==============================");

        assertThat(exceptions).as("겹치지 않는 시간대 예약은 예외 없이 모두 성공해야 한다").isEmpty();
        assertThat(savedCount).as("모든 예약이 DB에 저장되어야 한다").isEqualTo(timeSlots.size());
    }

    @Test
    @DisplayName("[정상 동작 확인] 같은 방에 겹치지 않는 시간대를 동시 예약하면 모두 성공한다")
    void 같은_방에_다른_시간대를_동시에_예약하면_모두_성공한다() throws InterruptedException {
        // given
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<LocalTime[]> timeSlots = List.of(
            new LocalTime[]{LocalTime.of(9, 0),  LocalTime.of(10, 0)},
            new LocalTime[]{LocalTime.of(10, 0), LocalTime.of(11, 0)},
            new LocalTime[]{LocalTime.of(11, 0), LocalTime.of(12, 0)},
            new LocalTime[]{LocalTime.of(13, 0), LocalTime.of(14, 0)},
            new LocalTime[]{LocalTime.of(14, 0), LocalTime.of(15, 0)}
        );

        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
        List<Long> successIds = Collections.synchronizedList(new ArrayList<>());

        // when
        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    startLatch.await();

                    ReservationReqDto reqDto = makeReqDto(
                        "사용자" + idx,
                        String.format("%04d", idx),
                        course.getId(),
                        tomorrow,
                        timeSlots.get(idx)[0],
                        timeSlots.get(idx)[1]
                    );
                    var result = reservationService.createReservation(room.getId(), reqDto);
                    successIds.add(result.getId());

                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        // then
        long savedCount = reservationRepository.findAll().stream()
            .filter(r -> r.getRoom().getId().equals(room.getId()))
            .filter(r -> r.getReservationDate().equals(tomorrow))
            .filter(r -> r.getStatus() == ReservationStatus.RESERVED)
            .count();

        System.out.println("==============================");
        System.out.println(" 동시 예약 테스트 결과 (다른 시간대)");
        System.out.println("==============================");
        System.out.println(" 시도한 요청 수    : " + threadCount);
        System.out.println(" 성공한 예약 수    : " + successIds.size());
        System.out.println(" 예외 발생 수      : " + exceptions.size());
        System.out.println(" DB에 저장된 예약  : " + savedCount);
        System.out.println("------------------------------");
        exceptions.forEach(e -> System.out.println(" 예외 내용: " + e.getMessage()));
        System.out.println("==============================");

        assertThat(exceptions).as("겹치지 않는 시간대 동시 예약은 예외 없이 모두 성공해야 한다").isEmpty();
        assertThat(savedCount).as("모든 예약이 DB에 저장되어야 한다").isEqualTo(threadCount);
    }

    @Test
    @DisplayName("[동시성 버그 확인] 동시에 같은 방·시간대에 예약하면 중복 예약이 생성된다")
    void 동시에_같은_시간에_예약하면_중복_예약이_발생한다() throws InterruptedException {
        // given
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1); // 모든 스레드 동시 출발용
        CountDownLatch doneLatch = new CountDownLatch(threadCount);  // 모든 스레드 완료 대기용

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);

        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
        List<Long> successIds = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    startLatch.await(); // 신호 전까지 대기 → 동시 출발

                    ReservationReqDto reqDto = makeReqDto(
                        "사용자" + idx,
                        String.format("%04d", idx), // 서로 다른 유저 (중복 유저 검증 우회)
                        course.getId(),
                        tomorrow, startTime, endTime
                    );
                    var result = reservationService.createReservation(room.getId(), reqDto);
                    successIds.add(result.getId());

                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // 전체 스레드 동시 출발
        doneLatch.await();      // 모든 스레드 완료까지 대기
        executor.shutdown();

        // then
        long savedCount = reservationRepository.findAll().stream()
            .filter(r -> r.getRoom().getId().equals(room.getId()))
            .filter(r -> r.getReservationDate().equals(tomorrow))
            .filter(r -> r.getStatus() == ReservationStatus.RESERVED)
            .count();

        System.out.println("==============================");
        System.out.println(" 동시성 테스트 결과");
        System.out.println("==============================");
        System.out.println(" 시도한 요청 수    : " + threadCount);
        System.out.println(" 성공한 예약 수    : " + successIds.size());
        System.out.println(" 예외 발생 수      : " + exceptions.size());
        System.out.println(" DB에 저장된 예약  : " + savedCount);
        System.out.println("==============================");

        if (savedCount > 1) {
            System.out.println("[FAIL] 동시성 문제 발생! " + savedCount + "개의 중복 예약이 생성됨");
        } else {
            System.out.println("[PASS] 동시성 제어 정상 동작 (예약 1건만 생성됨)");
        }

        // 동시성 제어가 없으면 이 assertion이 통과됨 (버그 존재 = 테스트 성공)
        assertThat(savedCount)
            .as("동시성 제어가 없으면 중복 예약이 생성되어야 한다 (버그 확인용)")
            .isGreaterThan(1);
    }

    private ReservationReqDto makeReqDto(String userName, String phone, Long courseId,
        LocalDate date, LocalTime startTime, LocalTime endTime) {
        ReservationReqDto dto = new ReservationReqDto();
        ReflectionTestUtils.setField(dto, "courseId", courseId);
        ReflectionTestUtils.setField(dto, "userName", userName);
        ReflectionTestUtils.setField(dto, "phoneLastNumber", phone);
        ReflectionTestUtils.setField(dto, "reservationDate", date);
        ReflectionTestUtils.setField(dto, "startTime", startTime);
        ReflectionTestUtils.setField(dto, "endTime", endTime);
        return dto;
    }
}
