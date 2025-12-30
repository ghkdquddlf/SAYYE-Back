package com.sayye.notice.controller;

import com.sayye.notice.dto.request.CreateNoticeReqDto;
import com.sayye.notice.dto.response.NoticeResDto;
import com.sayye.notice.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
        @Valid @RequestBody CreateNoticeReqDto reqDto,
        Authentication authentication
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(noticeService.create(reqDto,authentication.getName()));
    }

    @GetMapping("{noticeId}")
    public ResponseEntity<NoticeResDto> getNotice(
        @PathVariable Long noticeId
    ){
        return ResponseEntity.ok(noticeService.getNotice(noticeId));
    }

}
