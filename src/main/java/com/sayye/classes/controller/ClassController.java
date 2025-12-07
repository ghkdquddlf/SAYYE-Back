package com.sayye.classes.controller;

import com.sayye.classes.dto.ClassCreateRequest;
import com.sayye.classes.dto.ClassResponse;
import com.sayye.classes.service.ClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
}
