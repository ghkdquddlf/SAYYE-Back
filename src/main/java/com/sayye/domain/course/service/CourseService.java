package com.sayye.domain.course.service;

import com.sayye.domain.course.dto.request.CourseCreateRequest;
import com.sayye.domain.course.dto.request.CourseUpdateRequest;
import com.sayye.domain.course.dto.response.CourseResponse;
import com.sayye.domain.course.entity.Course;
import com.sayye.domain.course.repository.CourseRepository;
import com.sayye.global.exception.ApiException;
import com.sayye.global.exception.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 읽기 전용
public class CourseService {

    private final CourseRepository courseRepository;

    // 코스 생성
    @Transactional
    public CourseResponse createCourse(CourseCreateRequest request) {
        // 코스명 중복 체크
        if (courseRepository.existsByCourseName(request.getCourseName())) {
            throw new ApiException(ErrorCode.COURSE_NAME_DUPLICATED);
        }

        // DTO -> Entity 변환
        Course course = Course.builder()
            .courseName(request.getCourseName())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .build();

        // DB 저장
        Course savedCourse = courseRepository.save(course);

        // Entity -> Response DTO 변환 후 반환
        return CourseResponse.from(savedCourse);
    }

    // 코스 전체 목록 조회
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
            .map(CourseResponse::from) // Entity -> DTO 변환
            .collect(Collectors.toList());
    }

    // 코스 상세 조회
    public CourseResponse getCourseById(Long courseId) {
        // ID로 조회, 없으면 예외 발생
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ApiException(ErrorCode.COURSE_NOT_FOUND));

        return CourseResponse.from(course);
    }

    // 코스 수정
    @Transactional
    public CourseResponse updateCourse(Long courseId, CourseUpdateRequest request) {
        // 존재하는 코스인지 확인
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ApiException(ErrorCode.COURSE_NOT_FOUND));

        // 코스명 중복 체크 (본인 제외)
        if (courseRepository.existsByCourseNameAndIdNot(request.getCourseName(), courseId)) {
            throw new ApiException(ErrorCode.COURSE_NAME_DUPLICATED);
        }

        // 정보 수정 (더티 체킹으로 자동 업데이트)
        course.update(
            request.getCourseName(),
            request.getStartDate(),
            request.getEndDate()
        );

        return CourseResponse.from(course);
    }

    // 코스 삭제
    @Transactional
    public void deleteCourse(Long courseId) {
        // 존재하는 코스인지 확인
        if (!courseRepository.existsById(courseId)) {
            throw new ApiException(ErrorCode.COURSE_NOT_FOUND);
        }

        // 삭제
        courseRepository.deleteById(courseId);
    }
}
