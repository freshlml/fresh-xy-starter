package com.fresh.xy.mb.core;


import java.util.List;

public class Page<T> {
    private long total; //总记录数
    private long page; //当前页
    private long pageSize; //每页多少条
    private long pages; //总页数
    private List<T> items;

    public Page() {
        this.page = 1;
        this.pageSize = 10;
    }

    public Page(long page, long pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public long getOffset() {
        return this.getPage() > 0 ? (this.getPage() - 1) * this.getPageSize() : 0;
    }

}
