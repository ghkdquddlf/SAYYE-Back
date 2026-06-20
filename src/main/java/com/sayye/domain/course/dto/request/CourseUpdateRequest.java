package com.sayye.domain.course.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 코스 수정 요청 DTO
@Getter
@Setter
@NoArgsConstructor
public class CourseUpdateRequest {

    @NotBlank(message = "코스 이름은 필수입니다.")
    private String courseName;

    @NotNull(message = "시작일은 필수입니다.")
    private LocalDate startDate;

    @NotNull(message = "종료일은 필수입니다.")
    private LocalDate endDate;
}
