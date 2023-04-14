/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.context;

import com.turtle.IContext;
import com.turtle.config.DhtConfig;
import com.turtle.config.SystemConfig;
import com.turtle.net.torrent.dht.DhtRequest;
import com.turtle.net.torrent.dht.DhtResponse;
import com.turtle.utils.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * DHT上下文
 * @author KingBornUgly
 * @date 2023/1/4 9:10 PM
 */
public final class DhtContext implements IContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(DhtContext.class);

    private static final DhtContext INSTANCE = new DhtContext();

    public static final DhtContext getInstance() {
        return INSTANCE;
    }

    /**
     * <p>Token长度：{@value}</p>
     */
    private static final int TOKEN_LENGTH = 8;
    /**
     * <p>Token字符：{@value}</p>
     */
    private static final String TOKEN_CHARACTER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * <p>Token</p>
     */
    private final byte[] token;
    /**
     * <p>消息ID</p>
     */
    private short requestId = Short.MIN_VALUE;
    /**
     * <p>DHT请求列表</p>
     */
    private final List<DhtRequest> requests;

    private DhtContext() {
        this.token = this.buildToken();
        this.requests = new LinkedList<>();
        SystemThreadContext.timerAtFixedDelay(
                DhtConfig.DHT_REQUEST_TIMEOUT_INTERVAL,
                DhtConfig.DHT_REQUEST_TIMEOUT_INTERVAL,
                TimeUnit.MINUTES,
                this::timeout
        );
    }

    /**
     * <p>获取Token</p>
     *
     * @return Token
     */
    public byte[] token() {
        return this.token;
    }

    /**
     * <p>生成Token</p>
     *
     * @return Token
     */
    private byte[] buildToken() {
        final byte[] token = new byte[TOKEN_LENGTH];
        final byte[] bytes = TOKEN_CHARACTER.getBytes();
        final int length = bytes.length;
        final Random random = NumberUtils.random();
        for (int index = 0; index < TOKEN_LENGTH; index++) {
            token[index] = bytes[random.nextInt(length)];
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("生成Token：{}", new String(token));
        }
        return token;
    }

    /**
     * <p>生成消息ID</p>
     *
     * @return 消息ID
     */
    public byte[] buildRequestId() {
        synchronized (this) {
            return NumberUtils.shortToBytes(this.requestId++);
        }
    }

    /**
     * <p>放入请求</p>
     *
     * @param request 请求
     */
    public void request(DhtRequest request) {
        if (request == null) {
            return;
        }
        synchronized (this.requests) {
            // 删除旧的请求
            final DhtRequest oldRequest = this.remove(request.getT());
            if (oldRequest != null) {
                LOGGER.debug("删除没有收到响应DHT请求：{}", oldRequest);
            }
            this.requests.add(request);
        }
    }

    /**
     * <p>设置响应</p>
     *
     * @param response 响应
     *
     * @return 请求
     */
    public DhtRequest response(DhtResponse response) {
        if (response == null) {
            return null;
        }
        // 设置节点可用状态
        NodeContext.getInstance().available(response.getNodeId());
        DhtRequest request;
        synchronized (this.requests) {
            // 删除请求
            request = this.remove(response.getT());
        }
        if (request != null) {
            // 设置响应
            request.setResponse(response);
        }
        return request;
    }

    /**
     * <p>处理DHT超时请求</p>
     */
    private void timeout() {
        LOGGER.debug("处理DHT超时请求");
        final long timeout = SystemConfig.RECEIVE_TIMEOUT_MILLIS;
        final long timestamp = System.currentTimeMillis();
        synchronized (this.requests) {
            DhtRequest request;
            final Iterator<DhtRequest> iterator = this.requests.iterator();
            while(iterator.hasNext()) {
                request = iterator.next();
                if(timestamp - request.getTimestamp() > timeout) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * <p>删除并返回删除的请求</p>
     *
     * @param id 消息ID
     *
     * @return 请求
     */
    private DhtRequest remove(byte[] id) {
        DhtRequest request;
        final Iterator<DhtRequest> iterator = this.requests.iterator();
        while(iterator.hasNext()) {
            request = iterator.next();
            if(Arrays.equals(id, request.getT())) {
                iterator.remove();
                return request;
            }
        }
        return null;
    }

}

