/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.model.message;

import com.turtle.net.torrent.tracker.TrackerLauncher;
import com.turtle.utils.BeanUtils;

/**
 * xxxxx
 *
 * @author KingBornUgly
 * @date 2023/1/4 6:24 PM
 */
public final class ScrapeMessage {
    /**
     * <p>ID</p>
     *
     * @see TrackerLauncher#id()
     */
    private Integer id;
    /**
     * <p>做种Peer数量</p>
     */
    private Integer seeder;
    /**
     * <p>完成Peer数量</p>
     */
    private Integer completed;
    /**
     * <p>下载Peer数量</p>
     */
    private Integer leecher;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSeeder() {
        return seeder;
    }

    public void setSeeder(Integer seeder) {
        this.seeder = seeder;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public Integer getLeecher() {
        return leecher;
    }

    public void setLeecher(Integer leecher) {
        this.leecher = leecher;
    }

    public ScrapeMessage(Integer id, Integer seeder, Integer completed, Integer leecher) {
        this.id = id;
        this.seeder = seeder;
        this.completed = completed;
        this.leecher = leecher;
    }

    /**
     * <p>新建Tracker刮擦响应消息</p>
     *
     * @param id        ID
     * @param seeder    做种Peer数量
     * @param completed 完成Peer数量
     * @param leecher   下载Peer数量
     * @return {@link com.turtle.TURTLE.pojo.message.ScrapeMessage}
     */
    public static final ScrapeMessage newInstance(Integer id, Integer seeder, Integer completed, Integer leecher) {
        return new ScrapeMessage(id, seeder, completed, leecher);
    }

    @Override
    public String toString() {
        return BeanUtils.toString(this);
    }

}