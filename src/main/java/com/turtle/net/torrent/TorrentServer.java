/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent;


import com.turtle.config.SystemConfig;
import com.turtle.net.UdpServer;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 6:29 PM
 */
public final class TorrentServer extends UdpServer<TorrentAcceptHandler> {

    private static final TorrentServer INSTANCE = new TorrentServer();

    public static final TorrentServer getInstance() {
        return INSTANCE;
    }

    private TorrentServer() {
        super(SystemConfig.getTorrentPort(), "Torrent(UTP/DHT/STUN) Server", TorrentAcceptHandler.getInstance());
        this.handle();
    }

}
