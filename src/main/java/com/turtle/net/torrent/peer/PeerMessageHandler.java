package com.turtle.net.torrent.peer;


import com.turtle.exception.NetException;
import com.turtle.net.TcpMessageHandler;
import com.turtle.net.codec.IMessageEncoder;
import com.turtle.net.torrent.IEncryptMessageSender;

import java.nio.ByteBuffer;

/**
 * <p>Peer消息代理</p>
 * 
 * @author turtle
 */
public final class PeerMessageHandler extends TcpMessageHandler implements IEncryptMessageSender {

	/**
	 * <p>消息编码器</p>
	 */
	private final IMessageEncoder<ByteBuffer> messageEncoder;
	/**
	 * <p>Peer消息代理</p>
	 */
	private final PeerSubMessageHandler peerSubMessageHandler;
	
	/**
	 * <p>服务端</p>
	 */
	public PeerMessageHandler() {
		this(PeerSubMessageHandler.newInstance());
	}

	/**
	 * <p>客户端</p>
	 * 
	 * @param peerSubMessageHandler Peer消息代理
	 */
	public PeerMessageHandler(PeerSubMessageHandler peerSubMessageHandler) {
		peerSubMessageHandler.messageEncryptSender(this);
		final PeerUnpackMessageCodec peerUnpackMessageCodec = new PeerUnpackMessageCodec(peerSubMessageHandler);
		final PeerCryptMessageCodec peerCryptMessageCodec = new PeerCryptMessageCodec(peerUnpackMessageCodec, peerSubMessageHandler);
		this.messageDecoder = peerCryptMessageCodec;
		this.messageEncoder = peerCryptMessageCodec;
		this.peerSubMessageHandler = peerSubMessageHandler;
	}
	
	@Override
	public boolean useless() {
		return this.peerSubMessageHandler.useless();
	}
	
	@Override
	public void sendEncrypt(ByteBuffer buffer, int timeout) throws NetException {
		this.messageEncoder.encode(buffer);
		this.send(buffer, timeout);
	}
	
	@Override
	public ConnectType connectType() {
		return ConnectType.TCP;
	}

}
