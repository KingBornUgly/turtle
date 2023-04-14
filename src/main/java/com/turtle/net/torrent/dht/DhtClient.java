/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent.dht;

import com.turtle.model.InfoHash;
import com.turtle.model.session.NodeSession;
import com.turtle.net.UdpClient;
import com.turtle.net.torrent.TorrentServer;
import com.turtle.utils.NetUtils;
import com.turtle.utils.StringUtils;

import java.net.InetSocketAddress;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 6:29 PM
 */
public final class DhtClient extends UdpClient<DhtMessageHandler> {

    /**
     * @param socketAddress 地址
     */
    private DhtClient(InetSocketAddress socketAddress) {
        super("DHT Client", new DhtMessageHandler(socketAddress));
    }

    /**
     * <p>新建DHT客户端</p>
     *
     * @param host 地址
     * @param port 端口
     *
     * @return DHT客户端
     */
    public static final DhtClient newInstance(final String host, final int port) {
        return newInstance(NetUtils.buildSocketAddress(host, port));
    }

    /**
     * <p>新建DHT客户端</p>
     *
     * @param socketAddress 地址
     *
     * @return DHT客户端
     */
    public static final DhtClient newInstance(InetSocketAddress socketAddress) {
        return new DhtClient(socketAddress);
    }

    @Override
    public boolean open() {
        return this.open(TorrentServer.getInstance().channel());
    }

    /**
     * <p>Ping</p>
     *
     * @return 节点
     */
    public NodeSession ping() {
        return this.handler.ping();
    }

    /**
     * <p>查询节点</p>
     *
     * @param target NodeId或者InfoHash
     */
    public void findNode(String target) {
        this.findNode(StringUtils.unhex(target));
    }

    /**
     * <p>查询节点</p>
     *
     * @param target NodeId或者InfoHash
     */
    public void findNode(byte[] target) {
        this.handler.findNode(target);
    }

    /**
     * <p>查询Peer</p>
     *
     * @param infoHash InfoHash
     */
    public void getPeers(InfoHash infoHash) {
        this.getPeers(infoHash.infoHash());
    }

    /**
     * <p>查询Peer</p>
     *
     * @param infoHash InfoHash
     */
    public void getPeers(byte[] infoHash) {
        this.handler.getPeers(infoHash);
    }

    /**
     * <p>声明Peer</p>
     *
     * @param token Token
     * @param infoHash InfoHash
     */
    public void announcePeer(byte[] token, InfoHash infoHash) {
        this.announcePeer(token, infoHash.infoHash());
    }

    /**
     * <p>声明Peer</p>
     *
     * @param token Token
     * @param infoHash InfoHash
     */
    public void announcePeer(byte[] token, byte[] infoHash) {
        this.handler.announcePeer(token, infoHash);
    }

}

