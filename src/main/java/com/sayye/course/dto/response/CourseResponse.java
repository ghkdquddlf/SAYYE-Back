package com.sayye.course.dto.response;

import com.sayye.course.entity.Course;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

// 코스 응답 DTO
@Getter
@Builder
public class CourseResponse {

    private Long id;
    private String courseName;
    private LocalDate startDate;
    private LocalDate endDate;

    // Entity -> Response DTO 변환
    public static CourseResponse from(Course course) {
        return CourseResponse.builder()
            .id(course.getId())
            .courseName(course.getCourseName())
            .startDate(course.getStartDate())
            .endDate(course.getEndDate())
            .build();
    }
}
