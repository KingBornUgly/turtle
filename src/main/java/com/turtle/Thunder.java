/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle;

import com.turtle.config.DhtConfig;
import com.turtle.config.TrackerConfig;
import com.turtle.context.EntityContext;
import com.turtle.context.NatContext;
import com.turtle.context.ProtocolContext;
import com.turtle.context.TaskContext;
import com.turtle.context.initializer.*;
import com.turtle.exception.DownloadException;
import com.turtle.model.ITaskSession;
//import com.torrent.net.application.ApplicationClient;
//import com.torrent.net.application.ApplicationServer;
import com.turtle.net.torrent.TorrentServer;
import com.turtle.net.torrent.lsd.LocalServiceDiscoveryServer;
import com.turtle.net.torrent.peer.PeerServer;
import com.turtle.net.torrent.tracker.TrackerServer;
import com.turtle.net.torrent.utp.UtpRequestQueue;
import com.turtle.protocol.Protocol;
import com.turtle.protocol.ftp.FtpProtocol;
import com.turtle.protocol.hls.HlsProtocol;
import com.turtle.protocol.http.HttpProtocol;
import com.turtle.protocol.maget.MagnetProtocol;
import com.turtle.protocol.thunder.ThunderProtocol;
import com.turtle.protocol.torrent.TorrentProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 6:12 PM
 */
public class Thunder {

    private static final Logger LOGGER = LoggerFactory.getLogger(Thunder.class);

    private static final Thunder INSTANCE = new Thunder();

    public static final Thunder getInstance() {
        return INSTANCE;
    }

    /**
     * <p>是否添加下载完成等待锁</p>
     */
    private boolean lock = false;
    /**
     * <p>是否加载已有任务</p>
     */
    private boolean buildTask = false;
    /**
     * <p>是否加载Torrent协议</p>
     */
    private boolean buildTorrent = false;
    /**
     * <p>是否启动系统监听</p>
     * <p>启动检测：开启监听失败表示已经存在系统实例（发送消息唤醒已有实例窗口）</p>
     */
    private boolean buildApplication = false;
    /**
     * <p>系统是否可用</p>
     */
    private volatile boolean available = false;

    private Thunder() {
        // 实体优先同步加载
        EntityInitializer.newInstance().sync();
        ConfigInitializer.newInstance().sync();
    }

    /**
     * <p>新建下载任务</p>
     *
     * @param url 下载链接
     *
     * @return 下载任务
     *
     * @throws DownloadException 下载异常
     *
     * @see TaskContext#download(String)
     */
    public ITaskSession download(String url) throws DownloadException {
        return TaskContext.getInstance().download(url);
    }

