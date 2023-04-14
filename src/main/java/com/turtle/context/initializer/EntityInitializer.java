/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.context.initializer;


import com.turtle.context.EntityContext;

/**
 * 实体初始化器
 * @author KingBornUgly
 * @date 2023/1/4 9:46 PM
 */
public final class EntityInitializer extends Initializer {

    private EntityInitializer() {
        super("实体");
    }

    public static final EntityInitializer newInstance() {
        return new EntityInitializer();
    }

    @Override
    protected void init() {
        EntityContext.getInstance().load();
    }

}
