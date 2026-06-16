package com.sayye.domain.notice.repository;

import com.sayye.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

}
