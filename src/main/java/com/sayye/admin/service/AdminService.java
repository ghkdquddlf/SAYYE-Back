package com.sayye.admin.service;

import com.sayye.admin.dto.request.UpdatePasswordRequest;
import com.sayye.admin.dto.request.SignupRequest;
import com.sayye.admin.dto.response.AdminResponse;
import com.sayye.admin.entity.Admin;
import com.sayye.admin.Role;
import com.sayye.admin.repository.AdminRepository;
import com.sayye.exception.ApiException;
import com.sayye.exception.ErrorCode;
import java.util.ArrayList;
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
        List<Admin> admins = adminRepository.findAll();
        List<AdminResponse> dtoList = new ArrayList<>();

        for (Admin admin : admins) {
            dtoList.add(new AdminResponse(admin));
        }

        return dtoList;
    }

    public AdminResponse findById(
        Long userId,
        String currentUserId
    ) {
        Admin admin = getAdminById(userId);
        Admin currentAdmin = adminRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ApiException(ErrorCode.ADMIN_NOT_FOUND_ERROR));
        
        // MASTER는 모든 관리자 조회 가능, ADMIN은 본인만 조회 가능
        if (currentAdmin.getRole() != Role.MASTER && !admin.getUserId().equals(currentUserId)) {
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
    public AdminResponse signup(SignupRequest req) {
        // 중복 체크
        if (adminRepository.findByUserId(req.getUserId()).isPresent()) {
            throw new ApiException(ErrorCode.ADMIN_USER_ID_DUPLICATED);
        }

        Admin admin = new Admin();
        admin.setUserId(req.getUserId());
        admin.setPassword(passwordEncoder.encode(req.getPassword()));
        admin.setName(req.getName());
        admin.setEmail(req.getEmail());
        admin.setRole(Role.ADMIN); // 기본 ADMIN 권한 부여, 추후 설정 가능

        adminRepository.save(admin);

        return new AdminResponse(admin);
    }

    @Transactional
    public void delete(Long userId) {
        adminRepository.deleteById(userId);
    }

    // id로 관리자 찾는 메서드
    private Admin getAdminById(Long userId) {
        return adminRepository.findById(userId).orElseThrow(
            () -> new ApiException(ErrorCode.ADMIN_NOT_FOUND_ERROR)
        );
    }

}
