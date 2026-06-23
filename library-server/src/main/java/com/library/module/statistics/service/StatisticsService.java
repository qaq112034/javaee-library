package com.library.module.statistics.service;

import java.util.Map;

public interface StatisticsService {

    /** 仪表盘概览数据 */
    Map<String, Object> getDashboard();

    /** 借阅趋势（最近N天） */
    Object getBorrowTrend(int days);

    /** 分类借阅统计 */
    Object getCategoryStats();
}
