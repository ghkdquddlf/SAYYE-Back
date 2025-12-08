package com.sayye.classes.service;

import com.sayye.classes.dto.ClassUpdateRequest;
import com.sayye.classes.entity.Class;
import com.sayye.classes.dto.ClassCreateRequest;
import com.sayye.classes.dto.ClassResponse;
import com.sayye.classes.repository.ClassRepository;
import com.sayye.exception.ApiException;
import com.sayye.exception.ErrorCode;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 읽기 전용
public class ClassService {

    private final ClassRepository classRepository;

    // 클래스 생성
    @Transactional // 쓰기 작업이므로 readOnly = false
    public ClassResponse createClass(ClassCreateRequest request) {
        // DTO -> Entity 변환
        Class classEntity = Class.builder()
            .className(request.getClassName())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .build();

        // DB 저장
        Class savedClass = classRepository.save(classEntity);

        // Entity -> Response DTO 변환 후 반환
        return ClassResponse.from(savedClass);
    }

    // 클래스 전체 목록 조회
    public List<ClassResponse> getAllClasses() {
        return classRepository.findAll().stream()
            .map(ClassResponse::from) // Entity -> DTO 변환
            .collect(Collectors.toList());
    }

    // 클래스 상세 조회
    public ClassResponse getClassById(Long classId) {
        // ID로 조회, 없으면 예외 발생
        Class classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new ApiException(ErrorCode.CLASS_NOT_FOUND));

        return ClassResponse.from(classEntity);
    }

    // 클래스 수정
    @Transactional
    public ClassResponse updateClass(Long classId, ClassUpdateRequest request) {
        // 존재하는 클래스인지 확인
        Class classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new ApiException(ErrorCode.CLASS_NOT_FOUND));

        // 정보 수정 (더티 체킹으로 자동 업데이트)
        classEntity.update(
            request.getClassName(),
            request.getStartDate(),
            request.getEndDate()
        );

        return ClassResponse.from(classEntity);
    }

    // 클래스 삭제
    @Transactional
    public void deleteClass(Long classId) {
        // 존재하는 클래스인지 확인
        if (!classRepository.existsById(classId)) {
            throw new ApiException(ErrorCode.CLASS_NOT_FOUND);
        }

        // 삭제
        classRepository.deleteById(classId);
    }
}