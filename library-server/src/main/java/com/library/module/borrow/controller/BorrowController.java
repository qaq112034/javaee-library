package com.library.module.borrow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.module.borrow.entity.BorrowRecord;
import com.library.module.borrow.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 借阅管理控制器
 */
@Tag(name = "借阅管理", description = "借书、还书、续借、借阅记录查询")
@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    @Operation(summary = "分页查询借阅记录")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('borrow:list')")
    public Result<PageResult<BorrowRecord>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        IPage<BorrowRecord> page = borrowService.pageRecords(current, size, status, keyword);
        return Result.ok(PageResult.from(page));
    }

    @Operation(summary = "借书")
    @PostMapping
    @PreAuthorize("hasAuthority('borrow:add')")
    public Result<Void> borrow(@RequestBody Map<String, Long> body) {
        Long readerId = body.get("readerId");
        Long bookId = body.get("bookId");
        if (readerId == null || bookId == null) {
            return Result.fail("读者ID和图书ID不能为空");
        }
        borrowService.borrowBook(readerId, bookId);
        return Result.okMsg("借书成功");
    }

    @Operation(summary = "还书")
    @PutMapping("/{id}/return")
    @PreAuthorize("hasAuthority('borrow:return')")
    public Result<Void> returnBook(@PathVariable Long id) {
        borrowService.returnBook(id);
        return Result.okMsg("还书成功");
    }

    @Operation(summary = "续借")
    @PutMapping("/{id}/renew")
    @PreAuthorize("hasAuthority('borrow:renew')")
    public Result<Void> renew(@PathVariable Long id) {
        borrowService.renewBook(id);
        return Result.okMsg("续借成功");
    }

    @Operation(summary = "查询读者借阅历史")
    @GetMapping("/reader/{readerId}")
    @PreAuthorize("hasAuthority('borrow:list')")
    public Result<List<BorrowRecord>> readerHistory(@PathVariable Long readerId) {
        return Result.ok(borrowService.getReaderHistory(readerId));
    }

    @Operation(summary = "查询逾期记录")
    @GetMapping("/overdue")
    @PreAuthorize("hasAuthority('borrow:list')")
    public Result<List<BorrowRecord>> overdue() {
        return Result.ok(borrowService.getOverdueRecords());
    }
}
