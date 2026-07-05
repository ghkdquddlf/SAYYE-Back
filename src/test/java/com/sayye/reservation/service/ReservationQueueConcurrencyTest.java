package com.sayye.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.sayye.domain.course.entity.Course;
import com.sayye.domain.course.repository.CourseRepository;
import com.sayye.domain.reservation.dto.request.ReservationReqDto;
import com.sayye.domain.reservation.entity.RequestStatus;
import com.sayye.domain.reservation.entity.ReservationReq;
import com.sayye.domain.reservation.repository.ReservationReqRepository;
import com.sayye.domain.reservation.repository.ReservationRepository;
import com.sayye.domain.reservation.service.ReservationService;
import com.sayye.domain.room.entity.Room;
import com.sayye.domain.room.repository.RoomRepository;
import com.sayye.global.config.RoomQueueManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

@SpringBootTest
@ActiveProfiles("test")
class ReservationQueueConcurrencyTest {

    @Autowired private RoomQueueManager roomQueueManager;
    @Autowired private ReservationService reservationService;
    @Autowired private RoomRepository roomRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private ReservationReqRepository reservationReqRepository;

    private Room room;
    private Course course;

    @BeforeEach
    void setUp() {
        room = roomRepository.save(Room.of("큐 테스트 회의실", 1, 10, "큐 동시성 테스트용"));
        // @PostConstruct 이후 생성된 방이라 큐에 등록되어 있지 않으므로 수동 등록
        roomQueueManager.registerRoom(room.getId());

        course = courseRepository.save(Course.builder()
            .courseName("테스트 클래스")
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusMonths(3))
            .build());
    }

    @AfterEach
    void cleanUp() {
        // 훅 초기화 (다음 테스트에 영향 없도록)
        roomQueueManager.setOnProcessingStart(id -> {});
        roomQueueManager.setOnProcessingComplete(id -> {});

        reservationRepository.deleteAll();
        reservationReqRepository.deleteAll();
        courseRepository.deleteAll();
        roomRepository.deleteAll();
    }

    @Test
    @DisplayName("[직렬화 증명] 동시에 N개 요청 → 처리 구간이 겹치는 쌍이 없어야 한다")
    void 동시_요청의_처리_구간이_겹치지_않는다() throws InterruptedException {
        // given
        int requestCount = 5;
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Long> requestIds = submitRequests(requestCount, tomorrow,
            LocalTime.of(10, 0), LocalTime.of(11, 0));

        // 처리 시작/종료 시각 기록 (requestId → 나노초 타임스탬프)
        Map<Long, Long> startTimes = new ConcurrentHashMap<>();
        Map<Long, Long> endTimes = new ConcurrentHashMap<>();

        // Consumer 완료 시점에 카운트다운 - "처리가 다 끝났을 때" 기다리기 위해
        CountDownLatch consumerDone = new CountDownLatch(requestCount);

        roomQueueManager.setOnProcessingStart(requestId -> {
            startTimes.put(requestId, System.nanoTime());
            // 직렬화 검증을 위한 인위적 지연 - 이 사이에 다른 요청이 끼어들면 구간이 겹침
            try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        roomQueueManager.setOnProcessingComplete(requestId -> {
            endTimes.put(requestId, System.nanoTime());
            consumerDone.countDown(); // Producer가 아닌 Consumer 완료 시점에 카운트다운
        });

        // when - N개 요청을 동시에 큐에 적재
        CountDownLatch startLatch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(requestCount);

        for (Long requestId : requestIds) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    roomQueueManager.enqueue(room.getId(), requestId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        startLatch.countDown(); // 동시 출발
        consumerDone.await();   // Consumer가 모두 완료할 때까지 대기 (폴링 없이 정확한 시점)
        executor.shutdown();

        // then 1 - 최종 상태: 1개 CONFIRMED, 나머지 FAILED
        List<ReservationReq> results = reservationReqRepository.findAllById(requestIds);
        long confirmedCount = results.stream().filter(r -> r.getStatus() == RequestStatus.CONFIRMED).count();
        long failedCount    = results.stream().filter(r -> r.getStatus() == RequestStatus.FAILED).count();

        // then 2 - 직렬화 증명: 어떤 두 처리 구간도 겹치지 않아야 한다
        boolean hasOverlap = false;
        for (int i = 0; i < requestIds.size(); i++) {
            for (int j = i + 1; j < requestIds.size(); j++) {
                long s1 = startTimes.get(requestIds.get(i));
                long e1 = endTimes.get(requestIds.get(i));
                long s2 = startTimes.get(requestIds.get(j));
                long e2 = endTimes.get(requestIds.get(j));

                if (s1 < e2 && s2 < e1) {
                    hasOverlap = true;
                    System.out.printf(" [겹침 발생] 요청 %d (%d~%d) ↔ 요청 %d (%d~%d)%n",
                        i, s1, e1, j, s2, e2);
                }
            }
        }

        printResult(requestCount, confirmedCount, failedCount, results);

        assertThat(hasOverlap).as("어떤 두 요청도 동시에 처리되어선 안 된다 (직렬화 실패)").isFalse();
        assertThat(confirmedCount).as("정확히 1개만 CONFIRMED 되어야 한다").isEqualTo(1);
        assertThat(failedCount).as("나머지는 모두 FAILED 되어야 한다").isEqualTo(requestCount - 1);
    }

    @Test
    @DisplayName("[정상 처리] 다른 시간대 5개 요청 → 모두 CONFIRMED, 구간 겹침 없음")
    void 다른_시간대_요청은_모두_성공하고_직렬화된다() throws InterruptedException {
        // given
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<LocalTime[]> timeSlots = List.of(
            new LocalTime[]{LocalTime.of(9, 0),  LocalTime.of(10, 0)},
            new LocalTime[]{LocalTime.of(10, 0), LocalTime.of(11, 0)},
            new LocalTime[]{LocalTime.of(11, 0), LocalTime.of(12, 0)},
            new LocalTime[]{LocalTime.of(13, 0), LocalTime.of(14, 0)},
            new LocalTime[]{LocalTime.of(14, 0), LocalTime.of(15, 0)}
        );

        List<Long> requestIds = new ArrayList<>();
        for (int i = 0; i < timeSlots.size(); i++) {
            ReservationReqDto dto = ReservationReqDto.builder()
                .courseId(course.getId())
                .userName("사용자" + i)
                .phoneLastNumber(String.format("%04d", i))
                .reservationDate(tomorrow)
                .startTime(timeSlots.get(i)[0])
                .endTime(timeSlots.get(i)[1])
                .build();
            requestIds.add(reservationService.submitReservation(room.getId(), dto).getId());
        }

        CountDownLatch consumerDone = new CountDownLatch(timeSlots.size());
        roomQueueManager.setOnProcessingComplete(id -> consumerDone.countDown());

        // when
        for (Long requestId : requestIds) {
            roomQueueManager.enqueue(room.getId(), requestId);
        }

        consumerDone.await(); // Consumer 완료까지 대기

        // then
        List<ReservationReq> results = reservationReqRepository.findAllById(requestIds);
        long confirmedCount = results.stream().filter(r -> r.getStatus() == RequestStatus.CONFIRMED).count();
        long failedCount    = results.stream().filter(r -> r.getStatus() == RequestStatus.FAILED).count();

        printResult(timeSlots.size(), confirmedCount, failedCount, results);

        assertThat(confirmedCount).as("겹치지 않는 시간대는 모두 CONFIRMED 되어야 한다").isEqualTo(timeSlots.size());
        assertThat(failedCount).as("실패한 요청이 없어야 한다").isEqualTo(0);
    }

    private List<Long> submitRequests(int count, LocalDate date,
        LocalTime startTime, LocalTime endTime) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ReservationReqDto dto = ReservationReqDto.builder()
                .courseId(course.getId())
                .userName("사용자" + i)
                .phoneLastNumber(String.format("%04d", i))
                .reservationDate(date)
                .startTime(startTime)
                .endTime(endTime)
                .build();
            ids.add(reservationService.submitReservation(room.getId(), dto).getId());
        }
        return ids;
    }

    private void printResult(long total, long confirmed, long failed, List<ReservationReq> results) {
        System.out.println("==============================");
        System.out.println(" 큐 기반 동시성 테스트 결과");
        System.out.println("==============================");
        System.out.println(" 시도한 요청 수 : " + total);
        System.out.println(" CONFIRMED      : " + confirmed);
        System.out.println(" FAILED         : " + failed);
        System.out.println("------------------------------");
        results.stream()
            .filter(r -> r.getStatus() == RequestStatus.FAILED)
            .forEach(r -> System.out.println(" 실패 사유: " + r.getFailureMessage()));
        System.out.println("==============================");
    }
}
