/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.codec;


import com.turtle.exception.NetException;
import com.turtle.net.torrent.codec.IMessageDecoder;

import java.net.InetSocketAddress;

/**
 * 消息处理器
 * 功能  实现
 * 消息解码 继承MessageCodec
 * 消息处理 实现IMessageDecoder
 * 消息编码 实现IMessageEncoder
 * @author KingBornUgly
 * @date 2023/1/4 9:01 PM
 */
public abstract class MessageCodec<I, O> implements IMessageDecoder<I>, IMessageEncoder<I> {

    /**
     * <p>消息处理器</p>
     */
    protected final IMessageDecoder<O> messageDecoder;

    /**
     * @param messageDecoder 消息处理器
     */
    protected MessageCodec(IMessageDecoder<O> messageDecoder) {
        this.messageDecoder = messageDecoder;
    }

    @Override
    public final boolean done() {
        return false;
    }

    @Override
    public final void decode(I message) throws NetException {
        this.doDecode(message, null);
    }

    @Override
    public final void decode(I message, InetSocketAddress address) throws NetException {
        this.doDecode(message, address);
    }

    /**
     * <p>消息解码</p>
     * <p>解码完成必须执行{@link #doNext(Object, InetSocketAddress)}方法</p>
     *
     * @param message 消息
     * @param address 地址
     *
     * @throws NetException 网络异常
     */
    protected abstract void doDecode(I message, InetSocketAddress address) throws NetException;

    /**
     * <p>执行消息处理器</p>
     *
     * @param message 消息
     * @param address 地址
     *
     * @throws NetException 网络异常
     */
    protected void doNext(O message, InetSocketAddress address) throws NetException {
        if(address != null) {
            if(this.messageDecoder.done()) {
                this.messageDecoder.onMessage(message, address);
            } else {
                this.messageDecoder.decode(message, address);
            }
        } else {
            if(this.messageDecoder.done()) {
                this.messageDecoder.onMessage(message);
            } else {
                this.messageDecoder.decode(message);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>消息处理请实现{@link IMessageDecoder}</p>
     */
    @Override
    public final void onMessage(I message) throws NetException {
        throw new NetException("消息处理器不能直接处理消息");
    }

    /**
     * {@inheritDoc}
     *
     * <p>消息处理请实现{@link IMessageDecoder}</p>
     */
    @Override
    public final void onMessage(I message, InetSocketAddress address) throws NetException {
        throw new NetException("消息处理器不能直接处理消息");
    }

}

