package com.sayye.notice.service;

import com.sayye.admin.entity.Admin;
import com.sayye.admin.repository.AdminRepository;
import com.sayye.exception.ApiException;
import com.sayye.exception.ErrorCode;
import com.sayye.notice.dto.request.NoticeReqDto;
import com.sayye.notice.dto.response.NoticeResDto;
import com.sayye.notice.entity.Notice;
import com.sayye.notice.repository.NoticeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public NoticeResDto createNotice(
        NoticeReqDto reqDto,
        String adminName
        ) {

       Admin admin = adminRepository.findByUserId(adminName).orElseThrow(
            ()-> new ApiException(ErrorCode.ADMIN_NOT_FOUND_ERROR));

       // 만약 마스터, 어드민 뿐만 아니라 매니저,상담사 Role이 추가될경우 검증하는 로직 필요
       Notice save = noticeRepository.save(Notice.of(reqDto,admin));

       return NoticeResDto.from(save);
    }


    public NoticeResDto getNotice(Long noticeId) {

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
            () -> new ApiException(ErrorCode.NOTICE_NOT_FOUND)
        );

        return NoticeResDto.from(notice);
    }

    public List<NoticeResDto> getNotices() {
        List<Notice> noticeList = noticeRepository.findAll();

        return noticeList.stream()
            .map(NoticeResDto::from)
            .collect(Collectors.toList());

    }

    @Transactional
    public NoticeResDto updateNotice(Long noticeId, NoticeReqDto reqDto) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
            ()-> new ApiException(ErrorCode.NOTICE_NOT_FOUND)
        );

        notice.update(reqDto);

        return NoticeResDto.from(notice);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
            ()-> new ApiException(ErrorCode.NOTICE_NOT_FOUND)
        );

        noticeRepository.delete(notice);
    }
}
