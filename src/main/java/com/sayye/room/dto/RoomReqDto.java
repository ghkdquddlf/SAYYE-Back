package com.sayye.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RoomReqDto {
        private String roomName;
        private int location;
        private int capacity;
        private String description;
}
