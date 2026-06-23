package com.library.module.statistics.controller;

import com.library.common.Result;
import com.library.module.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 数据统计控制器
 */
@Tag(name = "数据统计", description = "仪表盘、借阅趋势、分类统计")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "获取仪表盘概览数据")
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('statistics:view')")
    public Result<Map<String, Object>> dashboard() {
        return Result.ok(statisticsService.getDashboard());
    }

    @Operation(summary = "获取借阅趋势（最近N天）")
    @GetMapping("/trend")
    @PreAuthorize("hasAuthority('statistics:view')")
    public Result<Object> trend(@RequestParam(defaultValue = "30") int days) {
        return Result.ok(statisticsService.getBorrowTrend(days));
    }

    @Operation(summary = "获取分类借阅统计")
    @GetMapping("/category")
    @PreAuthorize("hasAuthority('statistics:view')")
    public Result<Object> categoryStats() {
        return Result.ok(statisticsService.getCategoryStats());
    }
}
