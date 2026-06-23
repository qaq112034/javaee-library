package com.library.module.book.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.module.book.entity.Book;
import com.library.module.book.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图书管理控制器
 */
@Tag(name = "图书管理", description = "图书的增删改查、上下架、热门图书")
@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "分页查询图书列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('book:list')")
    public Result<PageResult<Book>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        IPage<Book> page = bookService.pageBooks(current, size, keyword, categoryId, status);
        return Result.ok(PageResult.from(page));
    }

    @Operation(summary = "根据ID查询图书详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('book:list')")
    public Result<Book> getById(@PathVariable Long id) {
        return Result.ok(bookService.getBookById(id));
    }

    @Operation(summary = "新增图书")
    @PostMapping
    @PreAuthorize("hasAuthority('book:add')")
    public Result<Void> add(@RequestBody Book book) {
        bookService.addBook(book);
        return Result.okMsg("图书添加成功");
    }

    @Operation(summary = "更新图书信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('book:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        bookService.updateBook(book);
        return Result.okMsg("图书更新成功");
    }

    @Operation(summary = "删除图书")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('book:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        bookService.deleteBook(id);
        return Result.okMsg("图书删除成功");
    }

    @Operation(summary = "更新图书状态（上架/下架）")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('book:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) {
        bookService.updateStatus(id, body.get("status"));
        return Result.okMsg("状态更新成功");
    }

    @Operation(summary = "查询热门图书 Top N")
    @GetMapping("/hot")
    @PreAuthorize("hasAuthority('book:list')")
    public Result<List<Book>> hotBooks(@RequestParam(defaultValue = "10") int limit) {
        return Result.ok(bookService.getHotBooks(limit));
    }
}
