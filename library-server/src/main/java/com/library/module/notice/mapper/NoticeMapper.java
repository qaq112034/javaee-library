package com.library.module.notice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.module.notice.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
