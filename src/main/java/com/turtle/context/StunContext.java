/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.context;

import com.turtle.IContext;
import com.turtle.config.PeerConfig;
import com.turtle.config.StunConfig;
import com.turtle.config.SymbolConfig;
import com.turtle.config.SystemConfig;
import com.turtle.net.stun.StunClient;
import com.turtle.utils.NetUtils;
import com.turtle.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Stun上下文
 * @author KingBornUgly
 * @date 2023/1/4 9:25 PM
 */
public final class StunContext implements IContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(StunContext.class);

    private static final StunContext INSTANCE = new StunContext();

    /**
     * <p>配置多个服务器时轮询使用</p>
     */
    private int index = 0;

    public static final StunContext getInstance() {
        return INSTANCE;
    }

    private StunContext() {
    }

    /**
     * <p>端口映射</p>
     */
    public void mapping() {
        final InetSocketAddress address = this.buildServerAddress();
        if(address == null) {
            return;
        }
        StunClient.newInstance(address).mappedAddress();
    }

    /**
     * <p>设置端口映射信息</p>
     *
     * @param externalIPAddress 外网IP地址
     * @param port 外网端口
     */
    public void mapping(String externalIPAddress, int port) {
        LOGGER.debug("STUN端口映射：{}-{}", externalIPAddress, port);
        PeerConfig.nat();
        NatContext.getInstance().stun();
        SystemConfig.setExternalIPAddress(externalIPAddress);
        NodeContext.getInstance().buildNodeId(externalIPAddress);
        SystemConfig.setTorrentPortExt(port);
    }

    /**
     * <p>获取STUN服务器地址</p>
     *
     * @return STUN服务器地址
     */
    private InetSocketAddress buildServerAddress() {
        final String server = SystemConfig.getStunServer();
        if(StringUtils.isEmpty(server)) {
            LOGGER.warn("STUN服务器列表格式错误：{}", server);
            return null;
        }
        final String[] servers = server.split(SymbolConfig.Symbol.COMMA.toString());
        final int index = Math.abs(this.index++ % servers.length);
        return this.buildServerAddress(servers[index]);
    }

    /**
     * <p>获取STUN服务器地址</p>
     *
     * <table border="1">
     * 	<caption>支持格式</caption>
     * 	<tr>
     * 		<th>格式</th>
     * 	</tr>
     * 	<tr>
     * 		<td>stun1.l.google.com</td>
     * 	</tr>
     * 	<tr>
     * 		<td>stun:stun1.l.google.com</td>
     * 	</tr>
     * 	<tr>
     * 		<td>stun1.l.google.com:19302</td>
     * 	</tr>
     * 	<tr>
     * 		<td>stun:stun1.l.google.com:19302</td>
     * 	</tr>
     * </table>
     *
     * @param server STUN服务器地址
     *
     * @return STUN服务器地址
     */
    private InetSocketAddress buildServerAddress(String server) {
        LOGGER.debug("STUN服务器地址：{}", server);
        final String[] args = server.split(SymbolConfig.Symbol.COLON.toString());
        final int argLength = args.length;
        final String lastArg = args[argLength - 1];
        if(argLength == 0) {
            LOGGER.warn("STUN服务器格式错误：{}", server);
            return null;
        } else if(argLength == 1) {
            return NetUtils.buildSocketAddress(lastArg, StunConfig.DEFAULT_PORT);
        } else {
            if(StringUtils.isNumeric(lastArg)) {
                return NetUtils.buildSocketAddress(args[argLength - 2], Integer.parseInt(lastArg));
            } else {
                return NetUtils.buildSocketAddress(lastArg, StunConfig.DEFAULT_PORT);
            }
        }
    }

}

