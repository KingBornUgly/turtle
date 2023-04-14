//package com.torrent.event.adapter;
//
//
//import com.torrent.context.GuiContext;
//import com.torrent.event.GuiEvent;
//
///**
// * <p>GUI新建窗口事件</p>
// *
// * @author turtle
// */
//public class BuildEventAdapter extends GuiEvent {
//
//	public BuildEventAdapter() {
//		super(Type.BUILD, "新建窗口事件");
//	}
//
//	@Override
//	protected void executeNative(Object ... args) {
//		this.executeExtend(args);
//	}
//
//	@Override
//	protected void executeExtend(Object ... args) {
//		GuiContext.getInstance().lock();
//	}
//
//}
