package com.turtle.context.initializer;

import com.turtle.context.NatContext;

/**
 * <p>NAT初始化器</p>
 * 
 * @author turtle
 */
public final class NatInitializer extends Initializer {
	
	private NatInitializer() {
		super("NAT");
	}
	
	public static final NatInitializer newInstance() {
		return new NatInitializer();
	}
	
	@Override
	protected void init() {
		NatContext.getInstance().register();
	}

}
