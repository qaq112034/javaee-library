package com.library.module.borrow.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.exception.BusinessException;
import com.library.module.book.entity.Book;
import com.library.module.book.mapper.BookMapper;
import com.library.module.borrow.entity.BorrowRecord;
import com.library.module.borrow.mapper.BorrowRecordMapper;
import com.library.module.borrow.service.BorrowService;
import com.library.module.reader.entity.Reader;
import com.library.module.reader.mapper.ReaderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 借阅管理服务实现
 * <p>
 * 核心业务流程：
 * <pre>
 * 【借书】
 *   1. 校验读者状态（是否被停用）
 *   2. 校验读者借阅数量（是否已达上限）
 *   3. 校验读者是否有逾期未还的书
 *   4. 校验图书状态（是否上架、是否有库存）
 *   5. 创建借阅记录
 *   6. 图书可借数量 -1
 *   7. 清除热门图书缓存
 *
 * 【还书】
 *   1. 校验记录状态（必须是"借阅中"或"已逾期"）
 *   2. 更新记录状态为"已归还"
 *   3. 记录归还日期
 *   4. 图书可借数量 +1
 *
 * 【续借】
 *   1. 校验记录状态（只能续借"借阅中"的）
 *   2. 校验续借次数（最多2次）
 *   3. 应还日期延长（默认 +30天）
 *   4. 续借次数 +1
 * </pre>
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordMapper borrowRecordMapper;
    private final BookMapper bookMapper;
    private final ReaderMapper readerMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public IPage<BorrowRecord> pageRecords(int current, int size, Integer status, String keyword) {
        Page<BorrowRecord> page = new Page<>(current, size);
        return borrowRecordMapper.selectBorrowPage(page, status, keyword);
    }

    @Override
    @Transactional
    public void borrowBook(Long readerId, Long bookId) {
        // 1. 校验读者
        Reader reader = readerMapper.selectById(readerId);
        if (reader == null) {
            throw new BusinessException("读者不存在");
        }
        if (reader.getStatus() == 0) {
            throw new BusinessException("该读者已被停用，无法借阅");
        }

        // 2. 校验读者当前借阅数量
        int currentBorrows = borrowRecordMapper.countCurrentBorrows(readerId);
        if (currentBorrows >= reader.getMaxBorrowCount()) {
            throw new BusinessException("该读者已达到最大借阅数量（" + reader.getMaxBorrowCount() + "本）");
        }

        // 3. 校验是否有逾期未还
        List<BorrowRecord> overdueRecords = borrowRecordMapper.selectOverdueRecords();
        boolean hasOverdue = overdueRecords.stream()
                .anyMatch(r -> r.getReaderId().equals(readerId));
        if (hasOverdue) {
            throw new BusinessException("该读者有逾期未还的图书，请先归还后再借阅");
        }

        // 4. 校验图书
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        if (book.getStatus() == 0) {
            throw new BusinessException("该图书已下架，无法借阅");
        }
        if (book.getAvailableCopies() <= 0) {
            throw new BusinessException("该图书已全部借出，暂无库存");
        }

        // 5. 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setReaderId(readerId);
        record.setBookId(bookId);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(reader.getMaxBorrowDays()));
        record.setStatus(1);  // 借阅中
        record.setRenewCount(0);
        borrowRecordMapper.insert(record);

        // 6. 图书可借数量 -1，借阅次数 +1
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        book.setBorrowCount((book.getBorrowCount() == null ? 0 : book.getBorrowCount()) + 1);
        bookMapper.updateById(book);

        // 7. 清除热门图书缓存（借阅数据变了）
        redisTemplate.delete("library:hot_books");

        log.info("借书成功: reader={}, book={}, dueDate={}",
                reader.getRealName(), book.getTitle(), record.getDueDate());
    }

    @Override
    @Transactional
    public void returnBook(Long recordId) {
        // 1. 查询借阅记录
        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("借阅记录不存在");
        }
        if (record.getStatus() == 2) {
            throw new BusinessException("该图书记已归还");
        }

        // 2. 更新记录
        record.setStatus(2);  // 已归还
        record.setReturnDate(LocalDateTime.now());
        borrowRecordMapper.updateById(record);

        // 3. 图书可借数量 +1
        Book book = bookMapper.selectById(record.getBookId());
        if (book != null) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookMapper.updateById(book);
        }

        log.info("还书成功: recordId={}, bookId={}", recordId, record.getBookId());
    }

    @Override
    @Transactional
    public void renewBook(Long recordId) {
        // 1. 查询记录
        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("借阅记录不存在");
        }
        if (record.getStatus() != 1) {
            throw new BusinessException("只有借阅中的图书才能续借");
        }
        if (record.getRenewCount() >= 2) {
            throw new BusinessException("续借次数已达上限（2次）");
        }

        // 2. 延长应还日期
        Reader reader = readerMapper.selectById(record.getReaderId());
        int extendDays = reader != null ? reader.getMaxBorrowDays() : 30;
        record.setDueDate(record.getDueDate().plusDays(extendDays));
        record.setRenewCount(record.getRenewCount() + 1);
        borrowRecordMapper.updateById(record);

        log.info("续借成功: recordId={}, 新应还日期={}", recordId, record.getDueDate());
    }

    @Override
    public List<BorrowRecord> getReaderHistory(Long readerId) {
        return borrowRecordMapper.selectByReaderId(readerId);
    }

    @Override
    public List<BorrowRecord> getOverdueRecords() {
        return borrowRecordMapper.selectOverdueRecords();
    }

    /**
     * 定时任务：每小时检查一次，将已过期但未还的记录状态更新为"已逾期"
     * <p>
     * cron 表达式：0 0 * * * ? = 每小时整点执行
     * </p>
     */
    @Override
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void updateOverdueStatus() {
        List<BorrowRecord> overdueRecords = borrowRecordMapper.selectOverdueRecords();
        for (BorrowRecord record : overdueRecords) {
            if (record.getStatus() == 1) {  // 借阅中 → 已逾期
                record.setStatus(3);
                borrowRecordMapper.updateById(record);
            }
        }
        if (!overdueRecords.isEmpty()) {
            log.info("定时任务：更新了 {} 条逾期记录", overdueRecords.size());
        }
    }

    @Override
    public List<Map<String, Object>> getBorrowTrend(int days) {
        return borrowRecordMapper.countByDay(days);
    }

    @Override
    public List<Map<String, Object>> getCategoryStats() {
        return borrowRecordMapper.countByCategory();
    }
}
