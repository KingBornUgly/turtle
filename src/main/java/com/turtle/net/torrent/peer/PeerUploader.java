/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent.peer;

import com.turtle.config.PeerConfig;
import com.turtle.model.session.PeerSession;
import com.turtle.model.session.TorrentSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 4:24 PM
 */
public final class PeerUploader extends PeerConnect {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeerUploader.class);

    /**
     * @param peerSession           Peer信息
     * @param torrentSession        BT任务信息
     * @param peerSubMessageHandler Peer消息代理
     */
    private PeerUploader(PeerSession peerSession, TorrentSession torrentSession, PeerSubMessageHandler peerSubMessageHandler) {
        super(peerSession, torrentSession, peerSubMessageHandler);
        this.available = true;
    }

    /**
     * <p>新建Peer接入</p>
     *
     * @param peerSession           Peer信息
     * @param torrentSession        BT任务信息
     * @param peerSubMessageHandler Peer消息代理
     * @return {@link PeerUploader}
     */
    public static final PeerUploader newInstance(PeerSession peerSession, TorrentSession torrentSession, PeerSubMessageHandler peerSubMessageHandler) {
        return new PeerUploader(peerSession, torrentSession, peerSubMessageHandler);
    }

    @Override
    public void download() {
        System.out.println("====+download");
        if (
            // 快速允许
                this.peerSession.supportAllowedFast() ||
                        // 解除阻塞
                        this.peerConnectSession.isPeerUnchoked()
        ) {
            super.download();
        }
    }

    @Override
    public void release() {
        try {
            if (this.available) {
                LOGGER.debug("关闭PeerUploader：{}", this.peerSession);
                super.release();
            }
        } catch (Exception e) {
            LOGGER.error("关闭PeerUploader异常", e);
        } finally {
            this.peerSession.statusOff(PeerConfig.STATUS_UPLOAD);
            this.peerSession.peerUploader(null);
        }
    }
}
