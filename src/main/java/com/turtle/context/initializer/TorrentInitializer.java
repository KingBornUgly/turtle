package com.turtle.context.initializer;

import com.turtle.config.PeerConfig;
import com.turtle.context.UtpContext;
import com.turtle.net.torrent.TorrentServer;
import com.turtle.net.torrent.peer.PeerServer;

/**
 * <p>Torrent初始化器</p>
 * 
 * @author turtle
 */
public final class TorrentInitializer extends Initializer {

	private TorrentInitializer() {
		super("Torrent");
	}
	
	public static final TorrentInitializer newInstance() {
		return new TorrentInitializer();
	}
	
	@Override
	protected void init() {
		PeerConfig.getInstance();
		UtpContext.getInstance();
		PeerServer.getInstance();
		TorrentServer.getInstance();
	}

}
