package com.library.module.notice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.PageResult;
import com.library.common.Result;
import com.library.module.notice.entity.Notice;
import com.library.module.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "公告管理", description = "系统公告的增删改查")
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "分页查询公告")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('notice:list')")
    public Result<PageResult<Notice>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer noticeType) {
        Page<Notice> page = noticeService.pageNotices(current, size, noticeType);
        return Result.ok(PageResult.from(page));
    }

    @Operation(summary = "查询公告详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('notice:list')")
    public Result<Notice> getById(@PathVariable Long id) {
        return Result.ok(noticeService.getNoticeById(id));
    }

    @Operation(summary = "新增公告（草稿）")
    @PostMapping
    @PreAuthorize("hasAuthority('notice:add')")
    public Result<Void> add(@RequestBody Notice notice) {
        noticeService.addNotice(notice);
        return Result.okMsg("公告创建成功");
    }

    @Operation(summary = "更新公告")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('notice:add')")
    public Result<Void> update(@PathVariable Long id, @RequestBody Notice notice) {
        notice.setId(id);
        noticeService.updateNotice(notice);
        return Result.okMsg("公告更新成功");
    }

    @Operation(summary = "删除公告")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('notice:add')")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return Result.okMsg("公告删除成功");
    }

    @Operation(summary = "发布公告（草稿→已发布）")
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('notice:add')")
    public Result<Void> publish(@PathVariable Long id) {
        noticeService.publishNotice(id);
        return Result.okMsg("公告发布成功");
    }
}
