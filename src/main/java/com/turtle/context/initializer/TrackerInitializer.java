package com.turtle.context.initializer;

import com.turtle.config.TrackerConfig;
import com.turtle.context.TrackerContext;
import com.turtle.exception.DownloadException;
import com.turtle.net.torrent.tracker.TrackerServer;

/**
 * <p>Tracker初始化器</p>
 * 
 * @author turtle
 */
public final class TrackerInitializer extends Initializer {

	private TrackerInitializer() {
		super("Tracker");
	}
	
	public static final TrackerInitializer newInstance() {
		return new TrackerInitializer();
	}
	
	@Override
	protected void init() throws DownloadException {
		TrackerConfig.getInstance();
		TrackerServer.getInstance();
		TrackerContext.getInstance();
	}

}
