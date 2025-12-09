package com.sayye.admin.entity;

import com.sayye.admin.Role;
import com.sayye.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "admins")
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 접근 제어 PROTECTED 레벨 유지
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK (auto-generated)

    @Column(nullable = false, unique = true)
    private String userId; // 관리자 계정 ID (로그인용)

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    // 정적 팩토리 메서드로 관리자 생성
    public static Admin of(String userId, String encodedPassword, String name, String email, Role role) {
        Admin admin = new Admin();
        admin.userId = userId;
        admin.password = encodedPassword;
        admin.name = name;
        admin.email = email;
        admin.role = role;
        return admin;
    }

    // 관리자 비밀번호 수정 메서드
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    // 다른 관리자에 대한 접근 권한 확인
    public boolean canAccessAdmin(String targetUserId) {

        // MASTER는 모든 관리자 접근 가능
        if (this.role == Role.MASTER) {
            return true;
        }

        // ADMIN은 본인만 접근 가능
        return this.userId.equals(targetUserId);
    }

}
