package com.sayye.room.dto.response;

import com.sayye.room.entity.Room;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomResDto {
    private Long id;
    private String roomName;
    private int location;
    private int capacity;
    private String description;


    public static RoomResDto from(Room room){
        return RoomResDto.builder()
                   .id(room.getId())
                   .roomName(room.getRoomName())
                   .location(room.getLocation())
                   .capacity(room.getCapacity())
                   .description(room.getDescription())
                   .build();
    }
}
