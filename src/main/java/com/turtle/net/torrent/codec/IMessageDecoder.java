/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent.codec;

import com.turtle.exception.NetException;

import java.net.InetSocketAddress;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 5:11 PM
 */
public interface IMessageDecoder<I> {

    /**
     * <p>判断消息是否解码完成</p>
     *
     * @return 是否解码完成
     *
     * @see #decode(Object)
     * @see #decode(Object, InetSocketAddress)
     * @see #onMessage(Object)
     * @see #onMessage(Object, InetSocketAddress)
     */
    default boolean done() {
        return true;
    }

    /**
     * <p>消息解码</p>
     *
     * @param message 消息
     *
     * @throws NetException 网络异常
     */
    default void decode(I message) throws NetException {
    }

    /**
     * <p>消息解码</p>
     *
     * @param message 消息
     * @param address 地址
     *
     * @throws NetException 网络异常
     */
    default void decode(I message, InetSocketAddress address) throws NetException {
    }

    /**
     * <p>消息处理</p>
     *
     * @param message 消息
     *
     * @throws NetException 网络异常
     */
    default void onMessage(I message) throws NetException {
    }

    /**
     * <p>消息处理</p>
     *
     * @param message 消息
     * @param address 地址
     *
     * @throws NetException 网络异常
     */
    default void onMessage(I message, InetSocketAddress address) throws NetException {
    }

}

