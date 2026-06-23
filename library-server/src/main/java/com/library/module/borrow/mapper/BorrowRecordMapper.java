package com.library.module.borrow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.module.borrow.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 借阅记录 Mapper
 */
@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {

    /**
     * 分页查询借阅记录（关联读者和图书表）
     * <p>
     * 使用 LEFT JOIN 查询读者姓名(r.real_name)、图书标题(b.title)，
     * 避免 N+1 查询问题。
     * </p>
     */
    @Select("<script>" +
            "SELECT br.*, r.real_name AS reader_name, r.reader_no, " +
            "b.title AS book_title, b.isbn AS book_isbn " +
            "FROM borrow_record br " +
            "LEFT JOIN reader r ON br.reader_id = r.id " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "WHERE 1=1 " +
            "<if test='status != null'>" +
            "  AND br.status = #{status}" +
            "</if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "  AND (r.real_name LIKE CONCAT('%',#{keyword},'%') " +
            "    OR b.title LIKE CONCAT('%',#{keyword},'%'))" +
            "</if>" +
            "ORDER BY br.create_time DESC" +
            "</script>")
    IPage<BorrowRecord> selectBorrowPage(Page<BorrowRecord> page,
                                          @Param("status") Integer status,
                                          @Param("keyword") String keyword);

    /**
     * 查询某读者的借阅历史
     */
    @Select("SELECT br.*, b.title AS book_title, b.isbn AS book_isbn " +
            "FROM borrow_record br " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "WHERE br.reader_id = #{readerId} " +
            "ORDER BY br.create_time DESC")
    List<BorrowRecord> selectByReaderId(@Param("readerId") Long readerId);

    /**
     * 查询某读者当前借阅中的数量
     */
    @Select("SELECT COUNT(*) FROM borrow_record " +
            "WHERE reader_id = #{readerId} AND status IN (1, 3)")
    int countCurrentBorrows(@Param("readerId") Long readerId);

    /**
     * 查询逾期未还的记录
     */
    @Select("SELECT br.*, r.real_name AS reader_name, r.phone, " +
            "b.title AS book_title " +
            "FROM borrow_record br " +
            "LEFT JOIN reader r ON br.reader_id = r.id " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "WHERE br.status IN (1, 3) AND br.due_date < NOW()")
    List<BorrowRecord> selectOverdueRecords();

    /**
     * 统计过去 N 天每天的借阅量
     */
    @Select("SELECT DATE(borrow_date) AS date, COUNT(*) AS count " +
            "FROM borrow_record " +
            "WHERE borrow_date >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(borrow_date) " +
            "ORDER BY date")
    List<Map<String, Object>> countByDay(@Param("days") int days);

    /**
     * 统计各分类的借阅量
     */
    @Select("SELECT c.category_name AS name, COUNT(*) AS value " +
            "FROM borrow_record br " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "LEFT JOIN book_category c ON b.category_id = c.id " +
            "GROUP BY b.category_id, c.category_name " +
            "ORDER BY value DESC")
    List<Map<String, Object>> countByCategory();
}
