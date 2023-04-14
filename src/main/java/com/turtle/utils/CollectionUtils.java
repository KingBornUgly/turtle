/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle.utils;

import java.util.Collection;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 6:04 PM
 */
public final class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * <p>判断是否为空</p>
     *
     * @param list 集合
     *
     * @return 是否为空
     */
    public static final boolean isEmpty(Collection<?> list) {
        return list == null || list.isEmpty();
    }

    /**
     * <p>判断是否非空</p>
     *
     * @param list 集合
     *
     * @return 是否非空
     */
    public static final boolean isNotEmpty(Collection<?> list) {
        return !isEmpty(list);
    }

}

