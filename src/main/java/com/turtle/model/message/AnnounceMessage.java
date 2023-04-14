/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.model.message;

import com.turtle.net.torrent.tracker.TrackerLauncher;
import com.turtle.utils.BeanUtils;

import java.util.Map;

/**
 * xxxxx
 *
 * @author KingBornUgly
 * @date 2023/1/4 6:21 PM
 */
public final class AnnounceMessage {
    /**
     * <p>ID</p>
     *
     * @see TrackerLauncher#id()
     */
    private Integer id;
    /**
     * <p>TrackerId</p>
     * <p>HTTP Tracker使用</p>
     */
    private String trackerId;
    /**
     * <p>下次请求等待时间</p>
     */
    private Integer interval;
    /**
     * <p>做种Peer数量</p>
     */
    private Integer seeder;
    /**
     * <p>下载Peer数量</p>
     */
    private Integer leecher;
    /**
     * <p>Peer数据</p>
     * <p>IP=端口</p>
     */
    private Map<String, Integer> peers;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getSeeder() {
        return seeder;
    }

    public void setSeeder(Integer seeder) {
        this.seeder = seeder;
    }

    public Integer getLeecher() {
        return leecher;
    }

    public void setLeecher(Integer leecher) {
        this.leecher = leecher;
    }

    public Map<String, Integer> getPeers() {
        return peers;
    }

    public void setPeers(Map<String, Integer> peers) {
        this.peers = peers;
    }

    public AnnounceMessage(Integer id, String trackerId, Integer interval, Integer seeder, Integer leecher, Map<String, Integer> peers) {
        this.id = id;
        this.trackerId = trackerId;
        this.interval = interval;
        this.seeder = seeder;
        this.leecher = leecher;
        this.peers = peers;
    }

    /**
     * <p>新建UDP Tracker声明响应消息</p>
     *
     * @param id       ID
     * @param interval 下次请求等待时间
     * @param leecher  下载Peer数量
     * @param seeder   做种Peer数量
     * @param peers    Peer数据
     * @return {@link AnnounceMessage}
     */
    public static final AnnounceMessage newUdp(
            Integer id, Integer interval,
            Integer leecher, Integer seeder, Map<String, Integer> peers
    ) {
        return new AnnounceMessage(id, null, interval, seeder, leecher, peers);
    }

    /**
     * <p>新建HTTP Tracker声明响应消息</p>
     *
     * @param id          ID
     * @param trackerId   TrackerId
     * @param interval    下次请求等待时间
     * @param minInterval 下次请求等待最小时间
     * @param leecher     下载Peer数量
     * @param seeder      做种Peer数量
     * @param peers       Peer数据
     * @return {@link com.turtle.TURTLE.pojo.message.AnnounceMessage}
     */
    public static final AnnounceMessage newHttp(
            Integer id, String trackerId, Integer interval, Integer minInterval,
            Integer leecher, Integer seeder, Map<String, Integer> peers
    ) {
        if (interval != null && minInterval != null) {
            interval = Math.min(interval, minInterval);
        }
        return new AnnounceMessage(id, trackerId, interval, seeder, leecher, peers);
    }

    @Override
    public String toString() {
        return BeanUtils.toString(this);
    }

}
