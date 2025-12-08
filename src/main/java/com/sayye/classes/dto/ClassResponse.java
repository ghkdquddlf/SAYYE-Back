package com.sayye.classes.dto;

import com.sayye.classes.entity.Class;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

// 클래스 응답 DTO
@Getter
@Builder
public class ClassResponse {

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    // Entity -> Response DTO 변환
    public static ClassResponse from(Class classEntity) {
        return ClassResponse.builder()
            .id(classEntity.getId())
            .name(classEntity.getClassName())
            .startDate(classEntity.getStartDate())
            .endDate(classEntity.getEndDate())
            .build();
    }
}
