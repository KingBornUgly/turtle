/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent.dht.request;

import com.turtle.TorrentContext;
import com.turtle.config.DhtConfig;
import com.turtle.config.PeerConfig;
import com.turtle.config.SystemConfig;
import com.turtle.context.DhtContext;
import com.turtle.context.PeerContext;
import com.turtle.model.session.PeerSession;
import com.turtle.model.session.TorrentSession;
import com.turtle.net.torrent.dht.DhtRequest;
import com.turtle.net.torrent.dht.DhtResponse;
import com.turtle.net.torrent.dht.response.AnnouncePeerResponse;
import com.turtle.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * 声明Peer
 * 声明当前节点作为Peer
 * @author KingBornUgly
 * @date 2023/1/4 9:04 PM
 */
public final class AnnouncePeerRequest extends DhtRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnouncePeerRequest.class);

    private AnnouncePeerRequest() {
        super(DhtConfig.QType.ANNOUNCE_PEER);
    }

    /**
     * <p>新建请求</p>
     *
     * @param token token
     * @param infoHash InfoHash
     *
     * @return 请求
     */
    public static final AnnouncePeerRequest newRequest(byte[] token, byte[] infoHash) {
        final AnnouncePeerRequest request = new AnnouncePeerRequest();
        request.put(DhtConfig.KEY_PORT, SystemConfig.getTorrentPortExt());
        request.put(DhtConfig.KEY_TOKEN, token);
        request.put(DhtConfig.KEY_INFO_HASH, infoHash);
        request.put(DhtConfig.KEY_IMPLIED_PORT, DhtConfig.IMPLIED_PORT_AUTO);
        return request;
    }

    /**
     * <p>处理请求</p>
     *
     * @param request 请求
     *
     * @return 响应
     */
    public static final AnnouncePeerResponse execute(DhtRequest request) {
        final byte[] token = request.getBytes(DhtConfig.KEY_TOKEN);
        // 验证Token
        if(!Arrays.equals(token, DhtContext.getInstance().token())) {
            return AnnouncePeerResponse.newInstance(DhtResponse.buildErrorResponse(request.getT(), DhtConfig.ErrorCode.CODE_203.code(), "Token错误"));
        }
        final byte[] infoHash = request.getBytes(DhtConfig.KEY_INFO_HASH);
        final String infoHashHex = StringUtils.hex(infoHash);
        final TorrentSession torrentSession = TorrentContext.getInstance().torrentSession(infoHashHex);
        if(torrentSession != null) {
            // 默认端口
            Integer peerPort = request.getInteger(DhtConfig.KEY_PORT);
            final Integer impliedPort = request.getInteger(DhtConfig.KEY_IMPLIED_PORT);
            final InetSocketAddress socketAddress = request.getSocketAddress();
            final String peerHost = socketAddress.getHostString();
            // 是否自动配置端口
            final boolean impliedPortAuto = DhtConfig.IMPLIED_PORT_AUTO.equals(impliedPort);
            if(impliedPortAuto) {
                // 自动配置端口
                peerPort = socketAddress.getPort();
            }
            final PeerSession peerSession = PeerContext.getInstance().newPeerSession(
                    infoHashHex,
                    torrentSession.statistics(),
                    peerHost,
                    peerPort,
                    PeerConfig.Source.DHT
            );
            if(impliedPortAuto) {
                // 支持UTP
                peerSession.flags(PeerConfig.PEX_UTP);
            }
        } else {
            LOGGER.debug("声明Peer种子信息不存在：{}", infoHashHex);
        }
        return AnnouncePeerResponse.newInstance(request);
    }

}

