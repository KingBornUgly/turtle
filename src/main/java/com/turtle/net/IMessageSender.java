/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net;


import com.turtle.config.SystemConfig;
import com.turtle.exception.NetException;
import com.turtle.utils.StringUtils;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 5:24 PM
 */
public interface IMessageSender {

    /**
     * <p>判断是否可用</p>
     *
     * @return 是否可用
     */
    boolean available();

    /**
     * <p>消息发送</p>
     *
     * @param message 消息内容
     *
     * @throws NetException 网络异常
     */
    default void send(String message) throws NetException {
        this.send(message, null);
    }

    /**
     * <p>消息发送</p>
     *
     * @param message 消息内容
     * @param charset 编码格式
     *
     * @throws NetException 网络异常
     */
    default void send(String message, String charset) throws NetException {
        this.send(StringUtils.toBytes(message, charset));
    }

    /**
     * <p>消息发送</p>
     *
     * @param message 消息内容
     *
     * @throws NetException 网络异常
     */
    default void send(byte[] message) throws NetException {
        this.send(ByteBuffer.wrap(message));
    }

    /**
     * <p>消息发送</p>
     *
     * @param buffer 消息内容
     *
     * @throws NetException 网络异常
     */
    default void send(ByteBuffer buffer) throws NetException {
        this.send(buffer, SystemConfig.NONE_TIMEOUT);
    }

    /**
     * <p>消息发送</p>
     * <p>所有消息发送都使用此方法发送</p>
     *
     * @param buffer 消息内容
     * @param timeout 超时时间（秒）
     *
     * @throws NetException 网络异常
     */
    void send(ByteBuffer buffer, int timeout) throws NetException;

    /**
     * <p>获取远程服务地址</p>
     *
     * @return 远程服务地址
     */
    InetSocketAddress remoteSocketAddress();

    /**
     * <p>关闭资源</p>
     */
    void close();

    /**
     * <p>数据验证</p>
     *
     * @param buffer 消息内容
     *
     * @throws NetException 网络异常
     */
    default void check(ByteBuffer buffer) throws NetException {
        if(!this.available()) {
            throw new NetException("消息发送失败：通道不可用");
        }
        if(buffer.position() != 0) {
            buffer.flip();
        }
    }

}

