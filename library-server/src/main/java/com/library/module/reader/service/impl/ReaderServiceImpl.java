package com.library.module.reader.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.exception.BusinessException;
import com.library.module.reader.entity.Reader;
import com.library.module.reader.mapper.ReaderMapper;
import com.library.module.reader.service.ReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 读者管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReaderServiceImpl implements ReaderService {

    private final ReaderMapper readerMapper;

    @Override
    public Page<Reader> pageReaders(int current, int size, String keyword) {
        Page<Reader> page = new Page<>(current, size);
        LambdaQueryWrapper<Reader> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Reader::getRealName, keyword)
                   .or()
                   .like(Reader::getReaderNo, keyword)
                   .or()
                   .like(Reader::getPhone, keyword);
        }
        wrapper.orderByDesc(Reader::getCreateTime);
        return readerMapper.selectPage(page, wrapper);
    }

    @Override
    public Reader getReaderById(Long id) {
        Reader reader = readerMapper.selectById(id);
        if (reader == null) {
            throw new BusinessException("读者不存在");
        }
        return reader;
    }

    @Override
    @Transactional
    public void addReader(Reader reader) {
        // 自动生成读者编号（R + 年份 + 4位序号）
        if (!StringUtils.hasText(reader.getReaderNo())) {
            String year = String.valueOf(java.time.Year.now().getValue());
            reader.setReaderNo("R" + year + String.format("%04d",
                    readerMapper.selectCount(null) + 1));
        }
        // 默认值
        if (reader.getMaxBorrowCount() == null) reader.setMaxBorrowCount(5);
        if (reader.getMaxBorrowDays() == null) reader.setMaxBorrowDays(30);
        if (reader.getStatus() == null) reader.setStatus(1);

        readerMapper.insert(reader);
        log.info("新增读者: {} ({})", reader.getRealName(), reader.getReaderNo());
    }

    @Override
    @Transactional
    public void updateReader(Reader reader) {
        Reader existing = readerMapper.selectById(reader.getId());
        if (existing == null) {
            throw new BusinessException("读者不存在");
        }
        readerMapper.updateById(reader);
    }

    @Override
    @Transactional
    public void deleteReader(Long id) {
        Reader reader = readerMapper.selectById(id);
        if (reader == null) {
            throw new BusinessException("读者不存在");
        }
        // 检查是否有未归还的图书
        // borrowRecordMapper 由 BorrowService 管理，此处简化处理
        readerMapper.deleteById(id);
        log.info("删除读者: {}", reader.getRealName());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Reader reader = readerMapper.selectById(id);
        if (reader == null) {
            throw new BusinessException("读者不存在");
        }
        reader.setStatus(status);
        readerMapper.updateById(reader);
    }
}
