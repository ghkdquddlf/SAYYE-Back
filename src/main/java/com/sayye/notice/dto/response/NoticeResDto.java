package com.sayye.notice.dto.response;

import com.sayye.notice.entity.Notice;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeResDto {

    private Long id;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static NoticeResDto from(Notice notice){
        return NoticeResDto.builder()
                   .id(notice.getId())
                   .title(notice.getTitle())
                   .content(notice.getContent())
                   .createdAt(notice.getCreatedAt())
                   .updatedAt(notice.getUpdatedAt())
                   .build();
    }
}
