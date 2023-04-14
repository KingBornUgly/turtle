package com.turtle.net.torrent.dht.response;


import com.turtle.config.DhtConfig;
import com.turtle.model.session.NodeSession;
import com.turtle.net.torrent.dht.DhtRequest;
import com.turtle.net.torrent.dht.DhtResponse;

import java.util.List;

/**
 * <p>查找Node</p>
 * 
 * @author turtle
 */
public final class FindNodeResponse extends DhtResponse {

	/**
	 * @param t 节点ID
	 */
	private FindNodeResponse(byte[] t) {
		super(t);
	}
	
	/**
	 * @param response 响应
	 */
	private FindNodeResponse(DhtResponse response) {
		super(response.getT(), response.getY(), response.getR(), response.getE());
	}

	/**
	 * <p>新建响应</p>
	 * 
	 * @param request 请求
	 * 
	 * @return 响应
	 */
	public static final FindNodeResponse newInstance(DhtRequest request) {
		return new FindNodeResponse(request.getT());
	}
	
	/**
	 * <p>新建响应</p>
	 * 
	 * @param response 响应
	 * 
	 * @return 响应
	 */
	public static final FindNodeResponse newInstance(DhtResponse response) {
		return new FindNodeResponse(response);
	}
	
	/**
	 * <p>获取节点列表</p>
	 * 
	 * @return 节点列表
	 */
	public List<NodeSession> getNodes() {
		return this.deserializeNodes(DhtConfig.KEY_NODES);
	}
	
}
