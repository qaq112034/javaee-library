package com.library.module.reader.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.module.reader.entity.Reader;

/**
 * 读者管理服务接口
 */
public interface ReaderService {

    /** 分页查询读者 */
    Page<Reader> pageReaders(int current, int size, String keyword);

    /** 根据ID查询 */
    Reader getReaderById(Long id);

    /** 新增读者 */
    void addReader(Reader reader);

    /** 更新读者信息 */
    void updateReader(Reader reader);

    /** 删除读者 */
    void deleteReader(Long id);

    /** 更新读者状态 */
    void updateStatus(Long id, Integer status);
}
