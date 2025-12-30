package com.sayye.notice.controller;

import com.sayye.notice.dto.request.NoticeReqDto;
import com.sayye.notice.dto.response.NoticeResDto;
import com.sayye.notice.service.NoticeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<NoticeResDto> createNotice(
        @Valid @RequestBody NoticeReqDto reqDto,
        Authentication authentication
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(noticeService.createNotice(reqDto,authentication.getName()));
    }

    @GetMapping("{noticeId}")
    public ResponseEntity<NoticeResDto> getNotice(
        @PathVariable Long noticeId
    ){
        return ResponseEntity.ok(noticeService.getNotice(noticeId));
    }

    @GetMapping
    public ResponseEntity<List<NoticeResDto>> getNotices(){

        return  ResponseEntity.ok(noticeService.getNotices());
    }

    @PutMapping("{noticeId}")
    public ResponseEntity<NoticeResDto> updateNotice(
        @PathVariable Long noticeId,
        @Valid @RequestBody NoticeReqDto reqDto
    ){
        return ResponseEntity.ok(noticeService.updateNotice(noticeId,reqDto));
    }

}
