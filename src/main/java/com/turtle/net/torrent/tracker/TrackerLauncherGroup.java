/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent.tracker;

import com.turtle.config.PeerConfig;
import com.turtle.context.SystemThreadContext;
import com.turtle.context.TrackerContext;
import com.turtle.model.Magnet;
import com.turtle.model.Torrent;
import com.turtle.model.session.TorrentSession;
import com.turtle.model.session.TrackerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 5:57 PM
 */
public final class TrackerLauncherGroup {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackerLauncherGroup.class);

    /**
     * <p>BT任务信息</p>
     */
    private final TorrentSession torrentSession;
    /**
     * <p>TrackerLauncher集合</p>
     */
    private final List<TrackerLauncher> trackerLaunchers;

    /**
     * @param torrentSession BT任务信息
     */
    private TrackerLauncherGroup(TorrentSession torrentSession) {
        this.torrentSession = torrentSession;
        this.trackerLaunchers = new ArrayList<>();
    }

    /**
     * <p>新建Tracker执行器组</p>
     *
     * @param torrentSession BT任务信息
     *
     * @return {@link TrackerLauncherGroup}
     */
    public static final TrackerLauncherGroup newInstance(TorrentSession torrentSession) {
        return new TrackerLauncherGroup(torrentSession);
    }

    /**
     * <p>获取所有Tracker执行器的声明地址</p>
     *
     * @return Tracker执行器的声明地址
     */
    public List<String> trackers() {
        synchronized (this.trackerLaunchers) {
            return this.trackerLaunchers.stream()
                    .map(TrackerLauncher::announceUrl)
                    .collect(Collectors.toList());
        }
    }

    /**
     * <p>加载TrackerLauncher</p>
     */
    public void loadTracker() {
        List<TrackerSession> sessions = null;
        final PeerConfig.Action action = this.torrentSession.action();
        final TrackerContext context = TrackerContext.getInstance();
        if(action == PeerConfig.Action.TORRENT) {
            final Torrent torrent = this.torrentSession.torrent();
            sessions = context.sessions(torrent.getAnnounce(), torrent.getAnnounceList(), this.torrentSession.privateTorrent());
        } else if(action == PeerConfig.Action.MAGNET) {
            final Magnet magnet = this.torrentSession.magnet();
            sessions = context.sessions(magnet.getTr());
        } else {
            sessions = context.sessions();
        }
        final List list = sessions.stream()
                .map(client -> context.buildTrackerLauncher(client, this.torrentSession))
                .collect(Collectors.toList());
        synchronized (this.trackerLaunchers) {
            this.trackerLaunchers.addAll(list);
        }
    }

    /**
     * <p>查找Peer</p>
     *
     * @see TrackerLauncher#findPeer()
     */
    public void findPeer() {
        LOGGER.debug("Tracker查找Peer：{}", this.torrentSession);
        final List<TrackerLauncher> list;
        // 新建集合进行查找：防止释放资源等待
        synchronized (this.trackerLaunchers) {
            list = new ArrayList<>(this.trackerLaunchers);
        }
        list.forEach(TrackerLauncher::findPeer);
    }

    /**
     * <p>释放资源</p>
     */
    public void release() {
        LOGGER.debug("释放TrackerLauncherGroup：{}", this.torrentSession);
        synchronized (this.trackerLaunchers) {
            this.trackerLaunchers.forEach(launcher -> SystemThreadContext.submit(launcher::release));
            this.trackerLaunchers.clear();
        }
    }

}

