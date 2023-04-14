package com.turtle.net.torrent.peer.extension;

import com.turtle.config.PeerConfig;
import com.turtle.config.SystemConfig;
import com.turtle.exception.NetException;
import com.turtle.fromat.BEncodeDecoder;
import com.turtle.fromat.BEncodeEncoder;
import com.turtle.model.InfoHash;
import com.turtle.model.Torrent;
import com.turtle.model.session.PeerSession;
import com.turtle.model.session.TorrentSession;
import com.turtle.net.torrent.peer.ExtensionMessageHandler;
import com.turtle.net.torrent.peer.ExtensionTypeMessageHandler;
import com.turtle.utils.DigestUtils;
import com.turtle.utils.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Extension for Peers to Send Metadata Files</p>
 * <p>协议链接：http://www.bittorrent.org/beps/bep_0009.html</p>
 * <p>InfoHash交换种子{@linkplain Torrent#getInfo() 文件信息}</p>
 *
 * @author turtle
 */
public final class MetadataMessageHandler extends ExtensionTypeMessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataMessageHandler.class);

    /**
     * <p>数据交换Slice大小：{@value}</p>
     */
    public static final int SLICE_LENGTH = 16 * SystemConfig.ONE_KB;
    /**
     * <p>Slice索引：{@value}</p>
     */
    private static final String ARG_PIECE = "piece";
    /**
     * <p>消息类型：{@value}</p>
     *
     */
    private static final String ARG_MSG_TYPE = "msg_type";
    /**
     * <p>InfoHash种子文件数据大小：{@value}</p>
     */
    private static final String ARG_TOTAL_SIZE = "total_size";

    /**
     * <p>InfoHash</p>
     */
    private final InfoHash infoHash;
    /**
     * <p>BT任务信息</p>
     */
    private final TorrentSession torrentSession;

    /**
     * @param peerSession             Peer信息
     * @param torrentSession          BT任务信息
     * @param extensionMessageHandler 扩展协议代理
     */
    private MetadataMessageHandler(PeerSession peerSession, TorrentSession torrentSession, ExtensionMessageHandler extensionMessageHandler) {
        super(PeerConfig.ExtensionType.UT_METADATA, peerSession, extensionMessageHandler);
        this.infoHash = torrentSession.infoHash();
        this.torrentSession = torrentSession;
    }

    /**
     * <p>新建Metadata扩展协议代理</p>
     *
     * @param peerSession             Peer
     * @param torrentSession          BT任务信息
     * @param extensionMessageHandler 扩展消息代理
     * @return Metadata扩展协议代理
     */
    public static final MetadataMessageHandler newInstance(PeerSession peerSession, TorrentSession torrentSession, ExtensionMessageHandler extensionMessageHandler) {
        return new MetadataMessageHandler(peerSession, torrentSession, extensionMessageHandler);
    }

    @Override
    public void doMessage(ByteBuffer buffer) throws NetException {
        final BEncodeDecoder decoder = BEncodeDecoder.newInstance(buffer).next();
        if (decoder.isEmpty()) {
            LOGGER.warn("处理metadata消息错误（格式）：{}", decoder);
            return;
        }
        final Byte typeId = decoder.getByte(ARG_MSG_TYPE);
        final PeerConfig.MetadataType metadataType = PeerConfig.MetadataType.of(typeId);
        if (metadataType == null) {
            LOGGER.warn("处理metadata消息错误（未知类型）：{}", typeId);
            return;
        }
        LOGGER.debug("处理metadata消息：{}", metadataType);
        switch (metadataType) {
            case REQUEST:
                this.request(decoder);
                break;
            case DATA:
                this.data(decoder);
                break;
            case REJECT:
                this.reject(decoder);
                break;
            default:
                LOGGER.warn("处理metadata消息错误（类型未适配）：{}", metadataType);
        }
    }

    /**
     * <p>发送消息：request</p>
     */
    public void request() {
        LOGGER.debug("发送metadata消息-request");
        final int size = this.infoHash.size();
        final int messageSize = NumberUtils.ceilDiv(size, SLICE_LENGTH);
        for (int index = 0; index < messageSize; index++) {
            final Map<String, Object> request = this.buildMessage(PeerConfig.MetadataType.REQUEST, index);
            this.pushMessage(request);
        }
    }

    /**
     * <p>处理消息：request</p>
     *
     * @param decoder 消息
     */
    private void request(BEncodeDecoder decoder) {
        LOGGER.debug("处理metadata消息-request");
        final int piece = decoder.getInteger(ARG_PIECE);
        this.data(piece);
    }

    /**
     * <p>发送消息：data</p>
     *
     * @param piece Slice索引
     */
    public void data(int piece) {
        LOGGER.debug("发送metadata消息-data：{}", piece);
        final byte[] bytes = this.infoHash.info();
        if (bytes == null) {
            this.reject();
            return;
        }
        final int pos = piece * SLICE_LENGTH;
        if (pos > bytes.length) {
            this.reject();
            return;
        }
        int length = SLICE_LENGTH;
        if (pos + SLICE_LENGTH > bytes.length) {
            length = bytes.length - pos;
        }
        // Slice数据
        final byte[] x = new byte[length];
        System.arraycopy(bytes, pos, x, 0, length);
        final Map<String, Object> data = this.buildMessage(PeerConfig.MetadataType.DATA, piece);
        data.put(ARG_TOTAL_SIZE, this.infoHash.size());
        this.pushMessage(data, x);
    }

    /**
     * <p>处理消息：data</p>
     *
     * @param decoder 消息
     */
    private void data(BEncodeDecoder decoder) {
        LOGGER.debug("处理metadata消息-data");
        byte[] bytes = this.infoHash.info();
        if (bytes == null) {
            // 设置种子Info
            final int totalSize = decoder.getInteger(ARG_TOTAL_SIZE);
            bytes = new byte[totalSize];
            this.infoHash.info(bytes);
        }
        final int piece = decoder.getInteger(ARG_PIECE);
        final int pos = piece * SLICE_LENGTH;
        if (pos > bytes.length) {
            LOGGER.warn("处理metadata消息-data失败（数据长度错误）：{}-{}", pos, bytes.length);
            return;
        }
        int length = SLICE_LENGTH;
        if (pos + SLICE_LENGTH > bytes.length) {
            length = bytes.length - pos;
        }
        // 剩余数据作为Slice数据
        final byte[] x = decoder.oddBytes();
        System.arraycopy(x, 0, bytes, pos, length);
        final byte[] sourceHash = this.infoHash.infoHash();
        final byte[] targetHash = DigestUtils.sha1(bytes);
        // 判断Hash值是否相等（相等表示已经下载完成：保存种子文件）
        if (Arrays.equals(sourceHash, targetHash)) {
            this.torrentSession.saveTorrent();
        }
    }

    /**
     * <p>发送消息：reject</p>
     */
    public void reject() {
        LOGGER.debug("发送metadata消息-reject");
        final Map<String, Object> reject = this.buildMessage(PeerConfig.MetadataType.REJECT, 0);
        this.pushMessage(reject);
    }

    /**
     * <p>处理消息：reject</p>
     *
     * @param decoder 消息
     */
    private void reject(BEncodeDecoder decoder) {
        LOGGER.debug("处理metadata消息-reject：{}", decoder);
    }

    /**
     * <p>新建消息</p>
     *
     * @param type  消息类型
     * @param piece Slice索引
     * @return 消息
     */
    private Map<String, Object> buildMessage(PeerConfig.MetadataType type, int piece) {
        final Map<String, Object> message = new LinkedHashMap<>();
        message.put(ARG_MSG_TYPE, type.id());
        message.put(ARG_PIECE, piece);
        return message;
    }

    /**
     * <p>发送消息</p>
     *
     * @param data 消息
     */
    private void pushMessage(Map<String, Object> data) {
        this.pushMessage(data, null);
    }

    /**
     * <p>发送消息</p>
     *
     * @param data 消息
     * @param x    Slice数据
     */
    private void pushMessage(Map<String, Object> data, byte[] x) {
        final BEncodeEncoder encoder = BEncodeEncoder.newInstance();
        encoder.newMap().put(data).flush();
        if (x != null) {
            encoder.write(x);
        }
        final byte[] bytes = encoder.bytes();
        this.pushMessage(bytes);
    }

}
