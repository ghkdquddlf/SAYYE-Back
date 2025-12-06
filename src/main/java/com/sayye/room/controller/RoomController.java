package com.sayye.room.controller;


import com.sayye.room.dto.request.RoomCreateReqDto;
import com.sayye.room.dto.response.RoomResDto;
import com.sayye.room.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        @Valid @RequestBody RoomCreateReqDto roomCreateReqDto
    ){

        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(roomCreateReqDto));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResDto> getRoomById(
        @PathVariable Long roomId
    ){

        return ResponseEntity.status(HttpStatus.OK).body(roomService.getRoomById(roomId));
    }


}
