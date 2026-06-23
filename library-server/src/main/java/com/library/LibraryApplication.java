package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 图书管理系统 - Spring Boot 主启动类
 *
 * @author Team JEE
 * @since 2024-12-01
 */
@SpringBootApplication
@EnableCaching      // 启用 Spring Cache（整合 Redis）
@EnableScheduling   // 启用定时任务（逾期检查）
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
        System.out.println("========================================");
        System.out.println("  图书管理系统后端服务启动成功！");
        System.out.println("  API 文档: http://localhost:8080/doc.html");
        System.out.println("========================================");
    }
}
