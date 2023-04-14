/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent;


import com.turtle.config.SystemConfig;
import com.turtle.exception.NetException;
import com.turtle.net.IMessageSender;

import java.nio.ByteBuffer;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 5:12 PM
 */
public interface IEncryptMessageSender extends IMessageSender, IPeerConnect {

    /**
     * <p>消息加密发送</p>
     *
     * @param buffer 消息内容
     *
     * @throws NetException 网络异常
     */
    default void sendEncrypt(ByteBuffer buffer) throws NetException {
        this.sendEncrypt(buffer, SystemConfig.NONE_TIMEOUT);
    }

    /**
     * <p>消息加密发送</p>
     *
     * @param buffer 消息内容
     * @param timeout 超时时间（秒）
     *
     * @throws NetException 网络异常
     */
    void sendEncrypt(ByteBuffer buffer, int timeout) throws NetException;

}
