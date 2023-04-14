/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.upnp;

import com.turtle.config.SymbolConfig;
import com.turtle.exception.NetException;
import com.turtle.model.wrapper.HeaderWrapper;
import com.turtle.net.UdpClient;
import com.turtle.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * upnp客户端
 * @author KingBornUgly
 * @date 2023/1/4 9:34 PM
 */
public final class UpnpClient extends UdpClient<UpnpMessageHandler> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpnpClient.class);

    /**
     * <p>M-SEARCH协议：{@value}</p>
     */
    private static final String PROTOCOL = "M-SEARCH * HTTP/1.1";

    /**
     * @param socketAddress 地址
     */
    private UpnpClient(InetSocketAddress socketAddress) {
        super("UPNP Client", new UpnpMessageHandler(socketAddress));
    }

    public static final UpnpClient newInstance() {
        return new UpnpClient(NetUtils.buildSocketAddress(UpnpServer.upnpHost(), UpnpServer.UPNP_PORT));
    }

    @Override
    public boolean open() {
        return this.open(UpnpServer.getInstance().channel());
    }

    /**
     * <p>发送M-SEARCH消息</p>
     */
    public void mSearch() {
        LOGGER.debug("发送M-SEARCH消息");
        try {
            this.send(this.buildMSearch());
        } catch (NetException e) {
            LOGGER.error("发送M-SEARCH消息异常", e);
        }
    }

    /**
     * <p>新建M-SEARCH消息</p>
     *
     * @return 消息
     */
    private String buildMSearch() {
        final HeaderWrapper builder = HeaderWrapper.newBuilder(PROTOCOL);
        builder
                .header("HOST", SymbolConfig.Symbol.COLON.join(UpnpServer.upnpHost(), UpnpServer.UPNP_PORT))
                .header("ST", UpnpServer.UPNP_ROOT_DEVICE)
                .header("MAN", "\"ssdp:discover\"")
                .header("MX", "3");
        return builder.build();
    }

}

