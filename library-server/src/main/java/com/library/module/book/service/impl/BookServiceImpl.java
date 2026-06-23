package com.library.module.book.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.exception.BusinessException;
import com.library.module.book.entity.Book;
import com.library.module.book.mapper.BookMapper;
import com.library.module.book.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 图书管理服务实现
 * <p>
 * 缓存策略：
 * - 热门图书列表：使用 Redis 缓存，提升首页加载速度。
 *   每次图书借阅/归还后清除缓存，下次查询时重新加载。
 * - 使用 @Cacheable 注解声明式缓存（Spring Cache 抽象）。
 *   底层由 Redis 实现（见 RedisConfig）。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /** 热门图书缓存的 Redis Key */
    private static final String HOT_BOOKS_KEY = "library:hot_books";
    /** 热门图书缓存过期时间（小时） */
    private static final long HOT_BOOKS_TTL = 2;

    @Override
    public IPage<Book> pageBooks(int current, int size, String keyword, Long categoryId, Integer status) {
        Page<Book> page = new Page<>(current, size);
        return bookMapper.selectBookPage(page, keyword, categoryId, status);
    }

    @Override
    public Book getBookById(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        return book;
    }

    @Override
    @Transactional
    public void addBook(Book book) {
        // 初始可借数量 = 馆藏总数
        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(book.getTotalCopies());
        }
        // 默认上架
        if (book.getStatus() == null) {
            book.setStatus(1);
        }
        bookMapper.insert(book);
        log.info("新增图书: {}", book.getTitle());
    }

    @Override
    @Transactional
    @CacheEvict(value = "hotBooks", key = "'top10'")  // 清除热门图书缓存
    public void updateBook(Book book) {
        Book existing = bookMapper.selectById(book.getId());
        if (existing == null) {
            throw new BusinessException("图书不存在");
        }
        bookMapper.updateById(book);
        // 清除 Redis 中的热门图书缓存
        redisTemplate.delete(HOT_BOOKS_KEY);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        if (book.getAvailableCopies() < book.getTotalCopies()) {
            throw new BusinessException("该图书还有未归还的借阅记录，无法删除");
        }
        bookMapper.deleteById(id);  // 逻辑删除
        log.info("删除图书: {}", book.getTitle());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        book.setStatus(status);
        bookMapper.updateById(book);
    }

    @Override
    @Cacheable(value = "hotBooks", key = "'top10'")  // 结果存入 Redis 缓存
    public List<Book> getHotBooks(int limit) {
        log.debug("从数据库查询热门图书 (limit={})", limit);
        return bookMapper.selectHotBooks(limit);
    }
}
