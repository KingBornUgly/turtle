/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent.peer;

import com.turtle.exception.NetException;

import java.nio.ByteBuffer;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 4:51 PM
 */
public interface IExtensionMessageHandler {

    /**
     * <p>处理扩展消息</p>
     *
     * @param buffer 消息
     *
     * @throws NetException 网络异常
     */
    void onMessage(ByteBuffer buffer) throws NetException;

}
