//package com.sayye.reservation.service;
//
//import com.sayye.course.entity.Course;
//import com.sayye.course.repository.CourseRepository;
//import com.sayye.exception.ApiException;
//import com.sayye.exception.ErrorCode;
//import com.sayye.reservation.dto.CancelReservationReqDto;
//import com.sayye.reservation.dto.ReservationAdminResDto;
//import com.sayye.reservation.dto.ReservationReqDto;
//import com.sayye.reservation.dto.ReservationResDto;
//import com.sayye.reservation.entity.Reservation;
//import com.sayye.reservation.entity.ReservationStatus;
//import com.sayye.reservation.repository.ReservationRepository;
//import com.sayye.room.entity.Room;
//import com.sayye.room.repository.RoomRepository;
//import java.time.Duration;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.domain.Sort.Direction;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@RequiredArgsConstructor
//@Service
//@Transactional(readOnly = true)
//public class ReservationService {
//
//    private static final int DEFAULT_SIZE = 10;
//    private static final String SORT_BY = "createdAt";
//
//    private final ReservationRepository reservationRepository;
//
//    // Todo service 구현 완료 시 변경 필요
//    private final RoomRepository roomRepository;
//    private final CourseRepository courseRepository;
//
//    @Transactional
//    public void cancelReservation(Long reservationId, CancelReservationReqDto reqDto) {
//        Reservation reservation = reservationRepository.findById(reservationId)
//            .orElseThrow(() -> new ApiException(ErrorCode.RESERVATION_NOT_FOUND));
//
//        if (!reservation.isOwner(reqDto.getUserName(), reqDto.getPhoneLastNumber())) {
//            throw new ApiException(ErrorCode.RESERVATION_CANCEL_UNAUTHORIZED);
//        }
//
//        if (!reservation.isCancelable()) {
//            throw new ApiException(ErrorCode.RESERVATION_CANCEL_TIME_EXCEEDED);
//        }
//
//        reservationRepository.delete(reservation);
//    }
//
//    public Page<ReservationAdminResDto> getAllReservations(int page) {
//        Pageable pageable = PageRequest.of(page - 1, DEFAULT_SIZE,
//            Sort.by(Direction.DESC, SORT_BY));
//
//        return reservationRepository.findAll(pageable).map(ReservationAdminResDto::from);
//    }
//
//    @Transactional
//    public ReservationResDto createReservation(Long roomId, ReservationReqDto reqDto) {
//        // 예약 시작 시간이 10시 전이면
//        validateReservationTime(reqDto.getStartTime(), reqDto.getEndTime());
//
//        // Todo 회의실 존재 여부 검증
//        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException());
//
//        // Todo 클래스 존재 여부 검증
//        Course course = courseRepository.findById(1L).orElseThrow(() -> new RuntimeException());
//
//        // 해당 예약자가 예약 날짜에 이미 예약 했다면
//        boolean alreadyReserved = reservationRepository.existsByUserNameAndPhoneLastNumberAndReservationDateAndStatusNot(
//            reqDto.getUserName(),
//            reqDto.getPhoneLastNumber(),
//            reqDto.getReservationDate(),
//            ReservationStatus.CANCELED
//        );
//
//        if (alreadyReserved) {
//            throw new ApiException(ErrorCode.RESERVATION_USER_DUPLICATED);
//        }
//
//        // 예약자가 예약한 시간에 이미 예약 되어 있다면
//        // Todo room.getId로 수정 필요
//        if (reservationRepository.existsOverlap(roomId, reqDto.getReservationDate(),
//            reqDto.getStartTime(), reqDto.getEndTime(), ReservationStatus.CANCELED) > 0) {
//            throw new ApiException(ErrorCode.RESERVATION_TIME_OVERLAPPED);
//        }
//
//        Reservation saved = reservationRepository.save(reqDto.toEntity(room, course));
//        return ReservationResDto.from(saved);
//
//    }
//
//
//    public List<ReservationResDto> getReservationsByRoomId(Long roomId, LocalDate reservationDate) {
//        // Todo 회의실 존재 여부 검증
//        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException());
//
//        List<Reservation> reservations = reservationRepository.
//            findAllByRoomIdAndReservationDateAndStatusNotOrderByStartTimeAsc(room.getId(),
//                reservationDate, ReservationStatus.CANCELED);
//
//        return reservations.stream().map(ReservationResDto::from).toList();
//
//    }
//
//    private void validateReservationTime(LocalTime startTime, LocalTime endTime) {
//        // 예약 시작 시간이 10시 전이면
//        if (startTime.isBefore(LocalTime.of(10, 0))) {
//            throw new ApiException(ErrorCode.RESERVATION_INVALID_START_TIME);
//        }
//
//        // 이용 시간이 2시간 이상이면
//        long durationMinutes = Duration.between(startTime, endTime).toMinutes();
//        if (durationMinutes > 120) {
//            throw new ApiException(ErrorCode.RESERVATION_EXCEED_MAX_DURATION);
//        }
//    }
//}
