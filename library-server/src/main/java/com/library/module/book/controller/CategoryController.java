package com.library.module.book.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.common.Result;
import com.library.module.book.entity.Category;
import com.library.module.book.mapper.CategoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图书分类控制器
 */
@Tag(name = "分类管理", description = "图书分类的增删改查（树形结构）")
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryMapper categoryMapper;

    @Operation(summary = "查询分类树")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('category:manage')")
    public Result<List<Category>> tree() {
        // 查询所有分类
        List<Category> all = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
        // 构建树形结构
        List<Category> tree = buildTree(all, 0L);
        return Result.ok(tree);
    }

    @Operation(summary = "查询所有分类（平铺列表）")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('book:list')")
    public Result<List<Category>> list() {
        return Result.ok(categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder)));
    }

    @Operation(summary = "新增分类")
    @PostMapping
    @PreAuthorize("hasAuthority('category:manage')")
    public Result<Void> add(@RequestBody Category category) {
        categoryMapper.insert(category);
        return Result.okMsg("分类添加成功");
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('category:manage')")
    public Result<Void> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryMapper.updateById(category);
        return Result.okMsg("分类更新成功");
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('category:manage')")
    public Result<Void> delete(@PathVariable Long id) {
        // 检查是否有子分类
        Long childCount = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>().eq(Category::getParentId, id));
        if (childCount > 0) {
            return Result.fail("该分类下存在子分类，请先删除子分类");
        }
        categoryMapper.deleteById(id);
        return Result.okMsg("分类删除成功");
    }

    /**
     * 递归构建分类树
     */
    private List<Category> buildTree(List<Category> all, Long parentId) {
        return all.stream()
                .filter(c -> c.getParentId().equals(parentId))
                .peek(c -> c.setChildren(buildTree(all, c.getId())))
                .toList();
    }
}
