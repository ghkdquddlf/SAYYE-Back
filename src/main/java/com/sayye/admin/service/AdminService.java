package com.sayye.admin.service;

import com.sayye.admin.dto.request.UpdatePasswordRequest;
import com.sayye.admin.dto.response.AdminResponse;
import com.sayye.admin.entity.Admin;
import com.sayye.admin.repository.AdminRepository;
import com.sayye.exception.ApiException;
import com.sayye.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository repository;

    // 관리자 전체 조회
    @Transactional(readOnly = true)
    public List<AdminResponse> findAll() {
        List<Admin> admins = repository.findAll();
        List<AdminResponse> dtoList = new ArrayList<>();

        for (Admin admin : admins) {
            dtoList.add(new AdminResponse(admin));
        }

        return dtoList;
    }

    // 관리자 단일 조회
    @Transactional(readOnly = true)
    public AdminResponse findById(Long userId) {
        Admin admin = getAdminById(userId);
        return new AdminResponse(admin);
    }

    // 관리자 비밀번호 수정
    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        Admin admin = getAdminById(userId);

        // 1. 본인의 비밀번호만 수정 가능.

        // 2. oldPassword가 기존의 비밀번호와 다른 경우(예외 발생)

        // 3. oldPassword가 newPassword와 같은 경우(예외 발생)

        // 4. 최종적으로 비밀번호 변경
        admin.updatePassword(request.getNewPassword());
    }


    // id로 관리자 찾는 메서드
    private Admin getAdminById(Long userId) {
        return repository.findById(userId).orElseThrow(
            () -> new ApiException(ErrorCode.ADMIN_NOT_FOUND_ERROR)
        );
    }


}
