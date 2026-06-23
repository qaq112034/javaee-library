package com.library.module.notice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.module.notice.entity.Notice;

public interface NoticeService {

    Page<Notice> pageNotices(int current, int size, Integer noticeType);

    Notice getNoticeById(Long id);

    void addNotice(Notice notice);

    void updateNotice(Notice notice);

    void deleteNotice(Long id);

    void publishNotice(Long id);
}
