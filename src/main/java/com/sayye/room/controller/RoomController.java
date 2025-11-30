package com.sayye.room.controller;


import com.sayye.room.dto.RoomReqDto;
import com.sayye.room.dto.RoomResDto;
import com.sayye.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResDto> createRoom(
        @RequestBody RoomReqDto roomReqDto
    ){

        return ResponseEntity.status(HttpStatus.OK).body(roomService.createRoom(roomReqDto));
    }


}
