package com.sayye.classes.repository;


import com.sayye.classes.entity.Class;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRepository extends JpaRepository<Class, Long> {
    // JpaRepository가 기본 CRUD 메서드 제공
    // - save(): 생성/수정
    // - findById(): ID로 조회
    // - findAll(): 전체 조회
    // - deleteById(): 삭제
}
