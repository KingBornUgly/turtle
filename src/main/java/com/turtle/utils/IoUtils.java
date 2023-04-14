/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 5:54 PM
 */
public final class IoUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IoUtils.class);

    private IoUtils() {
    }

    /**
     * <p>关闭Closeable</p>
     *
     * @param closeable Closeable
     */
    public static final void close(AutoCloseable closeable) {
        try {
            if(closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            LOGGER.error("关闭Closeable异常", e);
        }
    }

}
