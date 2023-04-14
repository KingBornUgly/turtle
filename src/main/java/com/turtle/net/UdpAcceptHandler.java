/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 6:05 PM
 */
public abstract class UdpAcceptHandler implements IChannelHandler<DatagramChannel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpAcceptHandler.class);

    /**
     * <p>接收消息</p>
     *
     * @param buffer 消息
     * @param socketAddress 地址
     */
    public void receive(ByteBuffer buffer, InetSocketAddress socketAddress) {
        final UdpMessageHandler handler = this.messageHandler(buffer, socketAddress);
        try {
            if(handler.available()) {
                buffer.flip();
                handler.onReceive(buffer, socketAddress);
            }
        } catch (Exception e) {
            LOGGER.error("UDP接收消息异常：{}", socketAddress, e);
        }
    }

    /**
     * <p>获取消息代理</p>
     *
     * @param buffer 消息
     * @param socketAddress 地址
     *
     * @return 消息代理
     */
    public abstract UdpMessageHandler messageHandler(ByteBuffer buffer, InetSocketAddress socketAddress);

}
