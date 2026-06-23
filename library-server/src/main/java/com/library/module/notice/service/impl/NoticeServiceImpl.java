package com.library.module.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.exception.BusinessException;
import com.library.module.notice.entity.Notice;
import com.library.module.notice.mapper.NoticeMapper;
import com.library.module.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    @Override
    public Page<Notice> pageNotices(int current, int size, Integer noticeType) {
        Page<Notice> page = new Page<>(current, size);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        if (noticeType != null) {
            wrapper.eq(Notice::getNoticeType, noticeType);
        }
        wrapper.orderByDesc(Notice::getCreateTime);
        return noticeMapper.selectPage(page, wrapper);
    }

    @Override
    public Notice getNoticeById(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        return notice;
    }

    @Override
    @Transactional
    public void addNotice(Notice notice) {
        if (notice.getStatus() == null) notice.setStatus(0); // 默认草稿
        noticeMapper.insert(notice);
        log.info("新增公告: {}", notice.getTitle());
    }

    @Override
    @Transactional
    public void updateNotice(Notice notice) {
        noticeMapper.updateById(notice);
    }

    @Override
    @Transactional
    public void deleteNotice(Long id) {
        noticeMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void publishNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在");
        }
        notice.setStatus(1);
        notice.setUpdateTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
    }
}
