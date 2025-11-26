package com.sayye.admin.controller;

import com.sayye.admin.dto.request.UpdatePasswordRequest;
import com.sayye.admin.dto.response.AdminResponse;
import com.sayye.admin.service.AdminService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminController {

    private final AdminService adminService;

    // 관리자 전체 조회
    @GetMapping
    public ResponseEntity<List<AdminResponse>> findAll() {
        return ResponseEntity.ok(adminService.findAll());
    }

    // 관리자 단일 조회
    @GetMapping("/{userId}")
    public ResponseEntity<AdminResponse> findById(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.findById(userId));
    }

    // 관리자 비밀번호 수정(AuthAdmin 추가해야 함)
    @PatchMapping("/{userId}")
    public void updatePassword(
        @PathVariable Long userId,
        @RequestBody UpdatePasswordRequest request
    ) {
        adminService.updatePassword(userId, request);
    }
}
