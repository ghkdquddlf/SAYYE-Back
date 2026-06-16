package com.sayye.domain.room.dto.response;

import com.sayye.domain.room.entity.Room;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomResDto {

    private Long id;
    private String roomName;
    private Integer location;
    private Integer capacity;
    private String description;


    public static RoomResDto from(Room room) {
        return RoomResDto.builder()
                   .id(room.getId())
                   .roomName(room.getRoomName())
                   .location(room.getLocation())
                   .capacity(room.getCapacity())
                   .description(room.getDescription())
                   .build();
    }
}
