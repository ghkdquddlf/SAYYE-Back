package com.sayye.classes.controller;

import com.sayye.classes.dto.ClassCreateRequest;
import com.sayye.classes.dto.ClassResponse;
import com.sayye.classes.dto.ClassUpdateRequest;
import com.sayye.classes.service.ClassService;
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
public class ClassController {

    private final ClassService classService;

    // 1. 클래스 생성 (POST /classes)
    @PostMapping
    public ResponseEntity<ClassResponse> createClass(
        @Valid @RequestBody ClassCreateRequest request) {

        ClassResponse response = classService.createClass(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. 클래스 전체 목록 조회 (GET /classes)
    @GetMapping
    public ResponseEntity<List<ClassResponse>> getAllClasses() {
        List<ClassResponse> responses = classService.getAllClasses();
        return ResponseEntity.ok(responses);
    }

    // 3. 클래스 상세 조회 (GET /classes/{classId})
    @GetMapping("/{classId}")
    public ResponseEntity<ClassResponse> getClassById(
        @PathVariable Long classId) {

        ClassResponse response = classService.getClassById(classId);
        return ResponseEntity.ok(response);
    }

    // 4. 클래스 수정 (PUT /classes/{classId})
    @PutMapping("/{classId}")
    public ResponseEntity<ClassResponse> updateClass(
        @PathVariable Long classId,
        @Valid @RequestBody ClassUpdateRequest request) {

        ClassResponse response = classService.updateClass(classId, request);
        return ResponseEntity.ok(response);
    }

    // 5. 클래스 삭제 (DELETE /classes/{classId})
    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long classId) {
        classService.deleteClass(classId);
        return ResponseEntity.noContent().build();
    }
}