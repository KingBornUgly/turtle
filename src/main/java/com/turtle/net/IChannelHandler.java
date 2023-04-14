/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net;

import java.nio.channels.Channel;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 6:05 PM
 */
public interface IChannelHandler<T extends Channel> {

    /**
     * <p>通道代理</p>
     *
     * @param channel 通道
     */
    void handle(T channel);

}
