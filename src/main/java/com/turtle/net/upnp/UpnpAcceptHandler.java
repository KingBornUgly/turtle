/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.upnp;


import com.turtle.net.UdpAcceptHandler;
import com.turtle.net.UdpMessageHandler;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * UPNP消息接收代理
 * @author KingBornUgly
 * @date 2023/1/4 9:36 PM
 */
public final class UpnpAcceptHandler extends UdpAcceptHandler {

    private static final UpnpAcceptHandler INSTANCE = new UpnpAcceptHandler();

    public static final UpnpAcceptHandler getInstance() {
        return INSTANCE;
    }

    /**
     * <p>消息代理</p>
     */
    private final UpnpMessageHandler upnpMessageHandler = new UpnpMessageHandler();

    private UpnpAcceptHandler() {
    }

    @Override
    public void handle(DatagramChannel channel) {
        this.upnpMessageHandler.handle(channel);
    }

    @Override
    public UdpMessageHandler messageHandler(ByteBuffer buffer, InetSocketAddress socketAddress) {
        return this.upnpMessageHandler;
    }

}

