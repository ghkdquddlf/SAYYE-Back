package com.sayye.classes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 클래스 생성 요청 DTO
@Getter
@Setter
@NoArgsConstructor
public class ClassCreateRequest {

    @NotBlank(message = "클래스 이름은 필수입니다.")
    private String className;

    @NotNull(message = "시작일은 필수입니다.")
    private LocalDate startDate;

    @NotNull(message = "종료일은 필수입니다.")
    private LocalDate endDate;
}