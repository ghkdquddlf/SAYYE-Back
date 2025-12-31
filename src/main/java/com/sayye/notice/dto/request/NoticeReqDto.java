package com.sayye.notice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NoticeReqDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private Boolean status;
}
