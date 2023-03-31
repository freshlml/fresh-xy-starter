package com.fresh.xy.mb.utils;

import com.fresh.common.result.PageJsonResultVo;
import com.fresh.xy.common.dto.PageDto;
import com.fresh.xy.mb.core.Page;

public class PageUtils {

    public static <T> Page<T> mybatisPage(PageDto pageDto) {
        Page<T> page = new Page<T>();
        page.setPage(pageDto.getPage());
        page.setPageSize(pageDto.getPageSize());
        return page;
    }

    public static <T> PageJsonResultVo<T> pageJsonResultVo(Page<T> page) {
        PageJsonResultVo<T> pageJsonResultVo = new PageJsonResultVo<>();
        pageJsonResultVo.setPage(page.getPage())
                .setPageSize(page.getPageSize())
                .setPages(page.getPages())
                .setTotal(page.getTotal())
                .setItems(page.getItems());

        return pageJsonResultVo;
    }

}
