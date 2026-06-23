package com.library.module.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.module.book.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图书分类 Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
