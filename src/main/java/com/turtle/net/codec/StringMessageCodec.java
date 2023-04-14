package com.turtle.net.codec;

import com.turtle.exception.NetException;
import com.turtle.net.torrent.codec.IMessageDecoder;
import com.turtle.utils.StringUtils;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * <p>字符串消息处理器</p>
 * 
 * @author turtle
 */
public final class StringMessageCodec extends MessageCodec<ByteBuffer, String> {

	/**
	 * @param messageDecoder 消息处理器
	 */
	public StringMessageCodec(IMessageDecoder<String> messageDecoder) {
		super(messageDecoder);
	}

	@Override
	protected void doDecode(ByteBuffer buffer, InetSocketAddress address) throws NetException {
		final String message = StringUtils.ofByteBuffer(buffer);
		this.doNext(message, address);
	}

}
