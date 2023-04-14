package com.turtle.context.initializer;

import com.turtle.context.TaskContext;

/**
 * <p>任务初始化器</p>
 * 
 * @author turtle
 */
public final class TaskInitializer extends Initializer {

	private TaskInitializer() {
		super("任务");
	}
	
	public static final TaskInitializer newInstance() {
		return new TaskInitializer();
	}
	
	@Override
	protected void init() {
		TaskContext.getInstance().load();
	}

}
