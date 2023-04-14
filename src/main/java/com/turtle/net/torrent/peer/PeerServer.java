package com.turtle.net.torrent.peer;


import com.turtle.config.SystemConfig;
import com.turtle.net.TcpServer;

/**
 * <p>Peer服务端</p>
 * 
 * @author turtle
 */
public final class PeerServer extends TcpServer<PeerMessageHandler> {
	
	private static final PeerServer INSTANCE = new PeerServer();
	
	public static final PeerServer getInstance() {
		return INSTANCE;
	}
	
	private PeerServer() {
		super("Peer Server", PeerMessageHandler.class);
		this.listen();
	}
	
	@Override
	public boolean listen() {
		return this.listen(SystemConfig.getTorrentPort());
	}

}
