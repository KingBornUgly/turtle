package com.turtle.context.initializer;

import com.turtle.config.DownloadConfig;
import com.turtle.config.SystemConfig;

/**
 * <p>配置初始化器</p>
 * 
 * @author turtle
 */
public final class ConfigInitializer extends Initializer {

	private ConfigInitializer() {
		super("配置");
	}
	
	public static final ConfigInitializer newInstance() {
		return new ConfigInitializer();
	}

	@Override
	protected void init() {
		SystemConfig.getInstance();
		DownloadConfig.getInstance();
	}

}
