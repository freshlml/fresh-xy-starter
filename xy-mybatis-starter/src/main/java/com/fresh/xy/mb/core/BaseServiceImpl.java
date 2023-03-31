package com.fresh.xy.mb.core;

import com.fresh.xy.common.entity.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BaseServiceImpl<ID, E extends BaseEntity<ID>, M extends BaseMapper<ID, E>> implements BaseService<ID, E> {

    @Autowired
    private M baseMapper;

    @Override
    public int save(E entity) {
        if(entity == null) throw new NullPointerException("参数[entity]不能为null");
        return baseMapper.insert(entity);
    }

    @Override
    public int removeById(ID id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public int removeByIds(List<ID> ids) {
        if(ids == null) throw new NullPointerException("参数[ids]不能为null");
        if(ids.size() == 0) throw new IllegalArgumentException("参数[ids]不能为empty");

        return baseMapper.deleteByIds(ids);
    }

    @Override
    public int updateById(E entity) {
        if(entity == null) throw new NullPointerException("参数[entity]不能为null");

        return baseMapper.updateById(entity);
    }

    @Override
    public E getById(ID id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<E> getByIds(List<ID> ids) {
        if(ids == null) throw new NullPointerException("参数[ids]不能为null");
        if(ids.size() == 0) throw new IllegalArgumentException("参数[ids]不能为empty");

        return baseMapper.selectByIds(ids);
    }

    @Override
    public Page<E> pageByEntity(Page<E> page, E entity) {
        if(page == null) throw new NullPointerException("参数[page]不能为null");

        List<E> items = baseMapper.listByEntity(page, entity);
        page.setItems(items);
        return page;
        /* for test PageInterceptor when page is null
        List<E> items = baseMapper.listByEntity(page, entity);
        if(page != null) {
            page.setItems(items);
            return page;
        }
        Page<E> result = new Page<E>(0, 0);
        result.setItems(items);
        return result;
         */
    }

    @Override
    public Page<E> pageByEntityForTest(Page<E> page) {
        if(page == null) throw new NullPointerException("参数[page]不能为null");

        List<E> items = baseMapper.listByEntityForTest(page);
        page.setItems(items);
        return page;
        /* for test PageInterceptor when page is null
        List<E> items = baseMapper.listByEntityForTest(page);
        if(page != null) {
            page.setItems(items);
            return page;
        }
        Page<E> result = new Page<E>(0, 0);
        result.setItems(items);
        return result;
         */
    }
}
