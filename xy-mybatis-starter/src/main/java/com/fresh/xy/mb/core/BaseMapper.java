package com.fresh.xy.mb.core;

import com.fresh.xy.common.entity.BaseEntity;

import java.util.List;

public interface BaseMapper<ID, E extends BaseEntity<ID>> {
    int insert(E entity);
    int deleteById(ID id);
    int deleteByIds(List<ID> ids);
    int updateById(E entity);
    E selectById(ID id);
    List<E> selectByIds(List<ID> ids);
    List<E> listByEntity(Page<E> page, E entity);
    List<E> listByEntityForTest(Page<E> page);
}
