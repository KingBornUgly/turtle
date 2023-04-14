/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent.peer;


import com.turtle.config.SystemConfig;
import com.turtle.model.session.PeerSession;
import com.turtle.net.TcpClient;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 5:20 PM
 */
public final class PeerClient extends TcpClient<PeerMessageHandler> {

    /**
     * <p>Peer信息</p>
     */
    private final PeerSession peerSession;
    /**
     * <p>Peer消息代理</p>
     */
    private final PeerSubMessageHandler peerSubMessageHandler;

    /**
     * @param peerSession Peer信息
     * @param peerSubMessageHandler Peer消息代理
     */
    private PeerClient(PeerSession peerSession, PeerSubMessageHandler peerSubMessageHandler) {
        super("Peer Client", SystemConfig.CONNECT_TIMEOUT, new PeerMessageHandler(peerSubMessageHandler));
        this.peerSession = peerSession;
        this.peerSubMessageHandler = peerSubMessageHandler;
    }

    /**
     * <p>新建Peer客户端</p>
     *
     * @param peerSession Peer信息
     * @param peerSubMessageHandler Peer消息代理
     *
     * @return {@link PeerClient}
     */
    public static final PeerClient newInstance(PeerSession peerSession, PeerSubMessageHandler peerSubMessageHandler) {
        return new PeerClient(peerSession, peerSubMessageHandler);
    }

    @Override
    public boolean connect() {
        return this.connect(this.peerSession.host(), this.peerSession.port());
    }

    /**
     * <p>获取Peer信息</p>
     *
     * @return Peer信息
     */
    public PeerSession peerSession() {
        return this.peerSession;
    }

    /**
     * <p>获取Peer消息代理</p>
     *
     * @return Peer消息代理
     */
    public PeerSubMessageHandler peerSubMessageHandler() {
        return this.peerSubMessageHandler;
    }

}
