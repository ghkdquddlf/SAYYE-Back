package com.sayye.domain.course.controller;

import com.sayye.domain.course.dto.request.CourseCreateRequest;
import com.sayye.domain.course.dto.request.CourseUpdateRequest;
import com.sayye.domain.course.dto.response.CourseResponse;
import com.sayye.domain.course.service.CourseService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // 1. 코스 생성 (POST /classes)
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
        @Valid @RequestBody CourseCreateRequest request) {

        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. 코스 전체 목록 조회 (GET /classes)
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        List<CourseResponse> responses = courseService.getAllCourses();
        return ResponseEntity.ok(responses);
    }

    // 3. 코스 상세 조회 (GET /classes/{courseId})
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(
        @PathVariable Long courseId) {

        CourseResponse response = courseService.getCourseById(courseId);
        return ResponseEntity.ok(response);
    }

    // 4. 코스 수정 (PUT /classes/{courseId})
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(
        @PathVariable Long courseId,
        @Valid @RequestBody CourseUpdateRequest request) {

        CourseResponse response = courseService.updateCourse(courseId, request);
        return ResponseEntity.ok(response);
    }

    // 5. 코스 삭제 (DELETE /classes/{courseId})
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }
}
