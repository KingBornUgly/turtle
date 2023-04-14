/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net;

import com.turtle.context.MessageHandlerContext;
import com.turtle.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * TCP消息接收代理
 * @author KingBornUgly
 * @date 2023/1/4 10:03 PM
 */
public final class TcpAcceptHandler<T extends TcpMessageHandler> implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpAcceptHandler.class);

    /**
     * <p>消息代理类型</p>
     */
    private final Class<T> clazz;
    /**
     * <p>消息代理上下文</p>
     */
    private final MessageHandlerContext context;

    /**
     * @param clazz 消息代理类型
     */
    private TcpAcceptHandler(Class<T> clazz) {
        this.clazz = clazz;
        this.context = MessageHandlerContext.getInstance();
    }

    /**
     * <p>新建TCP消息接收代理</p>
     *
     * @param <T> 消息代理类型
     *
     * @param clazz 消息代理类型
     *
     * @return TCP消息接收代理
     */
    public static final <T extends TcpMessageHandler> TcpAcceptHandler<T> newInstance(Class<T> clazz) {
        return new TcpAcceptHandler<>(clazz);
    }

    @Override
    public void completed(AsynchronousSocketChannel channel, AsynchronousServerSocketChannel server) {
        LOGGER.debug("TCP连接成功：{}", channel);
        server.accept(server, this);
        final T handler = BeanUtils.newInstance(this.clazz);
        handler.handle(channel);
        this.context.newInstance(handler);
    }

    @Override
    public void failed(Throwable throwable, AsynchronousServerSocketChannel server) {
        LOGGER.error("TCP连接异常：{}", server, throwable);
    }

}
