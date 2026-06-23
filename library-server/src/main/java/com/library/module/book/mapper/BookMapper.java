package com.library.module.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.module.book.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 图书 Mapper
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {

    /**
     * 分页查询图书（带分类名称）
     * <p>
     * 使用 LEFT JOIN 关联 book_category 表获取分类名称。
     * MyBatis-Plus 的分页 IPage 会自动处理分页逻辑。
     * </p>
     */
    @Select("<script>" +
            "SELECT b.*, c.category_name FROM book b " +
            "LEFT JOIN book_category c ON b.category_id = c.id " +
            "WHERE b.deleted = 0 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "  AND (b.title LIKE CONCAT('%',#{keyword},'%') " +
            "    OR b.author LIKE CONCAT('%',#{keyword},'%') " +
            "    OR b.isbn LIKE CONCAT('%',#{keyword},'%'))" +
            "</if>" +
            "<if test='categoryId != null'>" +
            "  AND b.category_id = #{categoryId}" +
            "</if>" +
            "<if test='status != null'>" +
            "  AND b.status = #{status}" +
            "</if>" +
            "ORDER BY b.create_time DESC" +
            "</script>")
    IPage<Book> selectBookPage(Page<Book> page,
                               @Param("keyword") String keyword,
                               @Param("categoryId") Long categoryId,
                               @Param("status") Integer status);

    /**
     * 查询热门图书（按借阅次数降序）
     * 结果通常缓存到 Redis，减少数据库查询压力
     */
    @Select("SELECT b.*, c.category_name FROM book b " +
            "LEFT JOIN book_category c ON b.category_id = c.id " +
            "WHERE b.deleted = 0 AND b.status = 1 " +
            "ORDER BY b.borrow_count DESC LIMIT #{limit}")
    List<Book> selectHotBooks(@Param("limit") int limit);
}
