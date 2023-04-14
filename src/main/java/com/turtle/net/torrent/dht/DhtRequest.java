/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.net.torrent.dht;

import com.turtle.config.DhtConfig;
import com.turtle.config.SystemConfig;
import com.turtle.context.DhtContext;
import com.turtle.context.NodeContext;
import com.turtle.fromat.BEncodeDecoder;
import com.turtle.fromat.BEncodeEncoder;
import com.turtle.model.session.NodeSession;
import com.turtle.utils.BeanUtils;
import com.turtle.utils.CollectionUtils;
import com.turtle.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * dht请求
 * @author KingBornUgly
 * @date 2023/1/4 9:05 PM
 */
public class DhtRequest extends DhtMessage {

    private static final Logger LOGGER = LoggerFactory.getLogger(DhtRequest.class);

    /**
     * <p>请求类型</p>
     *
     * @see DhtConfig#KEY_Q
     */
    private final DhtConfig.QType q;
    /**
     * <p>请求参数</p>
     *
     * @see DhtConfig#KEY_A
     */
    private final Map<String, Object> a;
    /**
     * <p>请求时间戳</p>
     */
    private final long timestamp;
    /**
     * <p>响应</p>
     */
    private DhtResponse response;

    /**
     * <p>新建请求</p>
     *
     * @param q 请求类型
     */
    protected DhtRequest(DhtConfig.QType q) {
        this(DhtContext.getInstance().buildRequestId(), DhtConfig.KEY_Q, q, new LinkedHashMap<>());
        this.put(DhtConfig.KEY_ID, NodeContext.getInstance().nodeId());
    }

    /**
     * <p>解析请求</p>
     *
     * @param t 消息ID
     * @param y 消息类型
     * @param q 请求类型
     * @param a 请求参数
     */
    private DhtRequest(byte[] t, String y, DhtConfig.QType q, Map<String, Object> a) {
        super(t, y);
        this.q = q;
        this.a = a;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * <p>读取请求</p>
     *
     * @param decoder 消息
     *
     * @return 请求
     */
    public static final DhtRequest valueOf(final BEncodeDecoder decoder) {
        final byte[] t = decoder.getBytes(DhtConfig.KEY_T);
        final String y = decoder.getString(DhtConfig.KEY_Y);
        final DhtConfig.QType q = DhtConfig.QType.of(decoder.getString(DhtConfig.KEY_Q));
        final Map<String, Object> a = decoder.getMap(DhtConfig.KEY_A);
        return new DhtRequest(t, y, q, a);
    }

    /**
     * <p>获取请求类型</p>
     *
     * @return 请求类型
     */
    public DhtConfig.QType getQ() {
        return q;
    }

    /**
     * <p>获取请求参数</p>
     *
     * @return 请求参数
     */
    public Map<String, Object> getA() {
        return a;
    }

    /**
     * <p>获取请求时间戳</p>
     *
     * @return 请求时间戳
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * <p>获取响应</p>
     *
     * @return 响应
     */
    public DhtResponse getResponse() {
        return response;
    }

    /**
     * <p>设置响应</p>
     *
     * @param response 响应
     */
    public void setResponse(DhtResponse response) {
        this.response = response;
    }

    /**
     * <p>判断是否已经获取响应</p>
     *
     * @return 是否已经获取响应
     */
    public boolean hasResponse() {
        return this.response != null;
    }

    @Override
    public final Object get(String key) {
        if(this.a == null) {
            return null;
        }
        return this.a.get(key);
    }

    @Override
    public final void put(String key, Object value) {
        this.a.put(key, value);
    }

    @Override
    public final byte[] toBytes() {
        final Map<String, Object> request = new LinkedHashMap<>();
        request.put(DhtConfig.KEY_T, this.t);
        request.put(DhtConfig.KEY_Y, this.y);
        request.put(DhtConfig.KEY_Q, this.q.value());
        request.put(DhtConfig.KEY_A, this.a);
        return BEncodeEncoder.encodeMap(request);
    }

    /**
     * <p>序列化节点列表</p>
     *
     * @param nodes 节点列表
     *
     * @return 节点数据
     */
    protected static final byte[] serializeNodes(List<NodeSession> nodes) {
        if(CollectionUtils.isEmpty(nodes)) {
            return new byte[0];
        }
        final List<NodeSession> availableNodes = nodes.stream()
                // 分享IP地址
                .filter(node -> NetUtils.ip(node.getHost()))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(availableNodes)) {
            return new byte[0];
        }
        // TODO：IPv6
        final ByteBuffer buffer = ByteBuffer.allocate(26 * availableNodes.size());
        for (NodeSession node : availableNodes) {
            buffer.put(node.getId());
            buffer.putInt(NetUtils.ipToInt(node.getHost()));
            buffer.putShort(NetUtils.portToShort(node.getPort()));
        }
        return buffer.array();
    }

    /**
     * <p>添加响应锁</p>
     */
    public void lockResponse() {
        if(!this.hasResponse()) {
            synchronized (this) {
                if(!this.hasResponse()) {
                    try {
                        this.wait(SystemConfig.RECEIVE_TIMEOUT_MILLIS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        LOGGER.debug("线程等待异常", e);
                    }
                }
            }
        }
    }

    /**
     * <p>释放响应锁</p>
     */
    public void unlockResponse() {
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.t);
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) {
            return true;
        }
        if(object instanceof DhtRequest) {
            return Arrays.equals(this.t, ((DhtRequest) object).t);
        }
        return false;
    }

    @Override
    public String toString() {
        return BeanUtils.toString(this, this.t, this.y, this.q, this.a);
    }

}
