package com.sayye.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CancelReservationReqDto {

    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, message = "이름은 최소 2글자 이상이어야 합니다.")
    private String userName;

    @NotBlank(message = "휴대폰 뒷자리는 필수입니다.")
    @Pattern(regexp = "^[0-9]{4}$", message = "휴대폰 뒷번호는 4자리 숫자여야 합니다.")
    private String phoneLastNumber;

}
