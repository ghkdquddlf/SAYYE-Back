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
    private boolean status;//현재 공지 게시 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admins_id", nullable = false)
    private Admin admin;

    @Builder
    public Notice(Admin admin,String title, String content, boolean status){
        this.admin = admin;
        this.title = title;
        this.content = content;
        this.status = status;
    }


    public static Notice of(NoticeReqDto request, Admin admin) {
        return Notice.builder()
                   .title(request.getTitle())
                   .content(request.getContent())
                   .admin(admin)
                   .status(request.isStatus())
                   .build();
    }

    public void update(NoticeReqDto reqDto){
        this.title = reqDto.getTitle();
        this.content = reqDto.getContent();
    }
}
