package com.library.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.module.book.mapper.BookMapper;
import com.library.module.borrow.mapper.BorrowRecordMapper;
import com.library.module.reader.mapper.ReaderMapper;
import com.library.module.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计服务实现
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final BookMapper bookMapper;
    private final ReaderMapper readerMapper;
    private final BorrowRecordMapper borrowRecordMapper;

    @Override
    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        // 图书总数
        long totalBooks = bookMapper.selectCount(
                new LambdaQueryWrapper<com.library.module.book.entity.Book>()
                        .eq(com.library.module.book.entity.Book::getStatus, 1));

        // 读者总数
        long totalReaders = readerMapper.selectCount(null);

        // 借阅中数量
        long borrowingCount = borrowRecordMapper.selectCount(
                new LambdaQueryWrapper<com.library.module.borrow.entity.BorrowRecord>()
                        .eq(com.library.module.borrow.entity.BorrowRecord::getStatus, 1));

        // 逾期数量
        long overdueCount = borrowRecordMapper.selectCount(
                new LambdaQueryWrapper<com.library.module.borrow.entity.BorrowRecord>()
                        .eq(com.library.module.borrow.entity.BorrowRecord::getStatus, 3));

        dashboard.put("totalBooks", totalBooks);
        dashboard.put("totalReaders", totalReaders);
        dashboard.put("borrowingCount", borrowingCount);
        dashboard.put("overdueCount", overdueCount);

        return dashboard;
    }

    @Override
    public Object getBorrowTrend(int days) {
        List<Map<String, Object>> trend = borrowRecordMapper.countByDay(days);
        return trend;
    }

    @Override
    public Object getCategoryStats() {
        List<Map<String, Object>> stats = borrowRecordMapper.countByCategory();
        return stats;
    }
}
