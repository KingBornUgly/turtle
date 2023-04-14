/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent.tracker;


import com.turtle.net.UdpServer;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 6:04 PM
 */
public final class TrackerServer extends UdpServer<TrackerAcceptHandler> {

    private static final TrackerServer INSTANCE = new TrackerServer();

    public static final TrackerServer getInstance() {
        return INSTANCE;
    }

    private TrackerServer() {
        super("Tracker Server", TrackerAcceptHandler.getInstance());
        this.handle();
    }

}

