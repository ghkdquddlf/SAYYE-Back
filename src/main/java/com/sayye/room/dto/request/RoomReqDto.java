package com.sayye.room.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RoomReqDto {


        @NotBlank
        private String roomName;

        private int location;

        @Min(1)
        private int capacity;

        private String description;
}
