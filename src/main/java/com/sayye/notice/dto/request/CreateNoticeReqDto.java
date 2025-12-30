package com.sayye.notice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class CreateNoticeReqDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
