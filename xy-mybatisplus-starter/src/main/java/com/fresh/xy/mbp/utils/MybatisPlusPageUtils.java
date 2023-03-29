package com.fresh.xy.mbp.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fresh.common.result.PageJsonResultVo;
import com.fresh.xy.common.dto.PageDto;


public class MybatisPlusPageUtils {

    public static <T> IPage<T> mybatisPlusPage(PageDto pageDto) {
        Page<T> pageParam = new Page<>();
        pageParam.setCurrent(pageDto.getPage());
        pageParam.setSize(pageDto.getPageSize());
        return pageParam;
    }

    public static <T> PageJsonResultVo<T> pageJsonResultVo(IPage<T> page) {
        PageJsonResultVo<T> pageJsonResultVo = new PageJsonResultVo<>();
        pageJsonResultVo.setPage(page.getCurrent())
                .setPageSize(page.getSize())
                .setPages(page.getPages())
                .setTotal(page.getTotal())
                .setItems(page.getRecords());
        return pageJsonResultVo;
    }


}
