/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.codec;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 8:57 PM
 */
public interface IMessageEncoder<I> {

    /**
     * <p>消息编码</p>
     *
     * @param message 原始消息
     *
     * @return 编码消息
     */
    default I encode(I message) {
        return message;
    }

}
