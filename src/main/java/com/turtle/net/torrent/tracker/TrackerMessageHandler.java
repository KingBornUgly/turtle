/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent.tracker;

import com.turtle.config.SystemConfig;
import com.turtle.config.TrackerConfig;
import com.turtle.context.TrackerContext;
import com.turtle.model.message.AnnounceMessage;
import com.turtle.model.message.ScrapeMessage;
import com.turtle.net.UdpMessageHandler;
import com.turtle.utils.ByteUtils;
import com.turtle.utils.PeerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * xxxxx
 *
 * @author KingBornUgly
 * @date 2023/1/4 6:03 PM
 */
public final class TrackerMessageHandler extends UdpMessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackerMessageHandler.class);

    /**
     * <p>连接消息最小长度：{@value}</p>
     */
    private static final int CONNECT_MIN_LENGTH = 12;
    /**
     * <p>声明消息最小长度：{@value}</p>
     */
    private static final int ANNOUNCE_MIN_LENGTH = 16;
    /**
     * <p>刮擦消息最小长度：{@value}</p>
     */
    private static final int SCRAPE_MIN_LENGTH = 16;

    /**
     * <p>服务端</p>
     */
    public TrackerMessageHandler() {
        this(null);
    }

    /**
     * <p>客户端</p>
     *
     * @param socketAddress 地址
     */
    public TrackerMessageHandler(InetSocketAddress socketAddress) {
        super(socketAddress);
    }

    @Override
    public void onReceive(ByteBuffer buffer, InetSocketAddress socketAddress) {
        final int remaining = buffer.remaining();
        if (remaining < Integer.BYTES) {
            LOGGER.warn("处理UDP Tracker消息错误（长度）：{}", remaining);
            return;
        }
        final int id = buffer.getInt();
        final TrackerConfig.Action action = TrackerConfig.Action.of(id);
        if (action == null) {
            LOGGER.warn("处理UDP Tracker消息错误（未知动作）：{}", id);
            return;
        }
        switch (action) {
            case CONNECT:
                this.doConnect(buffer);
                break;
            case ANNOUNCE:
                this.doAnnounce(buffer);
                break;
            case SCRAPE:
                this.doScrape(buffer);
                break;
            case ERROR:
                this.doError(buffer);
                break;
            default:
                LOGGER.debug("处理UDP Tracker消息错误（动作未适配）：{}", action);
        }
    }

    /**
     * <p>连接消息</p>
     *
     * @param buffer 消息
     */
    private void doConnect(ByteBuffer buffer) {
        final int remaining = buffer.remaining();
        if (remaining < CONNECT_MIN_LENGTH) {
            LOGGER.debug("处理UDP Tracker连接消息错误（长度）：{}", remaining);
            return;
        }
        final int trackerId = buffer.getInt();
        final long connectionId = buffer.getLong();
        TrackerContext.getInstance().connectionId(trackerId, connectionId);
    }

    /**
     * <p>声明消息</p>
     *
     * @param buffer 消息
     */
    private void doAnnounce(ByteBuffer buffer) {
        final int remaining = buffer.remaining();
        if (remaining < ANNOUNCE_MIN_LENGTH) {
            LOGGER.debug("处理UDP Tracker声明消息错误（长度）：{}", remaining);
            return;
        }
        final AnnounceMessage message = AnnounceMessage.newUdp(
                buffer.getInt(),
                buffer.getInt(),
                buffer.getInt(),
                buffer.getInt(),
                // 根据本地IP地址返回相同类型Peer
                SystemConfig.externalIPAddressIPv4() ? PeerUtils.readIPv4(buffer) : PeerUtils.readIPv6(buffer)
        );
        TrackerContext.getInstance().announce(message);
    }

    /**
     * <p>刮擦消息</p>
     *
     * @param buffer 消息
     */
    private void doScrape(ByteBuffer buffer) {
        final int remaining = buffer.remaining();
        if (remaining < SCRAPE_MIN_LENGTH) {
            LOGGER.debug("处理UDP Tracker刮擦消息错误（长度）：{}", remaining);
            return;
        }
        final ScrapeMessage message = ScrapeMessage.newInstance(
                buffer.getInt(),
                buffer.getInt(),
                buffer.getInt(),
                buffer.getInt()
        );
        TrackerContext.getInstance().scrape(message);
    }

    /**
     * <p>错误消息</p>
     *
     * @param buffer 消息
     */
    private void doError(ByteBuffer buffer) {
        final int remaining = buffer.remaining();
        if (remaining < Integer.BYTES) {
            LOGGER.debug("处理UDP Tracker错误消息错误（长度）：{}", remaining);
            return;
        }
        final int trackerId = buffer.getInt();
        final String message = ByteUtils.remainingToString(buffer);
        LOGGER.warn("UDP Tracker错误消息：{}-{}", trackerId, message);
    }

}

