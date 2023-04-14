//package com.torrent.event.adapter;
//
//import com.torrent.context.GuiContext;
//import com.torrent.event.GuiEvent;
//import com.torrent.model.message.ApplicationMessage;
//
///**
// * <p>GUI刷新任务状态事件</p>
// *
// * @author turtle
// */
//public class RefreshTaskStatusEventAdapter extends GuiEvent {
//
//	public RefreshTaskStatusEventAdapter() {
//		super(Type.REFRESH_TASK_STATUS, "刷新任务状态事件");
//	}
//
//	@Override
//	protected void executeNative(Object... args) {
//		this.executeExtend(args);
//	}
//
//	@Override
//	protected void executeExtend(Object ... args) {
//		final ApplicationMessage message = ApplicationMessage.Type.REFRESH_TASK_STATUS.build();
//		GuiContext.getInstance().sendExtendGuiMessage(message);
//	}
//
//}
