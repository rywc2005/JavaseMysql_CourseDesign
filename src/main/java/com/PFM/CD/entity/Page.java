package com.PFM.CD.entity;

import java.util.List;

/**
 * @Author: 马xs
 * @CreateTime: 2025-06-25
 * @Description:
 * @Version: 17.0
 */


public class Page<T> {
    private List<T> content;      // 当前页数据
    private int totalPages;       // 总页数
    private long totalElements;   // 总记录数
    private int pageNum;          // 当前页码（从1开始）
    private int pageSize;         // 每页记录数

    // 新增构造方法和计算总页数的方法
    public Page(List<T> content, long totalElements, int pageNum, int pageSize) {
        this.content = content;
        this.totalElements = totalElements;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = (int) ((totalElements + pageSize - 1) / pageSize);
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    // Getter/Setter 方法
    // ... 省略已有方法，补充新增字段的getter/setter ...
}

