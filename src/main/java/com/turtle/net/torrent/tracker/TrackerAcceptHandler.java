/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent.tracker;

import com.turtle.net.UdpAcceptHandler;
import com.turtle.net.UdpMessageHandler;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 6:04 PM
 */
public final class TrackerAcceptHandler extends UdpAcceptHandler {

    private static final TrackerAcceptHandler INSTANCE = new TrackerAcceptHandler();

    public static final TrackerAcceptHandler getInstance() {
        return INSTANCE;
    }

    private TrackerAcceptHandler() {
    }

    /**
     * <p>消息代理</p>
     */
    private final TrackerMessageHandler trackerMessageHandler = new TrackerMessageHandler();

    @Override
    public UdpMessageHandler messageHandler(ByteBuffer buffer, InetSocketAddress socketAddress) {
        return this.trackerMessageHandler;
    }

    @Override
    public void handle(DatagramChannel channel) {
        this.trackerMessageHandler.handle(channel);
    }

}
