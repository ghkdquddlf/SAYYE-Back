package com.sayye.room.service;


import com.sayye.exception.ApiException;
import com.sayye.exception.ErrorCode;
import com.sayye.room.dto.request.RoomReqDto;
import com.sayye.room.dto.response.RoomResDto;
import com.sayye.room.entity.Room;
import com.sayye.room.repository.RoomRepository;
import jakarta.validation.Valid;
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

    public RoomResDto createRoom(RoomReqDto roomReqDto) {

        if(roomRepository.existsByRoomName(roomReqDto.getRoomName())){
            throw new ApiException(ErrorCode.ROOM_NAME_DUPLICATED);
        }
        Room room   = new Room(roomReqDto.getRoomName(), roomReqDto.getLocation(),
            roomReqDto.getCapacity(), roomReqDto.getDescription()) ;


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

    public RoomResDto updateRoom(Long roomId, RoomReqDto roomReqDto) {



        // 회의실 존재 여부 확인
        Room room = roomRepository.findById(roomId).orElseThrow(
            ()-> new ApiException(ErrorCode.ROOM_NOT_FOUND));

        // 중복된 회의실 이름인지 확인
        // 이름의 변경이 감지되었을 때만 if문이 실행되도록 로직 구성
        if(!room.getRoomName().equals(roomReqDto.getRoomName()) && roomRepository.existsByRoomName(roomReqDto.getRoomName())){
            throw new ApiException(ErrorCode.ROOM_NAME_DUPLICATED);
        }

        // JPA가 Dirty Checking 을 통해 객체의 변경을 감지하기 때문에 별도로 Repository에 반영해줄 필요가 없음
        room.update(roomReqDto);


        return RoomResDto.from(room);
    }
}
