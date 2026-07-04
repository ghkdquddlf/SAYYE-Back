package com.sayye.domain.reservation.service;

import com.sayye.domain.course.entity.Course;
import com.sayye.domain.course.repository.CourseRepository;
import com.sayye.global.exception.ApiException;
import com.sayye.global.exception.ErrorCode;
import com.sayye.domain.reservation.dto.request.CancelReservationReqDto;
import com.sayye.domain.reservation.dto.request.ReadReservationReqDto;
import com.sayye.domain.reservation.dto.request.ReservationReqDto;
import com.sayye.domain.reservation.dto.request.UpdateReservationReqDto;
import com.sayye.domain.reservation.dto.response.ReservationAdminResDto;
import com.sayye.domain.reservation.dto.response.ReservationRequestStatusResDto;
import com.sayye.domain.reservation.dto.response.ReservationResDto;
import com.sayye.domain.reservation.entity.Reservation;
import com.sayye.domain.reservation.entity.ReservationReq;
import com.sayye.domain.reservation.entity.ReservationStatus;
import com.sayye.domain.reservation.repository.ReservationReqRepository;
import com.sayye.domain.reservation.repository.ReservationRepository;
import com.sayye.domain.room.entity.Room;
import com.sayye.domain.room.repository.RoomRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReservationService {

    private static final int DEFAULT_SIZE = 10;
    private static final String SORT_BY = "createdAt";

    private final ReservationRepository reservationRepository;
    private final ReservationReqRepository reservationReqRepository;

    // Todo service 구현 완료 시 변경 필요
    private final RoomRepository roomRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public ReservationReq submitReservation(Long roomId, ReservationReqDto reqDto) {
        return reservationReqRepository.save(ReservationReq.toEntity(reqDto, roomId));
    }

    public ReservationRequestStatusResDto getRequestStatus(Long requestId) {
        ReservationReq req = reservationReqRepository.findById(requestId)
            .orElseThrow(() -> new ApiException(ErrorCode.RESERVATION_NOT_FOUND));
        return ReservationRequestStatusResDto.from(req);
    }

    @Transactional
    public void cancelReservation(Long reservationId, CancelReservationReqDto reqDto) {
        Reservation reservation = findReservation(reservationId);

        validateOwner(reservation, reqDto.getUserName(), reqDto.getPhoneLastNumber());
        validateModifiableTime(reservation);

        reservation.cancel();
    }

    public Page<ReservationAdminResDto> getAllReservationsForAdmin(int page) {
        Pageable pageable = PageRequest.of(page - 1, DEFAULT_SIZE,
            Sort.by(Direction.DESC, SORT_BY));

        return reservationRepository.findAll(pageable).map(ReservationAdminResDto::from);
    }

    public List<ReservationResDto> getAllReservations(ReadReservationReqDto reqDto) {
        List<Reservation> reservations = reservationRepository.findByUserNameAndPhoneLastNumberOrderByCreatedAtDesc(
            reqDto.getUserName(), reqDto.getPhoneLastNumber());
        return reservations.stream().map(ReservationResDto::from).toList();
    }

    public ReservationResDto getReservationDetail(Long reservationId,
        ReadReservationReqDto reqDto) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ApiException(ErrorCode.RESERVATION_NOT_FOUND));

        validateOwner(reservation, reqDto.getUserName(), reqDto.getPhoneLastNumber());
        return ReservationResDto.from(reservation);
    }


    @Transactional
    public ReservationResDto createReservation(Long roomId, ReservationReqDto reqDto) {

        // 익일까지만 예약 가능 검증
        validateBookingAvailableTime(reqDto.getReservationDate());

        // 예약 시작 시간이 10시 전이면
        validateReservationTime(reqDto.getStartTime(), reqDto.getEndTime());

        // Todo 회의실 존재 여부 검증
        Room room = roomRepository.findByIdWithLock(roomId).orElseThrow(() -> new RuntimeException());

        // Todo 클래스 존재 여부 검증
        Course course = courseRepository.findById(reqDto.getCourseId())
            .orElseThrow(() -> new RuntimeException());

        // 해당 예약자가 예약 날짜에 이미 예약 했다면
        validateDuplicateUser(reqDto.getUserName(), reqDto.getPhoneLastNumber(),
            reqDto.getReservationDate());

        // 예약자가 예약한 시간에 이미 예약 되어 있다면
        // Todo room.getId로 수정 필요
        validateOverlap(room.getId(), reqDto.getReservationDate(), reqDto.getStartTime(),
            reqDto.getEndTime(), null);

        Reservation saved = reservationRepository.save(reqDto.toEntity(room, course));
        return ReservationResDto.from(saved);

    }


    public List<ReservationResDto> getReservationsByRoomId(Long roomId, LocalDate reservationDate) {
        // Todo 회의실 존재 여부 검증
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException());

        List<Reservation> reservations = reservationRepository.
            findAllByRoomIdAndReservationDateAndStatusNotInOrderByStartTimeAsc(room.getId(),
                reservationDate,
                List.of(ReservationStatus.CANCELED, ReservationStatus.CANCELLED_BY_ADMIN));

        return reservations.stream().map(ReservationResDto::from).toList();

    }

    @Transactional
    public ReservationResDto updateReservation(Long reservationId, UpdateReservationReqDto reqDto) {
        Reservation reservation = findReservation(reservationId);

        validateOwner(reservation, reqDto.getUserName(), reqDto.getPhoneLastNumber());

        validateModifiableTime(reservation);

        validateReservationTime(reqDto.getStartTime(), reqDto.getEndTime());

        // 날짜가 바뀌었을 때만 검증
        if (reservation.isDateChanged(reqDto.getReservationDate())) {
            validateBookingAvailableTime(reqDto.getReservationDate());

            validateDuplicateUser(reqDto.getUserName(), reqDto.getPhoneLastNumber(),
                reqDto.getReservationDate());
        }

        validateOverlap(reservation.getRoom().getId(), reqDto.getReservationDate(),
            reqDto.getStartTime(), reqDto.getEndTime(), reservationId);

        reservation.updateReservationTime(reqDto.getStartTime(), reqDto.getEndTime(),
            reqDto.getReservationDate());

        return ReservationResDto.from(reservation);
    }

    private Reservation findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ApiException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    // 예약자 일치 여부
    private void validateOwner(Reservation reservation, String userName, String phoneLastNumber) {
        if (!reservation.isOwner(userName, phoneLastNumber)) {
            throw new ApiException(ErrorCode.RESERVATION_UNAUTHORIZED);
        }
    }

    // 취소/변경 가능 시간 여부
    private void validateModifiableTime(Reservation reservation) {
        if (!reservation.isModifiable(LocalDateTime.now())) {
            throw new ApiException(ErrorCode.RESERVATION_TIME_LIMIT_EXCEEDED);
        }
    }

    private void validateDuplicateUser(String userName, String phone, LocalDate date) {
        boolean alreadyReserved = reservationRepository.existsByUserNameAndPhoneLastNumberAndReservationDateAndStatusNotIn(
            userName,
            phone,
            date,
            List.of(ReservationStatus.CANCELED, ReservationStatus.CANCELLED_BY_ADMIN)
        );

        if (alreadyReserved) {
            throw new ApiException(ErrorCode.RESERVATION_USER_DUPLICATED);
        }

    }

    private void validateOverlap(Long roomId, LocalDate reservationDate, LocalTime startTime,
        LocalTime endTime, Long excludeId) {
        if (reservationRepository.existsOverlap(roomId, reservationDate, startTime, endTime,
            List.of(ReservationStatus.CANCELED, ReservationStatus.CANCELLED_BY_ADMIN), excludeId)
            > 0) {
            throw new ApiException(ErrorCode.RESERVATION_TIME_OVERLAPPED);
        }
    }

    // 예약 시 검증
    private void validateReservationTime(LocalTime startTime, LocalTime endTime) {
        // 예약 시작 시간이 9시 전이면
        if (startTime.isBefore(LocalTime.of(9, 0))) {
            throw new ApiException(ErrorCode.RESERVATION_INVALID_START_TIME);
        }

        // 이용 시간이 2시간 이상이면
        long durationMinutes = Duration.between(startTime, endTime).toMinutes();
        if (durationMinutes > 120) {
            throw new ApiException(ErrorCode.RESERVATION_EXCEED_MAX_DURATION);
        }
    }

    private void validateBookingAvailableTime(LocalDate reservationDate) {
        // 현재 날짜와 시간
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime reservationDateTime = reservationDate.atTime(LocalTime.MAX);

        LocalDateTime endOfTomorrow = now.toLocalDate().plusDays(1).atTime(LocalTime.MAX);

        if (reservationDateTime.isBefore(now) || reservationDateTime.isAfter(endOfTomorrow)) {
            throw new ApiException(ErrorCode.INVALID_RESERVATION_DATE);
        }

    }
}