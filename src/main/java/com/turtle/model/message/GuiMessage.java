//package com.torrent.model.message;
//
//
//import com.torrent.context.GuiContext;
//import com.torrent.event.GuiEventMessage;
//import com.torrent.exception.PacketSizeException;
//import com.torrent.fromat.BEncodeDecoder;
//
///**
// * <p>GUI消息</p>
// *
// * @author turtle
// *
// */
//public final class GuiMessage{
//	/**
//	 * <p>消息类型</p>
//	 */
//	private GuiContext.MessageType type;
//	/**
//	 * <p>消息标题</p>
//	 */
//	private String title;
//	/**
//	 * <p>消息内容</p>
//	 */
//	private String message;
//
//	public GuiContext.MessageType getType() {
//		return type;
//	}
//
//	public void setType(GuiContext.MessageType type) {
//		this.type = type;
//	}
//
//	public String getTitle() {
//		return title;
//	}
//
//	public void setTitle(String title) {
//		this.title = title;
//	}
//
//	public String getMessage() {
//		return message;
//	}
//
//	public void setMessage(String message) {
//		this.message = message;
//	}
//
//	public GuiMessage(GuiContext.MessageType type, String title, String message) {
//		this.type = type;
//		this.title = title;
//		this.message = message;
//	}
//
//	/**
//	 * <p>通过系统消息读取GUI消息</p>
//	 *
//	 * @param message 系统消息
//	 *
//	 * @return GUI消息
//	 *
//	 * @throws PacketSizeException 网络包大小异常
//	 */
//	public static final GuiMessage of(ApplicationMessage message) throws PacketSizeException {
//		final String body = message.getBody();
//		final BEncodeDecoder decoder = BEncodeDecoder.newInstance(body).next();
//		if(decoder.isEmpty()) {
//			return null;
//		}
//		final String type = decoder.getString(GuiEventMessage.MESSAGE_TYPE);
//		final String title = decoder.getString(GuiEventMessage.MESSAGE_TITLE);
//		final String content = decoder.getString(GuiEventMessage.MESSAGE_MESSAGE);
//		return new GuiMessage(GuiContext.MessageType.of(type), title, content);
//	}
//
//}
