package com.library.module.borrow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.module.borrow.entity.BorrowRecord;

import java.util.List;
import java.util.Map;

/**
 * 借阅管理服务接口
 */
public interface BorrowService {

    /** 分页查询借阅记录 */
    IPage<BorrowRecord> pageRecords(int current, int size, Integer status, String keyword);

    /** 借书 */
    void borrowBook(Long readerId, Long bookId);

    /** 还书 */
    void returnBook(Long recordId);

    /** 续借 */
    void renewBook(Long recordId);

    /** 查询读者借阅历史 */
    List<BorrowRecord> getReaderHistory(Long readerId);

    /** 查询逾期记录 */
    List<BorrowRecord> getOverdueRecords();

    /** 定时任务：更新逾期状态 */
    void updateOverdueStatus();

    /** 借阅趋势统计（最近N天） */
    List<Map<String, Object>> getBorrowTrend(int days);

    /** 分类借阅统计 */
    List<Map<String, Object>> getCategoryStats();
}
