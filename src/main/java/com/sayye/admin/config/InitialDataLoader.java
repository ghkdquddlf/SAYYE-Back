package com.sayye.admin.config;

import com.sayye.admin.Role;
import com.sayye.admin.entity.Admin;
import com.sayye.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
// MASTER 계정이 제대로 생성되지 않는 문제가 발생해서 만든 클래스
// 테스트 용도라서 나중에 수정/삭제 필요
public class InitialDataLoader implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // MASTER 계정이 없으면 생성
        if (adminRepository.findByUserId("master").isEmpty()) {
            Admin master = new Admin();
            master.setUserId("master");
            master.setPassword(passwordEncoder.encode("password123"));
            master.setName("마스터관리자");
            master.setEmail("master@sesac.com");
            master.setRole(Role.MASTER);
            
            adminRepository.save(master);
            log.info("=================================");
            log.info("MASTER 계정이 생성되었습니다!");
            log.info("userId: master");
            log.info("password: password123");
            log.info("=================================");
        } else {
            log.info("MASTER 계정이 이미 존재합니다.");
        }
    }
}

