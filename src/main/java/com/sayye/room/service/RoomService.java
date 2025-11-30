package com.sayye.room.service;


import com.sayye.room.dto.RoomReqDto;
import com.sayye.room.dto.RoomResDto;
import com.sayye.room.entity.Room;
import com.sayye.room.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomResDto createRoom(RoomReqDto roomReqDto) {

        Room room   = new Room(roomReqDto.getRoomName(),roomReqDto.getLocation(),
            roomReqDto.getCapacity(), roomReqDto.getDescription()) ;


        roomRepository.save(room);


        return room;
    }
}
