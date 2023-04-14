package com.turtle.net.torrent.lsd;

import com.turtle.net.UdpAcceptHandler;
import com.turtle.net.UdpMessageHandler;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;


/**
 * <p>本地发现消息接收代理</p>
 * 
 * @author turtle
 */
public final class LocalServiceDiscoveryAcceptHandler extends UdpAcceptHandler {

	private static final LocalServiceDiscoveryAcceptHandler INSTANCE = new LocalServiceDiscoveryAcceptHandler();
	
	public static final LocalServiceDiscoveryAcceptHandler getInstance() {
		return INSTANCE;
	}
	
	/**
	 * <p>消息代理</p>
	 */
	private final LocalServiceDiscoveryMessageHandler localServiceDiscoveryMessageHandler = new LocalServiceDiscoveryMessageHandler();
	
	private LocalServiceDiscoveryAcceptHandler() {
	}
	
	@Override
	public void handle(DatagramChannel channel) {
		this.localServiceDiscoveryMessageHandler.handle(channel);
	}
	
	@Override
	public UdpMessageHandler messageHandler(ByteBuffer buffer, InetSocketAddress socketAddress) {
		return this.localServiceDiscoveryMessageHandler;
	}

}
