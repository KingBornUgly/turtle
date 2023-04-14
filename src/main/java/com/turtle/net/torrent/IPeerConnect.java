/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 4:26 PM
 */
public interface IPeerConnect {

    /**
     * <p>连接类型</p>
     *
     * @author turtle
     */
    public enum ConnectType {

        /**
         * <p>UTP</p>
         */
        UTP,
        /**
         * <p>TCP</p>
         */
        TCP;

    }

    /**
     * <p>获取连接类型</p>
     *
     * @return 连接类型
     */
    IPeerConnect.ConnectType connectType();
}