    /**
     * <p>添加下载完成等待锁</p>
     */
    public void lockDownload() {
        synchronized (this) {
            this.lock = true;
            while(TaskContext.getInstance().downloading()) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.debug("线程等待异常", e);
                }
            }
        }
    }

    /**
     * <p>解除下载完成等待锁</p>
     */
    public void unlockDownload() {
        if(this.lock) {
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    /**
     * <p>判断系统是否可用</p>
     *
     * @return 是否可用
     */
    public static final boolean available() {
        return INSTANCE.available;
    }

    /**
     * <p>关闭资源</p>
     */
    public static final void shutdown() {
        if(INSTANCE.available) {
            INSTANCE.available = false;
            if(INSTANCE.buildApplication) {
//                ApplicationServer.getInstance().close();
            }
            // 优先关闭任务
            TaskContext.getInstance().shutdown();
            if(INSTANCE.buildTorrent) {
                // 加载Torrent协议
                PeerServer.getInstance().close();
                TorrentServer.getInstance().close();
                TrackerServer.getInstance().close();
                LocalServiceDiscoveryServer.getInstance().close();
                NatContext.getInstance().shutdown();
                UtpRequestQueue.getInstance().shutdown();
                // 保存DHT和Tracker配置
                DhtConfig.getInstance().persistent();
                TrackerConfig.getInstance().persistent();
            }
            EntityContext.getInstance().persistent();
        }
    }

    /**
     * <p>ThunderBuilder</p>
     *
     * @author turtle
     */
    public static final class ThunderBuilder {

        /**
         * <p>创建ThunderBuilder</p>
         *
         * @return {@link Thunder.ThunderBuilder}
         */
        public static final Thunder.ThunderBuilder newBuilder() {
            return new Thunder.ThunderBuilder();
        }

        private ThunderBuilder() {
        }

        /**
         * <p>同步创建Thunder</p>
         *
         * @return {@link Thunder}
         */
        public Thunder buildSync() {
            return this.build(true);
        }

        /**
         * <p>异步创建Thunder</p>
         *
         * @return {@link Thunder}
         */
        public Thunder buildAsyn() {
            return this.build(false);
        }

        /**
         * <p>创建Thunder</p>
         *
         * @param sync 是否同步创建
         *
         * @return {@link Thunder}
         *
         * @throws DownloadException 下载异常
         */
        public synchronized Thunder build(boolean sync) {
            LOGGER.debug("创建Thunder：{}", sync);
            if(INSTANCE.available) {
                return INSTANCE;
            }
            INSTANCE.available = true;
            if(INSTANCE.buildApplication) {
//                INSTANCE.available = ApplicationServer.getInstance().listen();
            }
            if(INSTANCE.available) {
                ProtocolContext.getInstance().available(INSTANCE.available);
                this.buildInitializers().forEach(initializer -> {
                    if(sync) {
                        initializer.sync();
                    } else {
                        initializer.asyn();
                    }
                });
            }
            return INSTANCE;
        }

        /**
         * <p>加载初始化列表</p>
         *
         * @return 初始化列表
         */
        private List<Initializer> buildInitializers() {
            final List<Initializer> list = new ArrayList<>();
            if(INSTANCE.buildTorrent) {
                list.add(NatInitializer.newInstance());
                list.add(DhtInitializer.newInstance());
                list.add(TorrentInitializer.newInstance());
                list.add(TrackerInitializer.newInstance());
                list.add(LocalServiceDiscoveryInitializer.newInstance());
            }
            if(INSTANCE.buildTask) {
                list.add(TaskInitializer.newInstance());
            }
            return list;
        }

        /**
         * <p>加载已有任务</p>
         *
         * @return {@link Thunder.ThunderBuilder}
         */
        public Thunder.ThunderBuilder loadTask() {
            INSTANCE.buildTask = true;
            return this;
        }

        /**
         * <p>启动系统监听</p>
         *
         * @return {@link Thunder.ThunderBuilder}
         */
        public Thunder.ThunderBuilder application() {
            INSTANCE.buildApplication = true;
            return this;
        }

        /**
         * <p>注册下载协议</p>
         *
         * @param protocol 下载协议
         *
         * @return {@link Thunder.ThunderBuilder}
         */
        public Thunder.ThunderBuilder register(Protocol protocol) {
            ProtocolContext.getInstance().register(protocol);
            return this;
        }

        /**
         * <p>注册FTP下载协议</p>
         *
         * @return {@link Thunder.ThunderBuilder}
         */
        public Thunder.ThunderBuilder enableFtp() {
            return this.register(FtpProtocol.getInstance());
        }

        /**
         * <p>注册HLS下载协议</p>
         *
         * @return {@link Thunder.ThunderBuilder}
         */
        public Thunder.ThunderBuilder enableHls() {
            return this.register(HlsProtocol.getInstance());
        }

        /**
         * <p>注册HTTP下载协议</p>
         *
         * @return {@link Thunder.ThunderBuilder}
         */
        public Thunder.ThunderBuilder enableHttp() {
            return this.register(HttpProtocol.getInstance());
        }

        /**
         * <p>注册Magnet下载协议</p>
         *
         * @return {@link Thunder.ThunderBuilder}
         */
        public Thunder.ThunderBuilder enableMagnet() {
            INSTANCE.buildTorrent = true;
            return this.register(MagnetProtocol.getInstance());
        }

        /**
         * <p>注册Thunder下载协议</p>
         *
         * @return {@link Thunder.ThunderBuilder}
         */
        public Thunder.ThunderBuilder enableThunder() {
            return this.register(ThunderProtocol.getInstance());
        }

        /**
         * <p>注册Torrent下载协议</p>
         *
         * @return {@link Thunder.ThunderBuilder}
         */
        public Thunder.ThunderBuilder enableTorrent() {
            INSTANCE.buildTorrent = true;
            return this.register(TorrentProtocol.getInstance());
        }

        /**
         * <p>注册所有协议</p>
         *
         * @return {@link Thunder.ThunderBuilder}
         */
        public Thunder.ThunderBuilder enableAllProtocol() {
            return this
                    .enableFtp()
                    .enableHls()
                    .enableHttp()
                    .enableMagnet()
                    .enableThunder()
                    .enableTorrent();
        }

    }
}
