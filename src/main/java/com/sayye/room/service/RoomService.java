package com.sayye.room.service;


import com.sayye.exception.ApiException;
import com.sayye.exception.ErrorCode;
import com.sayye.room.dto.request.RoomCreateReqDto;
import com.sayye.room.dto.response.RoomResDto;
import com.sayye.room.entity.Room;
import com.sayye.room.repository.RoomRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomResDto createRoom(RoomCreateReqDto roomCreateReqDto) {

        if(roomRepository.findByRoomName(roomCreateReqDto.getRoomName())){
            throw new ApiException(ErrorCode.ROOM_NAME_DUPLICATED);
        }
        Room room   = new Room(roomCreateReqDto.getRoomName(), roomCreateReqDto.getLocation(),
            roomCreateReqDto.getCapacity(), roomCreateReqDto.getDescription()) ;


        Room saved = roomRepository.save(room);


        return RoomResDto.from(saved);
    }

    @Transactional(readOnly = true)
    public RoomResDto getRoomById(Long roomId) {

        Room room = roomRepository.findById(roomId).orElseThrow(
            ()-> new ApiException(ErrorCode.ROOM_NOT_FOUND)
        );

        return RoomResDto.from(room);
    }

    @Transactional(readOnly = true)
    public List<RoomResDto> getAllRooms() {

        List<Room> rooms = roomRepository.findAll();

        return rooms.stream()
                   .map(RoomResDto::from)
                   .collect(Collectors.toList());

    }

    public void deleteRoom(Long roomId) {

        Room room = roomRepository.findById(roomId)
            .orElseThrow(()-> new ApiException(ErrorCode.ROOM_NOT_FOUND));

        roomRepository.delete(room);
    }
}
