package com.sayye.notice.entity;

import com.sayye.admin.entity.Admin;
import com.sayye.baseEntity.BaseEntity;
import com.sayye.notice.dto.request.NoticeReqDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "notices")
@NoArgsConstructor
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean status;//현재 공지 게시 여부

    @Column(nullable = false)
    private Boolean pinned; // 상단 고정 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admins_id", nullable = false)
    private Admin admin;

    @Builder
    public Notice(Admin admin,String title, String content, Boolean status, Boolean pinned){
        this.admin = admin;
        this.title = title;
        this.content = content;
        this.status = status;
        this.pinned = pinned;
    }


    public static Notice of(NoticeReqDto request, Admin admin) {
        return Notice.builder()
                   .title(request.getTitle())
                   .content(request.getContent())
                   .admin(admin)
                   .status(request.getStatus())
                   .pinned(request.getPinned())
                   .build();
    }

    public void update(NoticeReqDto reqDto){
        this.title = reqDto.getTitle();
        this.content = reqDto.getContent();
    }

    public void toggle() {
        this.status = !this.status;

        // 상단 고정이 true일 때 status를 false로 바꾼다면 pinned도 false가 되어야 함
        if (!this.status) {
            this.pinned = false;
        }
    }

    public void togglePinned() {
        // 숨김 처리된 공지사항을 고정한다면 status도 함께 true로 변경
        if (!this.status) {
            this.status = true;
            this.pinned = true;
        } else {
            // 게시 중인 상태라면 고정 여부만 반전
            this.pinned = !this.pinned;
        }
    }
}
