//package com.torrent.event.adapter;
//
//
//import com.torrent.context.GuiContext;
//import com.torrent.event.GuiEvent;
//import com.torrent.model.message.ApplicationMessage;
//
///**
// * <p>GUI显示窗口事件</p>
// *
// * @author turtle
// */
//public class ShowEventAdapter extends GuiEvent {
//
//	public ShowEventAdapter() {
//		super(Type.SHOW, "显示窗口事件");
//	}
//
//	@Override
//	protected void executeNative(Object... args) {
//		this.executeExtend(args);
//	}
//
//	@Override
//	protected void executeExtend(Object ... args) {
//		final ApplicationMessage message = ApplicationMessage.Type.SHOW.build();
//		GuiContext.getInstance().sendExtendGuiMessage(message);
//	}
//
//}
