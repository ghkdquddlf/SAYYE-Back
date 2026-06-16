package com.sayye.domain.admin.repository;

import com.sayye.domain.admin.entity.Admin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByUserId(String userId);

    boolean existsByEmail(String email);

    boolean existsByName(String name);

}
