/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent.dht.response;


import com.turtle.net.torrent.dht.DhtRequest;
import com.turtle.net.torrent.dht.DhtResponse;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 9:09 PM
 */
public final class AnnouncePeerResponse extends DhtResponse {

    /**
     * @param t 节点ID
     */
    private AnnouncePeerResponse(byte[] t) {
        super(t);
    }

    /**
     * @param response 响应
     */
    private AnnouncePeerResponse(DhtResponse response) {
        super(response.getT(), response.getY(), response.getR(), response.getE());
    }

    /**
     * <p>新建响应</p>
     *
     * @param request 请求
     *
     * @return 响应
     */
    public static final AnnouncePeerResponse newInstance(DhtRequest request) {
        return new AnnouncePeerResponse(request.getT());
    }

    /**
     * <p>新建响应</p>
     *
     * @param response 响应
     *
     * @return 响应
     */
    public static final AnnouncePeerResponse newInstance(DhtResponse response) {
        return new AnnouncePeerResponse(response);
    }

}

