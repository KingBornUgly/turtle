//package com.torrent.event.adapter;
//
//
//import com.torrent.context.GuiContext;
//import com.torrent.event.GuiEvent;
//import com.torrent.model.message.ApplicationMessage;
//
///**
// * <p>GUI隐藏窗口事件</p>
// *
// * @author turtle
// */
//public class HideEventAdapter extends GuiEvent {
//
//	public HideEventAdapter() {
//		super(Type.HIDE, "隐藏窗口事件");
//	}
//
//	@Override
//	protected void executeNative(Object ... args) {
//		this.executeExtend(args);
//	}
//
//	@Override
//	protected void executeExtend(Object ... args) {
//		final ApplicationMessage message = ApplicationMessage.Type.HIDE.build();
//		GuiContext.getInstance().sendExtendGuiMessage(message);
//	}
//
//}
