/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent.tracker;

import com.turtle.net.UdpClient;

import java.net.InetSocketAddress;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 6:02 PM
 */
public final class TrackerClient extends UdpClient<TrackerMessageHandler> {

    /**
     * @param socketAddress 地址
     */
    private TrackerClient(InetSocketAddress socketAddress) {
        super("Tracker Client", new TrackerMessageHandler(socketAddress));
    }

    /**
     * <p>新建Tracker客户端</p>
     *
     * @param socketAddress 地址
     *
     * @return {@link TrackerClient}
     */
    public static final TrackerClient newInstance(InetSocketAddress socketAddress) {
        return new TrackerClient(socketAddress);
    }

    @Override
    public boolean open() {
        return this.open(TrackerServer.getInstance().channel());
    }

}

