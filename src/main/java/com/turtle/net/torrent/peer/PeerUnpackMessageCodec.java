/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent.peer;


import com.turtle.config.PeerConfig;
import com.turtle.exception.NetException;
import com.turtle.exception.PacketSizeException;
import com.turtle.net.codec.MessageCodec;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Peer消息处理器：拆包
 * @author KingBornUgly
 * @date 2023/1/4 9:00 PM
 */
public final class PeerUnpackMessageCodec extends MessageCodec<ByteBuffer, ByteBuffer> {

    /**
     * <p>消息缓存</p>
     */
    private ByteBuffer buffer;
    /**
     * <p>消息长度</p>
     */
    private final ByteBuffer lengthStick;
    /**
     * <p>Peer消息代理</p>
     */
    private final PeerSubMessageHandler peerSubMessageHandler;

    /**
     * @param peerSubMessageHandler Peer消息代理
     */
    public PeerUnpackMessageCodec(PeerSubMessageHandler peerSubMessageHandler) {
        super(peerSubMessageHandler);
        this.lengthStick = ByteBuffer.allocate(Integer.BYTES);
        this.peerSubMessageHandler = peerSubMessageHandler;
    }

    @Override
    public void doDecode(ByteBuffer buffer, InetSocketAddress address) throws NetException {
        // 消息数据长度
        int length = 0;
        while(true) {
            if(this.buffer == null) {
                if(this.peerSubMessageHandler.handshakeRecv()) {
                    while(this.lengthStick.hasRemaining() && buffer.hasRemaining()) {
                        this.lengthStick.put(buffer.get());
                    }
                    if(this.lengthStick.hasRemaining()) {
                        // 消息长度缺失跳出
                        break;
                    } else {
                        this.lengthStick.flip();
                        length = this.lengthStick.getInt();
                        this.lengthStick.compact();
                    }
                } else {
                    // 握手消息长度
                    length = PeerConfig.HANDSHAKE_LENGTH;
                }
                // 心跳消息
                if(length <= 0) {
                    this.peerSubMessageHandler.keepAlive();
                    if(buffer.hasRemaining()) {
                        // 还有消息：继续处理
                        continue;
                    } else {
                        // 没有消息：跳出循环
                        break;
                    }
                }
                PacketSizeException.verify(length);
                this.buffer = ByteBuffer.allocate(length);
            } else {
                // 上次消息没有读取完成：计算剩余消息数据长度
                length = this.buffer.capacity() - this.buffer.position();
            }
            final int remaining = buffer.remaining();
            if(remaining > length) {
                // 包含一条完整消息：继续读取、清空缓存
                final byte[] bytes = new byte[length];
                buffer.get(bytes);
                this.buffer.put(bytes);
                this.buffer.flip();
                this.doNext(this.buffer, address);
                this.buffer = null;
            } else if(remaining == length) {
                // 刚好一条完整消息：跳出循环、清空缓存
                final byte[] bytes = new byte[length];
                buffer.get(bytes);
                this.buffer.put(bytes);
                this.buffer.flip();
                this.doNext(this.buffer, address);
                this.buffer = null;
                break;
            } else if(remaining < length) {
                // 不是一条完整消息：跳出循环、保留缓存
                final byte[] bytes = new byte[remaining];
                buffer.get(bytes);
                this.buffer.put(bytes);
                break;
            }
        }
    }

}
