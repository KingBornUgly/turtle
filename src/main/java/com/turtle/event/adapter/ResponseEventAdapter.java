//package com.torrent.event.adapter;
//
//import com.torrent.context.GuiContext;
//import com.torrent.event.GuiEventArgs;
//import com.torrent.model.message.ApplicationMessage;
//
///**
// * <p>GUI响应消息事件</p>
// *
// * @author turtle
// */
//public class ResponseEventAdapter extends GuiEventArgs {
//
//	public ResponseEventAdapter() {
//		super(Type.RESPONSE, "响应消息事件");
//	}
//
//	@Override
//	protected final void executeExtend(GuiContext.Mode mode, Object... args) {
//		if(!this.check(args, 1)) {
//			return;
//		}
//		final String message = (String) this.getArg(args, 0);
//		if(mode == GuiContext.Mode.NATIVE) {
//			this.executeNativeExtend(message);
//		} else {
//			this.executeExtendExtend(message);
//		}
//	}
//
//	/**
//	 * <p>本地消息</p>
//	 *
//	 * @param message 消息
//	 */
//	protected void executeNativeExtend(String message) {
//		this.executeExtendExtend(message);
//	}
//
//	/**
//	 * <p>扩展消息</p>
//	 *
//	 * @param message 消息
//	 */
//	protected void executeExtendExtend(String message) {
//		final ApplicationMessage applicationMessage = ApplicationMessage.Type.RESPONSE.build(message);
//		GuiContext.getInstance().sendExtendGuiMessage(applicationMessage);
//	}
//
//}
