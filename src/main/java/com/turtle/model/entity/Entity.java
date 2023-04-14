/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.model.entity;

import com.turtle.utils.BeanUtils;
import com.turtle.utils.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 4:40 PM
 */
public abstract class Entity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * <p>ID</p>
     */
    protected String id;
    /**
     * <p>创建时间</p>
     */
    protected Date createDate;
    /**
     * <p>修改时间</p>
     */
    protected Date modifyDate;

    /**
     * <p>获取ID</p>
     *
     * @return ID
     */
    public String getId() {
        return id;
    }

    /**
     * <p>设置ID</p>
     *
     * @param id ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * <p>获取创建时间</p>
     *
     * @return 创建时间
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * <p>设置创建时间</p>
     *
     * @param createDate 创建时间
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * <p>获取修改时间</p>
     *
     * @return 修改时间
     */
    public Date getModifyDate() {
        return modifyDate;
    }

    /**
     * <p>设置修改时间</p>
     *
     * @param modifyDate 修改时间
     */
    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) {
            return true;
        }
        if(object instanceof Entity ) {
            return StringUtils.equals(this.id, ((Entity) object).id);
        }
        return false;
    }

    @Override
    public String toString() {
        return BeanUtils.toString(this);
    }

}

