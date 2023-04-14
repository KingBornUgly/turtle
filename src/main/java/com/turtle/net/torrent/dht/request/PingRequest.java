package com.turtle.net.torrent.dht.request;


import com.turtle.config.DhtConfig;
import com.turtle.net.torrent.dht.DhtRequest;
import com.turtle.net.torrent.dht.response.PingResponse;

/**
 * <p>Ping</p>
 * 
 * @author turtle
 */
public final class PingRequest extends DhtRequest {

	private PingRequest() {
		super(DhtConfig.QType.PING);
	}
	
	/**
	 * <p>新建请求</p>
	 * 
	 * @return 请求
	 */
	public static final PingRequest newRequest() {
		return new PingRequest();
	}

	/**
	 * <p>处理请求</p>
	 * 
	 * @param request 请求
	 * 
	 * @return 响应
	 */
	public static final PingResponse execute(DhtRequest request) {
		return PingResponse.newInstance(request);
	}
	
}
