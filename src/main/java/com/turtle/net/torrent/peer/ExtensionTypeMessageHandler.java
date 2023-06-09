package com.turtle.net.torrent.peer;

import com.turtle.config.PeerConfig;
import com.turtle.exception.NetException;
import com.turtle.model.session.PeerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * <p>扩展协议类型</p>
 * 
 * @author turtle
 */
public abstract class ExtensionTypeMessageHandler implements IExtensionMessageHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionTypeMessageHandler.class);
	
	/**
	 * <p>扩展协议类型</p>
	 */
	protected final PeerConfig.ExtensionType extensionType;
	/**
	 * <p>Peer信息</p>
	 */
	protected final PeerSession peerSession;
	/**
	 * <p>扩展协议代理</p>
	 */
	protected final ExtensionMessageHandler extensionMessageHandler;
	
	/**
	 * @param extensionType 扩展协议类型
	 * @param peerSession Peer信息
	 * @param extensionMessageHandler 扩展协议代理
	 */
	protected ExtensionTypeMessageHandler(PeerConfig.ExtensionType extensionType, PeerSession peerSession, ExtensionMessageHandler extensionMessageHandler) {
		this.extensionType = extensionType;
		this.peerSession = peerSession;
		this.extensionMessageHandler = extensionMessageHandler;
	}

	@Override
	public void onMessage(ByteBuffer buffer) throws NetException {
		if(!this.supportExtensionType()) {
			LOGGER.debug("处理扩展协议消息错误（没有支持）：{}", this.extensionType);
			return;
		}
		this.doMessage(buffer);
	}
	
	/**
	 * <p>处理扩展消息</p>
	 * 
	 * @param buffer 消息
	 * 
	 * @throws NetException 网络异常
	 */
	protected abstract void doMessage(ByteBuffer buffer) throws NetException;
	
	/**
	 * <p>判断是否支持扩展协议</p>
	 * 
	 * @return 是否支持
	 */
	public boolean supportExtensionType() {
		return this.peerSession.supportExtensionType(this.extensionType);
	}
	
	/**
	 * <p>获取扩展协议ID</p>
	 * 
	 * @return 扩展协议ID
	 */
	protected Byte extensionTypeId() {
		return this.peerSession.extensionTypeId(this.extensionType);
	}

	/**
	 * <p>发送扩展消息</p>
	 * 
	 * @param buffer 扩展消息
	 */
	protected void pushMessage(ByteBuffer buffer) {
		this.pushMessage(buffer.array());
	}
	
	/**
	 * <p>发送扩展消息</p>
	 * 
	 * @param bytes 扩展消息
	 */
	protected void pushMessage(byte[] bytes) {
		this.extensionMessageHandler.pushMessage(this.extensionTypeId(), bytes);
	}
	
}
