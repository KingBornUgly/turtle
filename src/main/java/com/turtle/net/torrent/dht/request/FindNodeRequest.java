package com.turtle.net.torrent.dht.request;

import com.turtle.config.DhtConfig;
import com.turtle.context.NodeContext;
import com.turtle.model.session.NodeSession;
import com.turtle.net.torrent.dht.DhtRequest;
import com.turtle.net.torrent.dht.response.FindNodeResponse;

import java.util.List;

/**
 * <p>查找Node</p>
 * 
 * @author turtle
 */
public final class FindNodeRequest extends DhtRequest {

	private FindNodeRequest() {
		super(DhtConfig.QType.FIND_NODE);
	}
	
	/**
	 * <p>新建请求</p>
	 * 
	 * @param target NodeId或者InfoHash
	 * 
	 * @return 请求
	 */
	public static final FindNodeRequest newRequest(byte[] target) {
		final FindNodeRequest request = new FindNodeRequest();
		request.put(DhtConfig.KEY_TARGET, target);
		return request;
	}

	/**
	 * <p>处理请求</p>
	 * 
	 * @param request 请求
	 * 
	 * @return 响应
	 */
	public static final FindNodeResponse execute(DhtRequest request) {
		final FindNodeResponse response = FindNodeResponse.newInstance(request);
		final byte[] target = request.getBytes(DhtConfig.KEY_TARGET);
		final List<NodeSession> nodes = NodeContext.getInstance().findNode(target);
		response.put(DhtConfig.KEY_NODES, serializeNodes(nodes));
		return response;
	}
	
}
