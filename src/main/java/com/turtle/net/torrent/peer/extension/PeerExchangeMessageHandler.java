package com.turtle.net.torrent.peer.extension;

import com.turtle.config.PeerConfig;
import com.turtle.config.SystemConfig;
import com.turtle.context.PeerContext;
import com.turtle.exception.NetException;
import com.turtle.exception.PacketSizeException;
import com.turtle.fromat.BEncodeDecoder;
import com.turtle.fromat.BEncodeEncoder;
import com.turtle.model.session.PeerSession;
import com.turtle.model.session.TorrentSession;
import com.turtle.net.torrent.peer.ExtensionMessageHandler;
import com.turtle.net.torrent.peer.ExtensionTypeMessageHandler;
import com.turtle.utils.CollectionUtils;
import com.turtle.utils.MapUtils;
import com.turtle.utils.NetUtils;
import com.turtle.utils.PeerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Peer Exchange (PEX)</p>
 * <p>协议链接：http://www.bittorrent.org/beps/bep_0011.html</p>
 * 
 * @author turtle
 */
public final class PeerExchangeMessageHandler extends ExtensionTypeMessageHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PeerExchangeMessageHandler.class);
	
	/**
	 * <p>地址：{@value}</p>
	 */
	private static final String ADDED = "added";
	/**
	 * <p>属性：{@value}</p>
	 */
	private static final String ADDEDF = "added.f";
	/**
	 * <p>删除地址：{@value}</p>
	 */
	private static final String DROPPED = "dropped";
	/**
	 * <p>地址：{@value}</p>
	 */
	private static final String ADDED6 = "added6";
	/**
	 * <p>属性：{@value}</p>
	 */
	private static final String ADDED6F = "added6.f";
	/**
	 * <p>删除地址：{@value}</p>
	 */
	private static final String DROPPED6 = "dropped6";
	
	/**
	 * <p>BT任务信息</p>
	 */
	private final TorrentSession torrentSession;
	
	/**
	 * @param peerSession Peer信息
	 * @param torrentSession BT任务信息
	 * @param extensionMessageHandler 扩展协议代理
	 */
	private PeerExchangeMessageHandler(PeerSession peerSession, TorrentSession torrentSession, ExtensionMessageHandler extensionMessageHandler) {
		super(PeerConfig.ExtensionType.UT_PEX, peerSession, extensionMessageHandler);
		this.torrentSession = torrentSession;
	}
	
	/**
	 * <p>新建PEX扩展协议代理</p>
	 * 
	 * @param peerSession Peer
	 * @param torrentSession BT任务信息
	 * @param extensionMessageHandler 扩展消息代理
	 * 
	 * @return PEX扩展协议代理
	 */
	public static final PeerExchangeMessageHandler newInstance(PeerSession peerSession, TorrentSession torrentSession, ExtensionMessageHandler extensionMessageHandler) {
		return new PeerExchangeMessageHandler(peerSession, torrentSession, extensionMessageHandler);
	}
	
	@Override
	public void doMessage(ByteBuffer buffer) throws NetException {
		this.pex(buffer);
	}
	
	/**
	 * <p>发送消息：PEX</p>
	 * 
	 * @param bytes 消息
	 */
	public void pex(byte[] bytes) {
		LOGGER.debug("发送PEX消息");
		this.pushMessage(bytes);
	}
	
	/**
	 * <p>处理消息：PEX</p>
	 * 
	 * @param buffer 消息
	 * 
	 * @throws PacketSizeException 网络包异常
	 */
	private void pex(ByteBuffer buffer) throws PacketSizeException {
		LOGGER.debug("处理PEX消息");
		final BEncodeDecoder decoder = BEncodeDecoder.newInstance(buffer).next();
		if(decoder.isEmpty()) {
			LOGGER.warn("处理PEX消息错误（格式）：{}", decoder);
			return;
		}
		final byte[] added = decoder.getBytes(ADDED);
		final byte[] addedf = decoder.getBytes(ADDEDF);
		final Map<String, Integer> peersIPv4 = PeerUtils.readIPv4(added);
		this.readPeer(peersIPv4, addedf);
		final byte[] added6 = decoder.getBytes(ADDED6);
		final byte[] added6f = decoder.getBytes(ADDED6F);
		final Map<String, Integer> peersIPv6 = PeerUtils.readIPv6(added6);
		this.readPeer(peersIPv6, added6f);
	}
	
	/**
	 * <p>处理Peer</p>
	 * 
	 * @param peers Peer列表
	 * @param flags Peer属性
	 */
	private void readPeer(Map<String, Integer> peers, byte[] flags) {
		if(MapUtils.isNotEmpty(peers)) {
			final AtomicInteger index = new AtomicInteger(0);
			peers.forEach((host, port) -> {
				final PeerSession peerSession = PeerContext.getInstance().newPeerSession(
					this.torrentSession.infoHashHex(),
					this.torrentSession.statistics(),
					host,
					port,
					PeerConfig.Source.PEX
				);
				if(flags != null && flags.length > index.get()) {
					peerSession.flags(flags[index.getAndIncrement()]);
				}
				// 设置Pex来源
				peerSession.pexSource(this.peerSession);
			});
		}
	}
	
	/**
	 * <p>新建PEX消息</p>
	 * 
	 * @param optimize 优质Peer列表
	 * 
	 * @return 消息
	 */
	public static final byte[] buildMessage(List<PeerSession> optimize) {
		if(CollectionUtils.isEmpty(optimize)) {
			return new byte[0];
		}
		final List<PeerSession> optimizeIPv4 = new ArrayList<>();
		final List<PeerSession> optimizeIPv6 = new ArrayList<>();
		optimize.stream().distinct().forEach(session -> {
				if(NetUtils.ipv4(session.host())) {
					optimizeIPv4.add(session);
				} else {
					optimizeIPv6.add(session);
				}
			});
		// IPv4
		final ByteBuffer addedBuffer = ByteBuffer.allocate(SystemConfig.IPV4_PORT_LENGTH * optimizeIPv4.size());
		final ByteBuffer addedfBuffer = ByteBuffer.allocate(optimizeIPv4.size());
		optimizeIPv4.stream().forEach(session -> {
				addedBuffer.putInt(NetUtils.ipToInt(session.host()));
				addedBuffer.putShort(NetUtils.portToShort(session.port()));
				addedfBuffer.put(session.flags());
			});
		// IPv6
		final ByteBuffer added6Buffer = ByteBuffer.allocate(SystemConfig.IPV6_PORT_LENGTH * optimizeIPv6.size());
		final ByteBuffer added6fBuffer = ByteBuffer.allocate(optimizeIPv6.size());
		optimizeIPv6.stream().forEach(session -> {
				added6Buffer.put(NetUtils.ipToBytes(session.host()));
				added6Buffer.putShort(NetUtils.portToShort(session.port()));
				added6fBuffer.put(session.flags());
			});
		final Map<String, Object> data = new HashMap<>(9);
		final byte[] emptyBytes = new byte[0];
		data.put(ADDED, addedBuffer.array());
		data.put(ADDEDF, addedfBuffer.array());
		data.put(DROPPED, emptyBytes);
		data.put(ADDED6, added6Buffer.array());
		data.put(ADDED6F, added6fBuffer.array());
		data.put(DROPPED6, emptyBytes);
		return BEncodeEncoder.encodeMap(data);
	}

}
