//package com.torrent.event.adapter;
//
//
//import com.torrent.context.GuiContext;
//import com.torrent.event.GuiEvent;
//import com.torrent.model.message.ApplicationMessage;
//
///**
// * <p>GUI刷新任务列表事件</p>
// *
// * @author turtle
// */
//public class RefreshTaskListEventAdapter extends GuiEvent {
//
//	public RefreshTaskListEventAdapter() {
//		super(Type.REFRESH_TASK_LIST, "刷新任务列表事件");
//	}
//
//	@Override
//	protected void executeNative(Object... args) {
//		this.executeExtend(args);
//	}
//
//	@Override
//	protected void executeExtend(Object ... args) {
//		final ApplicationMessage message = ApplicationMessage.Type.REFRESH_TASK_LIST.build();
//		GuiContext.getInstance().sendExtendGuiMessage(message);
//	}
//
//}
