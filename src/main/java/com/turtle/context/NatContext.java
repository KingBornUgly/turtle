/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.context;

import com.turtle.IContext;
import com.turtle.config.SystemConfig;
import com.turtle.net.upnp.UpnpClient;
import com.turtle.net.upnp.UpnpServer;
import com.turtle.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 6:14 PM
 */
public final class NatContext implements IContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(NatContext.class);

    private static final NatContext INSTANCE = new NatContext();

    public static final NatContext getInstance() {
        return INSTANCE;
    }

    /**
     * <p>内网穿透类型</p>
     *
     * @author turtle
     */
    public enum Type {

        /**
         * <p>UPNP</p>
         */
        UPNP,
        /**
         * <p>STUN</p>
         */
        STUN,
        /**
         * <p>公网IP</p>
         */
        OPEN,
        /**
         * <p>没有使用内网穿透</p>
         */
        NONE;

    }

    /**
     * <p>UPNP端口映射超时时间（毫秒）：{@value}</p>
     */
    private static final long UPNP_TIMEOUT = 4L * SystemConfig.ONE_SECOND_MILLIS;
    /**
     * <p>注册NAT服务执行周期（秒）：{@value}</p>
     */
    private static final int NAT_INTERVAL = 10;

    /**
     * <p>内网穿透类型</p>
     */
    private NatContext.Type type = NatContext.Type.NONE;
    /**
     * <p>UPNP等待锁</p>
     */
    private final Object upnpLock = new Object();

    private NatContext() {
    }

    /**
     * <p>注册NAT服务</p>
     * <p>必须外部调用不能单例注册：导致不能唤醒</p>
     * <p>公网IP地址不用穿透，优先使用UPNP进行端口映射，如果映射失败使用STUN穿透。</p>
     */
    public void register() {
        if(this.type != NatContext.Type.NONE) {
            LOGGER.debug("注册NAT服务成功：{}", this.type);
            return;
        }
        if(NetUtils.localIP(NetUtils.LOCAL_HOST_ADDRESS)) {
            UpnpClient.newInstance().mSearch();
            this.lockUpnp();
            if(UpnpContext.getInstance().useable()) {
                this.type = NatContext.Type.UPNP;
            } else {
                StunContext.getInstance().mapping();
            }
            if(this.type == NatContext.Type.NONE) {
                LOGGER.debug("注册NAT服务失败：{}", NAT_INTERVAL);
                SystemThreadContext.timer(NAT_INTERVAL, TimeUnit.SECONDS, this::register);
            } else {
                LOGGER.debug("注册NAT服务成功：{}", this.type);
            }
        } else {
            LOGGER.debug("注册NAT服务成功：已是公网IP地址");
            this.type = NatContext.Type.OPEN;
            SystemConfig.setExternalIPAddress(NetUtils.LOCAL_HOST_ADDRESS);
            NodeContext.getInstance().buildNodeId(NetUtils.LOCAL_HOST_ADDRESS);
        }
    }

    /**
     * <p>获取内网穿透类型</p>
     *
     * @return 内网穿透类型
     */
    public NatContext.Type type() {
        return this.type;
    }

    /**
     * <p>设置STUN穿透类型</p>
     */
    public void stun() {
        this.type = NatContext.Type.STUN;
    }

    /**
     * <p>关闭NAT服务</p>
     */
    public void shutdown() {
        LOGGER.debug("关闭NAT服务");
        if(this.type == NatContext.Type.UPNP) {
            UpnpContext.getInstance().release();
            UpnpServer.getInstance().close();
        }
    }

    /**
     * <p>添加UPNP等待锁</p>
     */
    private void lockUpnp() {
        synchronized (this.upnpLock) {
            try {
                this.upnpLock.wait(UPNP_TIMEOUT);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.debug("线程等待异常", e);
            }
        }
    }

    /**
     * <p>释放UPNP等待锁</p>
     */
    public void unlockUpnp() {
        synchronized (this.upnpLock) {
            this.upnpLock.notifyAll();
        }
    }

}

