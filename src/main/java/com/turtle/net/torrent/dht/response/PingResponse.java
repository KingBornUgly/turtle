package com.turtle.net.torrent.dht.response;


import com.turtle.net.torrent.dht.DhtRequest;
import com.turtle.net.torrent.dht.DhtResponse;

/**
 * <p>Ping</p>
 * 
 * @author turtle
 */
public final class PingResponse extends DhtResponse {
	
	/**
	 * @param t 节点ID
	 */
	private PingResponse(byte[] t) {
		super(t);
	}

	/**
	 * @param response 响应
	 */
	private PingResponse(DhtResponse response) {
		super(response.getT(), response.getY(), response.getR(), response.getE());
	}
	
	/**
	 * <p>新建响应</p>
	 * 
	 * @param request 请求
	 * 
	 * @return 响应
	 */
	public static final PingResponse newInstance(DhtRequest request) {
		return new PingResponse(request.getT());
	}

	/**
	 * <p>新建响应</p>
	 * 
	 * @param response 响应
	 * 
	 * @return 响应
	 */
	public static final PingResponse newInstance(DhtResponse response) {
		return new PingResponse(response);
	}
	
}
