/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.context;

import com.turtle.IContext;
import com.turtle.config.SystemConfig;
import com.turtle.config.TrackerConfig;
import com.turtle.exception.NetException;
import com.turtle.model.message.AnnounceMessage;
import com.turtle.model.message.ScrapeMessage;
import com.turtle.model.session.TorrentSession;
import com.turtle.model.session.TrackerSession;
import com.turtle.net.torrent.tracker.HttpTrackerSession;
import com.turtle.net.torrent.tracker.TrackerLauncher;
import com.turtle.net.torrent.tracker.UdpTrackerSession;
import com.turtle.protocol.Protocol;
import com.turtle.utils.CollectionUtils;
import com.turtle.utils.StringUtils;
import com.turtle.utils.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 5:59 PM
 */
public final class TrackerContext implements IContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackerContext.class);

    private static final TrackerContext INSTANCE = new TrackerContext();

    public static final TrackerContext getInstance() {
        return INSTANCE;
    }

    /**
     * <p>TrackerSession Map</p>
     * <p>{@link TrackerSession#id()}=TrackerSession</p>
     */
    private final Map<Integer, TrackerSession> trackerSessions;
    /**
     * <p>TrackerLauncher Map</p>
     * <p>{@link TrackerLauncher#id()}=TrackerLauncher</p>
     */
    private final Map<Integer, TrackerLauncher> trackerLaunchers;

    private TrackerContext() {
        this.trackerSessions = new ConcurrentHashMap<>();
        this.trackerLaunchers = new ConcurrentHashMap<>();
        this.register();
    }

    /**
     * <p>新建TrackerLauncher</p>
     *
     * @param trackerSession TrackerSession
     * @param torrentSession BT任务信息
     *
     * @return TrackerLauncher
     */
    public TrackerLauncher buildTrackerLauncher(TrackerSession trackerSession, TorrentSession torrentSession) {
        final TrackerLauncher launcher = TrackerLauncher.newInstance(trackerSession, torrentSession);
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("加载TrackerLauncher：{}-{}-{}", launcher.id(), trackerSession.id(), trackerSession.announceUrl());
        }
        this.trackerLaunchers.put(launcher.id(), launcher);
        return launcher;
    }

    /**
     * <p>删除TrackerLauncher</p>
     *
     * @param id {@link TrackerLauncher#id()}
     */
    public void removeTrackerLauncher(Integer id) {
        LOGGER.debug("删除TrackerLauncher：{}", id);
        this.trackerLaunchers.remove(id);
    }

    /**
     * <p>处理声明信息</p>
     *
     * @param message 消息
     */
    public void announce(AnnounceMessage message) {
        if(message == null) {
            return;
        }
        final Integer id = message.getId();
        final TrackerLauncher trackerLauncher = this.trackerLaunchers.get(id);
        if(trackerLauncher != null) {
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug(" 收到声明响应消息：{} " +
                                "做种Peer数量：{} " +
                                "下载Peer数量：{} " +
                                "下次请求等待时间：{}",
                        trackerLauncher.announceUrl(),
                        message.getSeeder(),
                        message.getLeecher(),
                        message.getInterval()
                );
            }
            trackerLauncher.announce(message);
        } else {
            LOGGER.debug("TrackerLauncher没有注册（声明消息）：{}", message);
        }
    }

    /**
     * <p>处理刮擦消息</p>
     *
     * @param message 消息
     */
    public void scrape(ScrapeMessage message) {
        if(message == null) {
            return;
        }
        final Integer id = message.getId();
        final TrackerLauncher trackerLauncher = this.trackerLaunchers.get(id);
        if(trackerLauncher != null) {
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("收到刮擦响应消息：{} " +
                                "做种Peer数量：{} " +
                                "下载Peer数量：{} " +
                                "完成Peer数量：{}",
                        trackerLauncher.announceUrl(),
                        message.getSeeder(),
                        message.getLeecher(),
                        message.getCompleted()
                );
            }
        } else {
            LOGGER.debug("TrackerLauncher没有注册（刮擦消息）：{}", message);
        }
    }

    /**
     * <p>处理连接ID消息</p>
     *
     * @param trackerId {@link TrackerSession#id()}
     * @param connectionId 连接ID
     */
    public void connectionId(int trackerId, long connectionId) {
        final TrackerSession trackerSession = this.trackerSessions.get(trackerId);
        if(trackerSession instanceof UdpTrackerSession) {
            // 类型判断不用判断是否为空
            ((UdpTrackerSession) trackerSession).connectionId(connectionId);
        }
    }

    /**
     * <p>获取TrackerSession拷贝</p>
     *
     * @return TrackerSession拷贝
     */
    public List<TrackerSession> sessions() {
        return new ArrayList<>(this.trackerSessions.values());
    }

    /**
     * <p>获取TrackerSession列表</p>
     *
     * @param announceUrl 声明地址
     *
     * @return TrackerSession列表
     *
     * @see #sessions(String, List)
     */
    public List<TrackerSession> sessions(String announceUrl) {
        return this.sessions(announceUrl, null);
    }

    /**
     * <p>获取TrackerSession列表</p>
     *
     * @param announceUrls 声明地址集合
     *
     * @return TrackerSession列表
     *
     * @see #sessions(String, List)
     */
    public List<TrackerSession> sessions(List<String> announceUrls) {
        return this.sessions(null, announceUrls);
    }

    /**
     * <p>获取TrackerSession列表</p>
     *
     * @param announceUrl 声明地址
     * @param announceUrls 声明地址集合
     *
     * @return TrackerSession列表
     *
     * @see #sessions(String, List, boolean)
     */
    public List<TrackerSession> sessions(String announceUrl, List<String> announceUrls) {
        return this.sessions(announceUrl, announceUrls, false);
    }

    /**
     * <p>获取TrackerSession列表</p>
     *
     * @param announceUrl 声明地址
     * @param announceUrls 声明地址集合
     * @param privateTorrent 是否是私有种子
     *
     * @return TrackerSession列表
     *
     * @see #sessions(int, List)
     */
    public List<TrackerSession> sessions(String announceUrl, List<String> announceUrls, boolean privateTorrent) {
        final List<TrackerSession> sessions = this.buildTrackerSession(announceUrl, announceUrls);
        if(privateTorrent) {
            LOGGER.debug("私有种子：禁止补充Tracker");
            return sessions;
        }
        final int size = sessions.size();
        final int maxSize = SystemConfig.getTrackerSize();
        if(size < maxSize) {
            final List<TrackerSession> subjoin = this.sessions(maxSize - size, sessions);
            if(CollectionUtils.isNotEmpty(subjoin)) {
                sessions.addAll(subjoin);
            }
        }
        return sessions;
    }

    /**
     * <p>补充TrackerSession</p>
     *
     * @param size 补充数量
     * @param sessions 现有客户端
     *
     * @return TrackerSession列表
     */
    private List<TrackerSession> sessions(int size, List<TrackerSession> sessions) {
        return this.trackerSessions.values().stream()
                .filter(client -> client.available() && !sessions.contains(client))
                // 排序：权重
                .sorted()
                .limit(size)
                .collect(Collectors.toList());
    }

    /**
     * <p>注册默认Tracker服务器</p>
     */
    private void register() {
        this.buildTrackerSession(TrackerConfig.getInstance().announces());
    }

    /**
     * <p>新建TrackerSession列表</p>
     *
     * @param announceUrl 声明地址
     * @param announceUrls 声明地址集合
     *
     * @return TrackerSession列表
     *
     * @see #buildTrackerSession(List)
     */
    private List<TrackerSession> buildTrackerSession(String announceUrl, List<String> announceUrls) {
        final List<String> announces = new ArrayList<>();
        if(StringUtils.isNotEmpty(announceUrl)) {
            announces.add(announceUrl);
        }
        if(CollectionUtils.isNotEmpty(announceUrls)) {
            announces.addAll(announceUrls);
        }
        return this.buildTrackerSession(announces);
    }

    /**
     * <p>新建TrackerSession列表</p>
     *
     * @param announceUrls 声明地址集合
     *
     * @return TrackerSession列表
     *
     * @see #buildTrackerSession(String)
     */
    private List<TrackerSession> buildTrackerSession(List<String> announceUrls) {
        if(announceUrls == null) {
            announceUrls = new ArrayList<>();
        }
        return announceUrls.stream()
                .map(announceUrl -> this.buildTrackerSession(announceUrl.trim()))
                .filter(Objects::nonNull)
                .filter(TrackerSession::available)
                .collect(Collectors.toList());
    }

    /**
     * <p>新建TrackerSession</p>
     *
     * @param announceUrl 声明地址
     *
     * @return TrackerSession
     *
     * @see #buildTrackerSessionProxy(String)
     */
    private TrackerSession buildTrackerSession(String announceUrl) {
        if(StringUtils.isEmpty(announceUrl)) {
            return null;
        }
        final Optional<TrackerSession> optional = this.trackerSessions.values().stream()
                .filter(client -> client.equalsAnnounceUrl(announceUrl))
                .findFirst();
        if(optional.isPresent()) {
            return optional.get();
        }
        try {
            final TrackerSession session = this.buildTrackerSessionProxy(announceUrl);
            this.trackerSessions.put(session.id(), session);
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("注册TrackerSession：{}-{}", session.id(), session.announceUrl());
            }
            return session;
        } catch (NetException e) {
            LOGGER.error("注册TrackerSession异常：{}", announceUrl, e);
        } catch (Exception e) {
            LOGGER.error("注册TrackerSession异常：{}", announceUrl, e);
        }
        return null;
    }

    /**
     * <p>新建TrackerSession代理</p>
     *
     * @param announceUrl 声明地址
     *
     * @return TrackerSession
     *
     * @throws NetException 网络异常
     *
     * @see #buildTrackerSessionProtocol(String)
     */
    private TrackerSession buildTrackerSessionProxy(final String announceUrl) throws NetException {
        TrackerSession session = this.buildTrackerSessionProtocol(announceUrl);
        if(session == null) {
            // 注册失败URL解码重试
            session = this.buildTrackerSessionProtocol(UrlUtils.decode(announceUrl));
        }
        if(session == null) {
            throw new NetException("未知Tracker协议：" + announceUrl);
        }
        return session;
    }

    /**
     * <p>新建TrackerSession协议</p>
     *
     * @param announceUrl 声明地址
     *
     * @return TrackerSession
     *
     * @throws NetException 网络异常
     */
    private TrackerSession buildTrackerSessionProtocol(final String announceUrl) throws NetException {
        if(Protocol.Type.HTTP.verify(announceUrl)) {
            return HttpTrackerSession.newInstance(announceUrl);
        } else if(Protocol.Type.UDP.verify(announceUrl)) {
            return UdpTrackerSession.newInstance(announceUrl);
        }
        return null;
    }

}

