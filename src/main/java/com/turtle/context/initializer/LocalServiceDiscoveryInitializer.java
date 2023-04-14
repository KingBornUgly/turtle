package com.turtle.context.initializer;


import com.turtle.net.torrent.lsd.LocalServiceDiscoveryServer;

/**
 * <p>本地发现初始化器</p>
 * 
 * @author turtle
 */
public final class LocalServiceDiscoveryInitializer extends Initializer {

	/**
	 * <p>延迟时间（秒）：{@value}</p>
	 */
	private static final int DELAY = 6;
	
	private LocalServiceDiscoveryInitializer() {
		super("本地发现", DELAY);
	}
	
	public static final LocalServiceDiscoveryInitializer newInstance() {
		return new LocalServiceDiscoveryInitializer();
	}
	
	@Override
	protected void init() {
		LocalServiceDiscoveryServer.getInstance();
	}

}
