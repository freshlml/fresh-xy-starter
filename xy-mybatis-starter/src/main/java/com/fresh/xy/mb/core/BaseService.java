package com.fresh.xy.mb.core;

import com.fresh.xy.common.entity.BaseEntity;

import java.util.List;

public interface BaseService<ID, E extends BaseEntity<ID>> {

    /**
     * Insert the specified entity to corresponding actual table.
     *
     * @param entity the specified entity
     * @throws NullPointerException If the specified entity is null
     * @return the affected rows
     */
    int save(E entity);

    /**
     * Delete row of the corresponding actual table according to the specified id.
     * If the specified id is null, there nothings be delete.
     *
     * @param id the specified id
     * @return the affected rows
     */
    int removeById(ID id);

    /**
     * Delete rows of the corresponding actual table according to the specified ids.
     *
     * @param ids the specified ids
     * @throws NullPointerException If the specified ids is null
     * @throws IllegalArgumentException If the specified ids is empty
     * @return the affected rows
     */
    int removeByIds(List<ID> ids);

    /**
     * Update rows of the corresponding actual table according to the id field of the specified entity.
     * If the id field is null, there nothings be update.
     *
     * @param entity the specified entity
     * @throws NullPointerException If the specified entity is null
     * @return the affected rows
     */
    int updateById(E entity);

    /**
     * Select row of he corresponding actual table according to the specified id.
     * If the specified id is null, there nothings be select.
     *
     * @param id the specified id
     * @return a entity denotes the selected row
     */
    E getById(ID id);

    /**
     * Selects rows of the corresponding actual table according to the specified ids.
     *
     * @param ids the specified ids
     * @throws NullPointerException If the specified ids is null
     * @throws IllegalArgumentException If the specified ids is empty
     * @return a list containing the selected rows
     */
    List<E> getByIds(List<ID> ids);

    /**
     * Selects paged rows of the corresponding actual table according to the specified entity.
     * If the specified entity is null or all of field is null, then whole table paged.
     *
     * @param page the specified page
     * @param entity the specified entity
     * @throws NullPointerException If the specified page is null
     * @return a Page containing the selected rows and page params
     */
    Page<E> pageByEntity(Page<E> page, E entity);
    /**
     * Selects paged rows of the corresponding actual table according to the specified entity.
     *
     * @param page the specified page
     * @throws NullPointerException If the specified page is null
     * @return a Page containing the selected rows and page params
     */
    Page<E> pageByEntityForTest(Page<E> page);
}
