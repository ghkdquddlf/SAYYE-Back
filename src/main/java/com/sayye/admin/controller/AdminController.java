package com.sayye.admin.controller;

import com.sayye.admin.dto.request.UpdatePasswordRequest;
import com.sayye.admin.dto.response.AdminResponse;
import com.sayye.admin.dto.response.UpdatePasswordResponse;
import com.sayye.admin.service.AdminService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminController {

    private final AdminService adminService;

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
    public ResponseEntity<UpdatePasswordResponse> updatePassword(
        @PathVariable Long userId,
        @Valid @RequestBody UpdatePasswordRequest req,
        Authentication authentication
    ) {
        adminService.updatePassword(userId, req, authentication.getName());
        return ResponseEntity.ok(new UpdatePasswordResponse("비밀번호가 성공적으로 변경되었습니다."));
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

}
