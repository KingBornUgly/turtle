package com.turtle.net.torrent.lsd;

import java.net.InetSocketAddress;

import com.turtle.config.PeerConfig;
import com.turtle.config.SymbolConfig;
import com.turtle.config.SystemConfig;
import com.turtle.exception.NetException;
import com.turtle.model.wrapper.HeaderWrapper;
import com.turtle.net.UdpClient;
import com.turtle.utils.ArrayUtils;
import com.turtle.utils.NetUtils;
import com.turtle.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>本地发现客户端</p>
 * 
 * @author turtle
 */
public final class LocalServiceDiscoveryClient extends UdpClient<LocalServiceDiscoveryMessageHandler> {

	private static final Logger LOGGER = LoggerFactory.getLogger(LocalServiceDiscoveryClient.class);
	
	/**
	 * <p>BT-SEARCH协议：{@value}</p>
	 */
	private static final String PROTOCOL = "BT-SEARCH * HTTP/1.1";
	
	/**
	 * @param socketAddress 地址
	 */
	private LocalServiceDiscoveryClient(InetSocketAddress socketAddress) {
		super("LSD Client", new LocalServiceDiscoveryMessageHandler(socketAddress));
	}

	/**
	 * <p>新建本地发现客户端</p>
	 */
	public static final LocalServiceDiscoveryClient newInstance() {
		return new LocalServiceDiscoveryClient(NetUtils.buildSocketAddress(LocalServiceDiscoveryServer.lsdHost(), LocalServiceDiscoveryServer.LSD_PORT));
	}

	@Override
	public boolean open() {
		return this.open(LocalServiceDiscoveryServer.getInstance().channel());
	}
	
	/**
	 * <p>发送本地发现消息</p>
	 * 
	 * @param infoHashs InfoHash数组
	 */
	public void localSearch(String ... infoHashs) {
		if(ArrayUtils.isEmpty(infoHashs)) {
			return;
		}
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("发送本地发现消息（InfoHash）：{}", SymbolConfig.Symbol.COMMA.join(infoHashs));
		}
		try {
			this.send(this.buildMessage(infoHashs));
		} catch (NetException e) {
			LOGGER.error("发送本地发现消息异常", e);
		}
	}
	
	/**
	 * <p>新建本地发现消息</p>
	 * 
	 * @param infoHashs InfoHash数组
	 * 
	 * @return 本地发现消息
	 */
	private String buildMessage(String ... infoHashs) {
		final String peerId = StringUtils.hex(PeerConfig.getInstance().peerId());
		final HeaderWrapper builder = HeaderWrapper.newBuilder(PROTOCOL);
		builder
			.header(LocalServiceDiscoveryMessageHandler.HEADER_HOST, SymbolConfig.Symbol.COLON.join(LocalServiceDiscoveryServer.lsdHost(), LocalServiceDiscoveryServer.LSD_PORT))
			.header(LocalServiceDiscoveryMessageHandler.HEADER_PORT, String.valueOf(SystemConfig.getTorrentPort()))
			.header(LocalServiceDiscoveryMessageHandler.HEADER_COOKIE, peerId);
		for (String infoHash : infoHashs) {
			builder.header(LocalServiceDiscoveryMessageHandler.HEADER_INFOHASH, infoHash);
		}
		return builder.build();
	}
	
}
