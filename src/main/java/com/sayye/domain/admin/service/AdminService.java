package com.sayye.domain.admin.service;

import com.sayye.domain.admin.dto.request.UpdatePasswordRequest;
import com.sayye.domain.admin.dto.request.SignupRequest;
import com.sayye.domain.admin.dto.response.AdminResponse;
import com.sayye.domain.admin.entity.Admin;
import com.sayye.domain.admin.Role;
import com.sayye.domain.admin.repository.AdminRepository;
import com.sayye.global.exception.ApiException;
import com.sayye.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    // 관리자 전체 조회
    public List<AdminResponse> findAll() {
        return adminRepository.findAll().stream()
            .map(AdminResponse::new)
            .toList();
    }

    public AdminResponse findById(
        Long userId,
        String currentUserId
    ) {
        Admin admin = getAdminById(userId);
        Admin currentAdmin = adminRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ApiException(ErrorCode.ADMIN_NOT_FOUND_ERROR));
        
        // MASTER는 모든 관리자 조회 가능, ADMIN은 본인만 조회 가능
        if (!currentAdmin.canAccessAdmin(admin.getUserId())) {
            throw new ApiException(ErrorCode.ADMIN_ACCESS_DENIED);
        }

        return new AdminResponse(admin);
    }

    // 관리자 비밀번호 수정
    @Transactional
    public void updatePassword(
        Long userId,
        UpdatePasswordRequest request,
        String currentUserId
    ) {
        Admin admin = getAdminById(userId);
        
        // 본인만 비밀번호 수정 가능
        if (!admin.getUserId().equals(currentUserId)) {
            throw new ApiException(ErrorCode.ADMIN_ACCESS_DENIED);
        }

        if (!passwordEncoder.matches(request.getOldPassword(), admin.getPassword())) {
            throw new ApiException(ErrorCode.ADMIN_PASSWORD_MISMATCH);
        }

        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new ApiException(ErrorCode.ADMIN_PASSWORD_SAME);
        }

        admin.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public AdminResponse signup(SignupRequest request) {
        // userId 중복 체크
        if (adminRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new ApiException(ErrorCode.ADMIN_USER_ID_DUPLICATED);
        }

        // 관리자 이름 중복 체크
        if (adminRepository.existsByName(request.getName())) {
            throw new ApiException(ErrorCode.ADMIN_NAME_DUPLICATED);
        }

        // 이메일 중복 체크
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorCode.ADMIN_EMAIL_DUPLICATED);
        }

        // 정적 팩토리 메서드를 통한 객체 생성
        Admin admin = Admin.of(
            request.getUserId(),
            passwordEncoder.encode(request.getPassword()),
            request.getName(),
            request.getEmail(),
            Role.ADMIN  // 기본 ADMIN 권한 부여, 추후 설정 가능
        );

        adminRepository.save(admin);

        return new AdminResponse(admin);
    }

    @Transactional
    public void delete(Long userId) {
        adminRepository.deleteById(userId);
    }

    // id로 관리자 찾는 메서드
    private Admin getAdminById(Long id) {
        return adminRepository.findById(id).orElseThrow(
            () -> new ApiException(ErrorCode.ADMIN_NOT_FOUND_ERROR)
        );
    }

    // id로 관리자 찾는 메서드
    public Admin getAdminByUserId(String userId) {
        return adminRepository.findByUserId(userId).orElseThrow(
            () -> new ApiException(ErrorCode.ADMIN_NOT_FOUND_ERROR)
        );
    }

}
