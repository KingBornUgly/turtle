/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.utils;

import java.nio.ByteBuffer;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 3:19 PM
 */
public final class ByteUtils {

    private ByteUtils() {
    }

    /**
     * <p>读取剩余字节数据</p>
     *
     * @param buffer 缓冲数据
     * @return 剩余字节数据
     */
    public static final byte[] remainingToBytes(ByteBuffer buffer) {
        final byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return bytes;
    }

    /**
     * <p>读取剩余字节数据转为字符串</p>
     *
     * @param buffer 缓冲数据
     * @return 剩余字节数据字符串
     */
    public static final String remainingToString(ByteBuffer buffer) {
        return new String(remainingToBytes(buffer));
    }
}