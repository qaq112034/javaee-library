package com.library.module.book.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.module.book.entity.Book;

import java.util.List;

/**
 * 图书管理服务接口
 */
public interface BookService {

    /** 分页查询图书 */
    IPage<Book> pageBooks(int current, int size, String keyword, Long categoryId, Integer status);

    /** 根据ID查询图书 */
    Book getBookById(Long id);

    /** 新增图书 */
    void addBook(Book book);

    /** 更新图书信息 */
    void updateBook(Book book);

    /** 删除图书（逻辑删除） */
    void deleteBook(Long id);

    /** 上架/下架 */
    void updateStatus(Long id, Integer status);

    /** 查询热门图书（带 Redis 缓存） */
    List<Book> getHotBooks(int limit);
}
