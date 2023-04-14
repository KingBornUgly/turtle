//package com.torrent.event.adapter;
//
//
//import com.torrent.context.GuiContext;
//import com.torrent.event.GuiEvent;
//
///**
// * <p>GUI退出窗口事件</p>
// *
// * @author turtle
// */
//public class ExitEventAdapter extends GuiEvent {
//
//	public ExitEventAdapter() {
//		super(Type.EXIT, "退出窗口事件");
//	}
//
//	@Override
//	protected void executeNative(Object ... args) {
//		this.executeExtend(args);
//	}
//
//	@Override
//	protected void executeExtend(Object ... args) {
//		GuiContext.getInstance().unlock();
//	}
//
//}
