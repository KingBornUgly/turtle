package com.turtle.context.initializer;

import com.turtle.config.DhtConfig;
import com.turtle.context.DhtContext;
import com.turtle.context.NodeContext;

/**
 * <p>DHT初始化器</p>
 * 
 * @author turtle
 */
public final class DhtInitializer extends Initializer {

	private DhtInitializer() {
		super("DHT");
	}
	
	public static final DhtInitializer newInstance() {
		return new DhtInitializer();
	}
	
	@Override
	protected void init() {
		DhtConfig.getInstance();
		DhtContext.getInstance();
		NodeContext.getInstance();
	}

}
