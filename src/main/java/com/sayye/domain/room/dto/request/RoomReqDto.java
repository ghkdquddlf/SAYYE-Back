package com.sayye.domain.room.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RoomReqDto {


    @NotBlank
    private String roomName;

    private Integer location;

    @Min(1)
    private Integer capacity;

    private String description;
}
