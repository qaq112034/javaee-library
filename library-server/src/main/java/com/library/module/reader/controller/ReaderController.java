package com.library.module.reader.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.module.reader.entity.Reader;
import com.library.module.reader.service.ReaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 读者管理控制器
 */
@Tag(name = "读者管理", description = "读者的增删改查")
@RestController
@RequestMapping("/api/reader")
@RequiredArgsConstructor
public class ReaderController {

    private final ReaderService readerService;

    @Operation(summary = "分页查询读者列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('reader:list')")
    public Result<PageResult<Reader>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<Reader> page = readerService.pageReaders(current, size, keyword);
        return Result.ok(PageResult.from(page));
    }

    @Operation(summary = "根据ID查询读者")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('reader:list')")
    public Result<Reader> getById(@PathVariable Long id) {
        return Result.ok(readerService.getReaderById(id));
    }

    @Operation(summary = "新增读者")
    @PostMapping
    @PreAuthorize("hasAuthority('reader:add')")
    public Result<Void> add(@RequestBody Reader reader) {
        readerService.addReader(reader);
        return Result.okMsg("读者添加成功");
    }

    @Operation(summary = "更新读者信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('reader:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody Reader reader) {
        reader.setId(id);
        readerService.updateReader(reader);
        return Result.okMsg("读者更新成功");
    }

    @Operation(summary = "删除读者")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('reader:edit')")
    public Result<Void> delete(@PathVariable Long id) {
        readerService.deleteReader(id);
        return Result.okMsg("读者删除成功");
    }

    @Operation(summary = "更新读者状态（正常/停用）")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('reader:edit')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) {
        readerService.updateStatus(id, body.get("status"));
        return Result.okMsg("状态更新成功");
    }
}
