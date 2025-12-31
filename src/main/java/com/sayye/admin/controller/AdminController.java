package com.sayye.admin.controller;

import com.sayye.admin.dto.request.UpdatePasswordRequest;
import com.sayye.admin.dto.response.AdminResponse;
import com.sayye.admin.service.AdminService;
import com.sayye.reservation.dto.request.AdminReservationReqDto;
import com.sayye.reservation.dto.response.ReservationAdminResDto;
import com.sayye.reservation.dto.response.ReservationResDto;
import com.sayye.reservation.service.AdminReservationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;
    private final AdminReservationService adminReservationService;

    // 관리자 전체 조회 - MASTER만 가능
    @PreAuthorize("hasRole('MASTER')")
    @GetMapping
    public ResponseEntity<List<AdminResponse>> findAll() {
        return ResponseEntity.ok(adminService.findAll());
    }

    // 관리자 단일 조회 - MASTER는 모두 조회, ADMIN은 본인만 조회
    @GetMapping("/{userId}")
    public ResponseEntity<AdminResponse> findById(
        @PathVariable Long userId,
        Authentication authentication
    ) {
        return ResponseEntity.ok(adminService.findById(userId, authentication.getName()));
    }

    // 관리자 비밀번호 수정 - 본인만 가능
    @PatchMapping("/{userId}")
    public ResponseEntity<String> updatePassword(
        @PathVariable Long userId,
        @Valid @RequestBody UpdatePasswordRequest req,
        Authentication authentication
    ) {
        adminService.updatePassword(userId, req, authentication.getName());
        return ResponseEntity.ok()
                .body("비밀번호 수정이 완료되었습니다.");
    }

    // 관리자 삭제 - MASTER만 가능
    @PreAuthorize("hasRole('MASTER')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(
        @PathVariable Long userId
    ) {
        adminService.delete(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rooms/{roomId}/reservations")
    public ResponseEntity<ReservationResDto> createAdminReservation(@PathVariable Long roomId,
        @Valid @RequestBody AdminReservationReqDto reqDto, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(adminReservationService.createAdminReservation(roomId, reqDto, authentication.getName()));
    }

    // 관리자 조회용
    @GetMapping("/reservations")
    public ResponseEntity<Page<ReservationAdminResDto>> getAllReservationsForAdmin(
        @RequestParam(defaultValue = "1") int page) {
        int pageNumber = (page <= 0) ? 1 : page;

        return ResponseEntity.ok(adminReservationService.getAllReservations(pageNumber));
    }

}
