/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.context;

import com.turtle.IContext;
import com.turtle.config.PeerConfig;
import com.turtle.model.session.IStatisticsSession;
import com.turtle.model.session.PeerSession;
import com.turtle.net.torrent.peer.PeerConnect;
import com.turtle.net.torrent.peer.extension.PeerExchangeMessageHandler;
import com.turtle.utils.ArrayUtils;
import com.turtle.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 10:42 PM
 */
public final class PeerContext implements IContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeerContext.class);

    private static final PeerContext INSTANCE = new PeerContext();

    public static final PeerContext getInstance() {
        return INSTANCE;
    }

    /**
     * <p>have消息队列</p>
     */
    private final Map<String, List<Integer>> haves;
    /**
     * <p>Peer下载队列</p>
     * <p>Peer使用时从下载队列中取出，使用结束后重新放回下载队列。</p>
     * <p>InfoHashHex=Peer双端队列（尾部优先使用）</p>
     */
    private final Map<String, Deque<PeerSession>> peers;
    /**
     * <p>Peer存档队列</p>
     * <p>InfoHashHex=Peer队列</p>
     */
    private final Map<String, List<PeerSession>> storagePeers;

    private PeerContext() {
        this.haves = new ConcurrentHashMap<>();
        this.peers = new ConcurrentHashMap<>();
        this.storagePeers = new ConcurrentHashMap<>();
    }

    /**
     * <p>查找PeerSession</p>
     *
     * @param infoHashHex InfoHashHex
     * @param host Peer地址
     * @param port Peer端口
     *
     * @return Peer信息
     */
    public PeerSession findPeerSession(String infoHashHex, String host, Integer port) {
        final List<PeerSession> list = this.list(infoHashHex);
        synchronized (list) {
            return this.findPeerSession(list, host, port);
        }
    }

    /**
     * <p>获取Peer存档队列</p>
     *
     * @param infoHashHex InfoHashHex
     *
     * @return Peer存档队列
     */
    public List<PeerSession> listPeerSession(String infoHashHex) {
        final List<PeerSession> list = this.list(infoHashHex);
        synchronized (list) {
            return new ArrayList<>(list);
        }
    }

    /**
     * <p>判断是否找到Peer</p>
     *
     * @param infoHashHex InfoHashHex
     *
     * @return 是否找到Peer
     */
    public boolean isNotEmpty(String infoHashHex) {
        final List<PeerSession> list = this.list(infoHashHex);
        synchronized (list) {
            return !list.isEmpty();
        }
    }

    /**
     * <p>删除InfoHashHex所有队列</p>
     *
     * @param infoHashHex InfoHashHex
     */
    public void remove(String infoHashHex) {
        LOGGER.debug("删除Peer队列：{}", infoHashHex);
        this.haves.remove(infoHashHex);
        this.peers.remove(infoHashHex);
        this.storagePeers.remove(infoHashHex);
    }

    /**
     * <p>添加Peer</p>
     * <p>高优先级Peer插入尾部优先使用</p>
     *
     * @param infoHashHex InfoHashHex
     * @param parent 任务下载统计
     * @param host 地址
     * @param port 端口
     * @param source Peer来源
     *
     * @return PeerSession
     */
    public PeerSession newPeerSession(String infoHashHex, IStatisticsSession parent, String host, Integer port, PeerConfig.Source source) {
        synchronized (this) {
            final List<PeerSession> list = this.list(infoHashHex);
            synchronized (list) {
                PeerSession peerSession = this.findPeerSession(list, host, port);
                if(peerSession == null) {
                    LOGGER.debug("添加PeerSession：{}-{}-{}", host, port, source);
                    peerSession = PeerSession.newInstance(parent, host, port);
                    final Deque<PeerSession> deque = this.deque(infoHashHex);
                    synchronized (deque) {
                        if(source.preference()) {
                            // 插入尾部：优先级高
                            deque.offerLast(peerSession);
                        } else {
                            // 插入头部：优先级低
                            deque.offerFirst(peerSession);
                        }
                    }
                    list.add(peerSession);
                }
                peerSession.source(source);
                return peerSession;
            }
        }
    }

    /**
     * <p>添加劣质Peer：插入头部</p>
     *
     * @param infoHashHex InfoHashHex
     * @param peerSession Peer信息
     */
    public void inferior(String infoHashHex, PeerSession peerSession) {
        final Deque<PeerSession> deque = this.deque(infoHashHex);
        synchronized (deque) {
            deque.offerFirst(peerSession);
        }
    }

    /**
     * <p>添加优质Peer：插入尾部</p>
     *
     * @param infoHashHex InfoHashHex
     * @param peerSession Peer信息
     */
    public void preference(String infoHashHex, PeerSession peerSession) {
        final Deque<PeerSession> deque = this.deque(infoHashHex);
        synchronized (deque) {
            deque.offerLast(peerSession);
        }
    }

    /**
     * <p>挑选优质Peer</p>
     *
     * @param infoHashHex InfoHashHex
     *
     * @return Peer信息
     */
    public PeerSession pick(String infoHashHex) {
        final Deque<PeerSession> deque = this.deque(infoHashHex);
        synchronized (deque) {
            int index = 0;
            final int size = deque.size();
            PeerSession peerSession = null;
            while(true) {
                if(++index > size) {
                    break;
                }
                peerSession = deque.pollLast();
                if(peerSession.available()) {
                    return peerSession;
                } else {
                    deque.offerFirst(peerSession);
                }
            }
            return null;
        }
    }

    /**
     * <p>发送have消息</p>
     *
     * @param infoHashHex InfoHashHex
     * @param index Piece索引
     */
    public void have(String infoHashHex, int index) {
        final List<Integer> list = this.haves(infoHashHex);
        synchronized (list) {
            list.add(index);
        }
    }

    /**
     * <p>发送have消息</p>
     *
     * @param infoHashHex InfoHashHex
     */
    public void have(String infoHashHex) {
        Integer[] indexArray;
        final List<Integer> list = this.haves(infoHashHex);
        synchronized (list) {
            indexArray = list.toArray(new Integer[list.size()-1]);
            list.clear();
        }
        if(ArrayUtils.isEmpty(indexArray)) {
            LOGGER.debug("发送have消息：没有数据");
            return;
        }
        final List<PeerSession> sessions = this.listConnectPeerSession(infoHashHex);
        sessions.forEach(session -> {
            final PeerConnect peerConnect = session.peerConnect();
            if(peerConnect != null && peerConnect.available()) {
                peerConnect.have(indexArray);
            }
        });
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("发送have消息：{}-{}", sessions.size(), indexArray.length);
        }
    }

    /**
     * <p>发送PEX消息</p>
     *
     * @param infoHashHex InfoHashHex
     */
    public void pex(String infoHashHex) {
        final List<PeerSession> sessions = this.listConnectPeerSession(infoHashHex);
        // 优质Peer：下载数据
        final List<PeerSession> optimize = sessions.stream()
                .filter(session -> session.statistics().downloadSize() > 0)
                .collect(Collectors.toList());
        final byte[] message = PeerExchangeMessageHandler.buildMessage(optimize);
        if(ArrayUtils.isEmpty(message)) {
            LOGGER.debug("发送PEX消息失败：没有数据");
            return;
        }
        sessions.forEach(session -> {
            final PeerConnect peerConnect = session.peerConnect();
            if(peerConnect != null && peerConnect.available()) {
                peerConnect.pex(message);
            }
        });
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("发送PEX消息：{}", sessions.size());
        }
    }

    /**
     * <p>发送uploadOnly消息</p>
     *
     * @param infoHashHex InfoHashHex
     */
    public void uploadOnly(String infoHashHex) {
        final List<PeerSession> sessions = this.listConnectPeerSession(infoHashHex);
        sessions.forEach(session -> {
            final PeerConnect peerConnect = session.peerConnect();
            if(peerConnect != null && peerConnect.available()) {
                peerConnect.uploadOnly();
            }
        });
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("发送uploadOnly消息：{}", sessions.size());
        }
    }

    /**
     * <p>获取have消息队列</p>
     *
     * @param infoHashHex InfoHashHex
     *
     * @return have消息队列
     */
    private List<Integer> haves(String infoHashHex) {
        synchronized (this.haves) {
            return this.haves.computeIfAbsent(infoHashHex, key -> new ArrayList<>());
        }
    }

    /**
     * <p>获取Peer下载队列</p>
     *
     * @param infoHashHex InfoHashHex
     *
     * @return Peer下载队列
     */
    private Deque<PeerSession> deque(String infoHashHex) {
        synchronized (this.peers) {
            return this.peers.computeIfAbsent(infoHashHex, key -> new LinkedBlockingDeque<>());
        }
    }

    /**
     * <p>获取Peer存档队列</p>
     *
     * @param infoHashHex InfoHashHex
     *
     * @return Peer存档队列
     */
    private List<PeerSession> list(String infoHashHex) {
        synchronized (this.storagePeers) {
            return this.storagePeers.computeIfAbsent(infoHashHex, key -> new ArrayList<>());
        }
    }

    /**
     * <p>获取存档队列连接中的Peer队列</p>
     *
     * @param infoHashHex InfoHashHex
     *
     * @return 连接中的Peer队列
     */
    private List<PeerSession> listConnectPeerSession(String infoHashHex) {
        final List<PeerSession> list = this.list(infoHashHex);
        if(CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        synchronized (list) {
            return list.stream()
                    .filter(PeerSession::available)
                    .filter(PeerSession::connected)
                    .collect(Collectors.toList());
        }
    }

    /**
     * <p>查找PeerSession</p>
     *
     * @param list 队列
     * @param host 地址
     * @param port 端口
     *
     * @return Peer信息
     */
    private PeerSession findPeerSession(List<PeerSession> list, String host, Integer port) {
        return list.stream()
                .filter(peer -> peer.equals(host, port))
                .findFirst()
                .orElse(null);
    }

}

