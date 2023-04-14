/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net;


import com.turtle.exception.NetException;
import com.turtle.net.torrent.codec.IMessageDecoder;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 5:23 PM
 */
public abstract class MessageHandler<T extends Channel> implements IMessageHandler, IChannelHandler<T> {

    /**
     * <p>是否关闭</p>
     */
    protected volatile boolean close = false;
    /**
     * <p>通道</p>
     */
    protected T channel;
    /**
     * <p>消息处理器</p>
     */
    protected IMessageDecoder<ByteBuffer> messageDecoder;

    @Override
    public final boolean available() {
        return !this.close && this.channel != null && this.channel.isOpen();
    }

    @Override
    public void onReceive(ByteBuffer buffer) throws NetException {
        if(this.messageDecoder == null) {
            throw new NetException("请设置消息处理器或重新接收消息方法");
        }
        this.messageDecoder.decode(buffer);
    }

    @Override
    public void onReceive(ByteBuffer buffer, InetSocketAddress socketAddress) throws NetException {
        if(this.messageDecoder == null) {
            throw new NetException("请设置消息处理器或重新接收消息方法");
        }
        this.messageDecoder.decode(buffer, socketAddress);
    }

}

