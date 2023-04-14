/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.stun;



import com.turtle.config.StunConfig;
import com.turtle.net.UdpClient;
import com.turtle.net.torrent.TorrentServer;
import com.turtle.utils.NetUtils;

import java.net.InetSocketAddress;

/**
 * Stun客户端
 * 注意：简单的STUN客户端（没有实现所有功能）
 * @author KingBornUgly
 * @date 2023/1/4 9:27 PM
 */
public final class StunClient extends UdpClient<StunMessageHandler> {

    /**
     * @param socketAddress 地址
     */
    private StunClient(final InetSocketAddress socketAddress) {
        super("STUN Client", new StunMessageHandler(socketAddress));
    }

    /**
     * <p>新建Stun客户端</p>
     *
     * @param host 服务器地址
     *
     * @return Stun客户端
     */
    public static final StunClient newInstance(final String host) {
        return newInstance(host, StunConfig.DEFAULT_PORT);
    }

    /**
     * <p>新建Stun客户端</p>
     *
     * @param host 服务器地址
     * @param port 服务器端口
     *
     * @return Stun客户端
     */
    public static final StunClient newInstance(final String host, final int port) {
        return newInstance(NetUtils.buildSocketAddress(host, port));
    }

    /**
     * <p>新建Stun客户端</p>
     *
     * @param socketAddress 服务器地址
     *
     * @return Stun客户端
     */
    public static final StunClient newInstance(final InetSocketAddress socketAddress) {
        return new StunClient(socketAddress);
    }

    @Override
    public boolean open() {
        return this.open(TorrentServer.getInstance().channel());
    }

    /**
     * <p>发送映射消息</p>
     */
    public void mappedAddress() {
        this.handler.mappedAddress();
    }

}

