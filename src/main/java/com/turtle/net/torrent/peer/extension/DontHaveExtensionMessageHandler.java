package com.turtle.net.torrent.peer.extension;

import com.turtle.config.PeerConfig;
import com.turtle.model.session.PeerSession;
import com.turtle.net.torrent.peer.ExtensionMessageHandler;
import com.turtle.net.torrent.peer.ExtensionTypeMessageHandler;
import com.turtle.utils.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * <p>The lt_donthave extension</p>
 * <p>协议链接：http://bittorrent.org/beps/bep_0054.html</p>
 * <p>不再含有某个Piece</p>
 * 
 * @author turtle
 */
public final class DontHaveExtensionMessageHandler extends ExtensionTypeMessageHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DontHaveExtensionMessageHandler.class);
	
	/**
	 * @param peerSession Peer信息
	 * @param extensionMessageHandler 扩展协议代理
	 */
	private DontHaveExtensionMessageHandler(PeerSession peerSession, ExtensionMessageHandler extensionMessageHandler) {
		super(PeerConfig.ExtensionType.LT_DONTHAVE, peerSession, extensionMessageHandler);
	}

	/**
	 * <p>新建dontHave扩展协议代理</p>
	 * 
	 * @param peerSession Peer信息
	 * @param extensionMessageHandler 扩展协议代理
	 * 
	 * @return dontHave扩展协议代理
	 */
	public static final DontHaveExtensionMessageHandler newInstance(PeerSession peerSession, ExtensionMessageHandler extensionMessageHandler) {
		return new DontHaveExtensionMessageHandler(peerSession, extensionMessageHandler);
	}
	
	@Override
	protected void doMessage(ByteBuffer buffer) {
		this.dontHave(buffer);
	}
	
	/**
	 * <p>发送dontHave消息</p>
	 * 
	 * @param index Piece索引
	 */
	public void dontHave(int index) {
		LOGGER.debug("发送dontHave消息：{}", index);
		final byte[] bytes = NumberUtils.intToBytes(index);
		this.pushMessage(bytes);
	}
	
	/**
	 * <p>处理dontHave消息</p>
	 * 
	 * @param buffer 消息
	 */
	private void dontHave(ByteBuffer buffer) {
		final int index = buffer.getInt();
		LOGGER.debug("处理dontHave消息：{}", index);
		this.peerSession.pieceOff(index);
	}

}
