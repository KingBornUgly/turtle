package com.turtle.net.torrent.utp;


import com.turtle.exception.NetException;
import com.turtle.net.torrent.codec.IMessageDecoder;

import java.nio.ByteBuffer;

/**
 * <p>UTP请求</p>
 *
 * @author turtle
 */
public final class UtpRequest {
    /**
     * <p>请求数据</p>
     */
    private ByteBuffer buffer;
    /**
     * <p>消息处理器</p>
     */
    private IMessageDecoder<ByteBuffer> messageDecoder;

	public UtpRequest(ByteBuffer buffer, IMessageDecoder<ByteBuffer> messageDecoder) {
		this.buffer = buffer;
		this.messageDecoder = messageDecoder;
	}

	/**
     * <p>新建UTP请求</p>
     *
     * @param buffer         请求数据
     * @param messageDecoder 消息处理器
     * @return {@link UtpRequest}
     */
    public static final UtpRequest newInstance(ByteBuffer buffer,
                                               IMessageDecoder<ByteBuffer> messageDecoder) {
        return new

                UtpRequest(buffer, messageDecoder);

    }

    /**
     * <p>处理请求</p>
     *
     * @throws NetException 网络异常
     */
    public void execute() throws NetException {
        this.messageDecoder.decode(this.buffer);
    }

}
