/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.DatagramChannel;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 6:03 PM
 */
public abstract class UdpClient<T extends UdpMessageHandler> extends Client<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpClient.class);

    /**
     * <p>新建客户端时自动打开通道</p>
     *
     * @param name 客户端名称
     * @param handler 消息代理
     */
    protected UdpClient(String name, T handler) {
        super(name, handler);
        this.open();
    }

    /**
     * <p>打开通道</p>
     * <p>UDP客户端通道使用UDP服务端通道</p>
     *
     * @return 打开状态
     *
     * @see UdpServer#channel()
     * @see #open(DatagramChannel)
     */
    public abstract boolean open();

    /**
     * <p>打开通道</p>
     *
     * @param channel 通道
     *
     * @return 打开状态
     */
    protected boolean open(DatagramChannel channel) {
        if(channel == null) {
            return false;
        }
        this.handler.handle(channel);
        return true;
    }

    @Override
    public void close() {
        LOGGER.debug("关闭UDP Client：{}", this.name);
        super.close();
    }

}

