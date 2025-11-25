package com.sayye.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePasswordRequest {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;

}
