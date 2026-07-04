package com.sayye.global.config;

import com.sayye.domain.reservation.dto.response.ReservationResDto;
import com.sayye.domain.reservation.entity.ReservationReq;
import com.sayye.domain.reservation.repository.ReservationReqRepository;
import com.sayye.domain.reservation.service.ReservationService;
import com.sayye.domain.room.entity.Room;
import com.sayye.domain.room.repository.RoomRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomQueueManager {

    private final Map<Long, BlockingQueue<Long>> roomQueue = new ConcurrentHashMap<>();
    private final ExecutorService executorService;
    private final RoomRepository roomRepository;
    private final ReservationService reservationService;
    private final ReservationReqRepository reservationReqRepository;

    // 테스트 훅 - 기본값은 no-op
    private Consumer<Long> onProcessingStart = id -> {};
    private Consumer<Long> onProcessingComplete = id -> {};

    public void setOnProcessingStart(Consumer<Long> hook) {
        this.onProcessingStart = hook;
    }

    public void setOnProcessingComplete(Consumer<Long> hook) {
        this.onProcessingComplete = hook;
    }

    @PostConstruct
    public void init() {
        List<Room> rooms = roomRepository.findAll();
        rooms.forEach(this::registerRoom);
    }

    public void registerRoom(Room room) {
        BlockingQueue<Long> queue = new LinkedBlockingQueue<>();
        roomQueue.put(room.getId(), queue);
        startConsumer(room.getId(), queue);
    }

    public void enqueue(Long roomId, Long requestId) {
        BlockingQueue<Long> queue = roomQueue.get(roomId);
        if (queue == null) {
            throw new IllegalArgumentException("존재하지 않는 회의실입니다: " + roomId);
        }
        queue.offer(requestId);
    }

    private void startConsumer(Long roomId, BlockingQueue<Long> queue) {
        executorService.submit(() -> {
            while (true) {
                try {
                    Long requestId = queue.take();
                    ReservationReq req = reservationReqRepository.findById(requestId)
                        .orElseThrow(() -> new IllegalStateException("예약 요청을 찾을 수 없습니다: " + requestId));
                    req.markProcessing();
                    reservationReqRepository.save(req);

                    onProcessingStart.accept(requestId);
                    try {
                        ReservationResDto result = reservationService.createReservation(roomId, req.toDto());
                        req.markConfirmed(result.getId());
                    } catch (Exception e) {
                        req.markFailed(e.getMessage());
                    }
                    onProcessingComplete.accept(requestId);

                    reservationReqRepository.save(req);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }
}
