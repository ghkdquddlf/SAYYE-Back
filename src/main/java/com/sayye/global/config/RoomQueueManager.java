package com.sayye.global.config;

import com.sayye.domain.reservation.dto.request.ReservationReqDto;
import com.sayye.domain.reservation.dto.response.ReservationResDto;
import com.sayye.domain.reservation.service.ReservationService;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomQueueManager {

    private final Map<Long, BlockingQueue<Long>> roomQueue = new ConcurrentHashMap<>();
    private final ExecutorService executorService;
    private final ReservationService reservationService;

    // 테스트 훅 - 기본값은 no-op
    private volatile Consumer<Long> onProcessingStart = id -> {};
    private volatile Consumer<Long> onProcessingComplete = id -> {};

    public void setOnProcessingStart(Consumer<Long> hook) {
        this.onProcessingStart = hook;
    }

    public void setOnProcessingComplete(Consumer<Long> hook) {
        this.onProcessingComplete = hook;
    }

    public void enqueue(Long roomId, Long requestId) {
        roomQueue.computeIfAbsent(roomId, this::createQueue).offer(requestId);
    }

    private BlockingQueue<Long> createQueue(Long roomId) {
        BlockingQueue<Long> queue = new LinkedBlockingQueue<>();
        startConsumer(roomId, queue);
        return queue;
    }

    private void startConsumer(Long roomId, BlockingQueue<Long> queue) {
        executorService.submit(() -> {
            while (true) {
                try {
                    Long requestId = queue.take();
                    try {
                        ReservationReqDto reqDto = reservationService.markProcessing(requestId);

                        onProcessingStart.accept(requestId);
                        try {
                            ReservationResDto result = reservationService.createReservation(roomId, reqDto);
                            reservationService.markConfirmed(requestId, result.getId());
                        } catch (Exception e) {
                            reservationService.markFailed(requestId, e.getMessage());
                        }
                        onProcessingComplete.accept(requestId);
                    } catch (Exception e) {
                        log.error("예약 요청 처리 중 예상치 못한 오류 발생. roomId={}, requestId={}",
                            roomId, requestId, e);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }
}
