package com.sayye.domain.course.repository;

import com.sayye.domain.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    // JpaRepository가 기본 CRUD 메서드 제공

    // 코스명 중복 체크용
    boolean existsByCourseName(String courseName);

    // 수정 시 본인 제외하고 중복 체크용
    boolean existsByCourseNameAndIdNot(String courseName, Long id);
}
