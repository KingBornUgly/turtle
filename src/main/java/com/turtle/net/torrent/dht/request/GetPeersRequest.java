package com.turtle.net.torrent.dht.request;

import com.turtle.TorrentContext;
import com.turtle.config.DhtConfig;
import com.turtle.config.SystemConfig;
import com.turtle.context.NodeContext;
import com.turtle.context.PeerContext;
import com.turtle.model.session.NodeSession;
import com.turtle.model.session.PeerSession;
import com.turtle.model.session.TorrentSession;
import com.turtle.net.torrent.dht.DhtRequest;
import com.turtle.net.torrent.dht.response.GetPeersResponse;
import com.turtle.utils.CollectionUtils;
import com.turtle.utils.NetUtils;
import com.turtle.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>查找Peer</p>
 * 
 * @author turtle
 */
public final class GetPeersRequest extends DhtRequest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GetPeersRequest.class);

	private GetPeersRequest() {
		super(DhtConfig.QType.GET_PEERS);
	}
	
	/**
	 * <p>新建请求</p>
	 * 
	 * @param infoHash InfoHash
	 * 
	 * @return 请求
	 */
	public static final GetPeersRequest newRequest(byte[] infoHash) {
		final GetPeersRequest request = new GetPeersRequest();
		request.put(DhtConfig.KEY_INFO_HASH, infoHash);
		return request;
	}

	/**
	 * <p>处理请求</p>
	 * <p>能够查找到Peer返回Peer，反之返回最近的Node节点。</p>
	 * 
	 * @param request 请求
	 * 
	 * @return 响应
	 */
	public static final GetPeersResponse execute(DhtRequest request) {
		boolean needNodes = true;
		final GetPeersResponse response = GetPeersResponse.newInstance(request);
		final byte[] infoHash = request.getBytes(DhtConfig.KEY_INFO_HASH);
		final String infoHashHex = StringUtils.hex(infoHash);
		final TorrentSession torrentSession = TorrentContext.getInstance().torrentSession(infoHashHex);
		if(torrentSession != null) {
			// TODO：IPv6
			final ByteBuffer buffer = ByteBuffer.allocate(SystemConfig.IPV4_PORT_LENGTH);
			final List<PeerSession> list = PeerContext.getInstance().listPeerSession(infoHashHex);
			if(CollectionUtils.isNotEmpty(list)) {
				// 返回Peer
				needNodes = false;
				final Object values = list.stream()
					.filter(PeerSession::available)
					.filter(PeerSession::connected)
					.limit(DhtConfig.GET_PEER_SIZE)
					.map(peer -> {
						buffer.putInt(NetUtils.ipToInt(peer.host()));
						buffer.putShort(NetUtils.portToShort(peer.port()));
						buffer.flip();
						return buffer.array();
					})
					.collect(Collectors.toList());
				response.put(DhtConfig.KEY_VALUES, values);
			}
		} else {
			LOGGER.debug("查找Peer种子信息不存在：{}", infoHashHex);
		}
		if(needNodes) {
			// 返回Node
			final List<NodeSession> nodes = NodeContext.getInstance().findNode(infoHash);
			response.put(DhtConfig.KEY_NODES, serializeNodes(nodes));
		}
		return response;
	}
	
}
