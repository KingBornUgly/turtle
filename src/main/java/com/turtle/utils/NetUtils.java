/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle.utils;

import com.turtle.config.SymbolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 5:55 PM
 */
public final class NetUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetUtils.class);
    public static final String LOCAL_HOST_NAME;
    public static final String LOCAL_HOST_ADDRESS;
    public static final short LOCAL_PREFIX_LENGTH;
    public static final String LOOPBACK_HOST_NAME;
    public static final String LOOPBACK_HOST_ADDRESS;
    public static final NetworkInterface DEFAULT_NETWORK_INTERFACE;
    public static final StandardProtocolFamily LOCAL_PROTOCOL_FAMILY;
    public static final int MAX_PORT = 65536;
    private static final String IPV4_REGEX = "(\\d{1,3}\\.){3}\\d{1,3}";
    private static final String IPV6_REGEX = "((([0-9a-f]{1,4}(:|::))|(::)){0,7}){1}(([0-9a-f]{1,4})|(\\d{1,3}\\.){3}\\d{1,3})?(/\\d{0,3})?(%.+)?";

    private NetUtils() {
    }

    public static final boolean localIPv4() {
        return LOCAL_PROTOCOL_FAMILY == StandardProtocolFamily.INET;
    }

    public static final short portToShort(int port) {
        return (short)port;
    }

    public static final int portToInt(short port) {
        return Short.toUnsignedInt(port);
    }

    public static final int ipToInt(String ip) {
        Objects.requireNonNull(ip, "IP地址不能为空");
        StringTokenizer tokenizer = new StringTokenizer(ip, SymbolConfig.Symbol.DOT.toString());
        int index = 0;

        byte[] bytes;
        for(bytes = new byte[4]; tokenizer.hasMoreTokens(); bytes[index++] = (byte)Short.parseShort(tokenizer.nextToken())) {
            if (bytes.length <= index) {
                throw new IllegalArgumentException("IP地址错误：" + ip);
            }
        }

        return NumberUtils.bytesToInt(bytes);
    }

    public static final String intToIP(int ip) {
        byte[] bytes = NumberUtils.intToBytes(ip);
        StringBuilder builder = new StringBuilder();
        builder.append(Byte.toUnsignedInt(bytes[0])).append(SymbolConfig.Symbol.DOT.toString()).append(Byte.toUnsignedInt(bytes[1])).append(SymbolConfig.Symbol.DOT.toString()).append(Byte.toUnsignedInt(bytes[2])).append(SymbolConfig.Symbol.DOT.toString()).append(Byte.toUnsignedInt(bytes[3]));
        return builder.toString();
    }

    public static final byte[] ipToBytes(String ip) {
        try {
            return InetAddress.getByName(ip).getAddress();
        } catch (UnknownHostException var2) {
            LOGGER.error("IP地址编码异常：{}", ip, var2);
            return null;
        }
    }

    public static final String bytesToIP(byte[] value) {
        if (value == null) {
            return null;
        } else {
            try {
                return InetAddress.getByAddress(value).getHostAddress();
            } catch (UnknownHostException var2) {
                LOGGER.error("IP地址解码异常", var2);
                return null;
            }
        }
    }

    public static final byte[] bufferToIPv6(ByteBuffer buffer) {
        if (buffer == null) {
            return null;
        } else {
            byte[] bytes = new byte[16];
            buffer.get(bytes);
            return bytes;
        }
    }

    public static final boolean ip(String host) {
        return ipv4(host) || ipv6(host);
    }

    public static final boolean ipv4(String host) {
        return StringUtils.regex(host, "(\\d{1,3}\\.){3}\\d{1,3}", true);
    }

    public static final boolean ipv6(String host) {
        return StringUtils.regex(host, "((([0-9a-f]{1,4}(:|::))|(::)){0,7}){1}(([0-9a-f]{1,4})|(\\d{1,3}\\.){3}\\d{1,3})?(/\\d{0,3})?(%.+)?", true);
    }

    public static final boolean lan(String host) {
        if (ip(host)) {
            byte[] bytes = ipToBytes(host);
            byte[] localHostBytes = ipToBytes(LOCAL_HOST_ADDRESS);
            int index = ArrayUtils.mismatch(bytes, localHostBytes);
            if (index == -1) {
                return true;
            } else {
                return index * 8 >= LOCAL_PREFIX_LENGTH;
            }
        } else {
            return false;
        }
    }

    public static final boolean localIP(String host) {
        InetAddress inetAddress = null;

        try {
            inetAddress = InetAddress.getByName(host);
        } catch (UnknownHostException var3) {
            LOGGER.error("IP地址转换异常：{}", host, var3);
            return true;
        }

        return inetAddress.isAnyLocalAddress() || inetAddress.isLoopbackAddress() || inetAddress.isLinkLocalAddress() || inetAddress.isMulticastAddress() || inetAddress.isSiteLocalAddress();
    }

    public static final InetSocketAddress buildSocketAddress(int port) {
        return buildSocketAddress((String)null, port);
    }

    public static final InetSocketAddress buildSocketAddress(String host, int port) {
        return StringUtils.isEmpty(host) ? new InetSocketAddress(port) : new InetSocketAddress(host, port);
    }

    private static final String buildLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException var1) {
            LOGGER.error("获取本机名称异常", var1);
            return null;
        }
    }

    private static final String buildLoopbackHostName() {
        return InetAddress.getLoopbackAddress().getHostName();
    }

    private static final String buildLoopbackHostAddress() {
        return InetAddress.getLoopbackAddress().getHostAddress();
    }

    static {
        AtomicInteger index = new AtomicInteger(Integer.MAX_VALUE);
        ModifyOptional<Short> localPrefixLength = ModifyOptional.newInstance();
        ModifyOptional<String> localHostAddress = ModifyOptional.newInstance();
        ModifyOptional<NetworkInterface> defaultNetworkInterface = ModifyOptional.newInstance();

        try {
//            NetworkInterface.networkInterfaces().forEach((networkInterface) -> {
//                int nowIndex = networkInterface.getIndex();
//                networkInterface.getInterfaceAddresses().forEach((interfaceAddress) -> {
//                    InetAddress address = interfaceAddress.getAddress();
//                    if (index.get() > nowIndex && !address.isAnyLocalAddress() && !address.isLoopbackAddress() && !address.isLinkLocalAddress() && !address.isMulticastAddress()) {
//                        index.set(nowIndex);
//                        localHostAddress.set(address.getHostAddress());
//                        localPrefixLength.set(interfaceAddress.getNetworkPrefixLength());
//                        defaultNetworkInterface.set(networkInterface);
//                    }
//
//                });
//            });
            Enumeration<NetworkInterface> networkInterfaces =  NetworkInterface.getNetworkInterfaces();
            while(networkInterfaces.hasMoreElements()){
                NetworkInterface  networkInterface= networkInterfaces.nextElement();
                int nowIndex = networkInterface.getIndex();
                networkInterface.getInterfaceAddresses().forEach((interfaceAddress) -> {
                    InetAddress address = interfaceAddress.getAddress();
                    if (index.get() > nowIndex && !address.isAnyLocalAddress() && !address.isLoopbackAddress() && !address.isLinkLocalAddress() && !address.isMulticastAddress()) {
                        index.set(nowIndex);
                        localHostAddress.set(address.getHostAddress());
                        localPrefixLength.set(interfaceAddress.getNetworkPrefixLength());
                        defaultNetworkInterface.set(networkInterface);
                    }
                });
            }
        } catch (SocketException var5) {
            LOGGER.error("初始化本机网络信息异常", var5);
        }

        LOCAL_HOST_NAME = buildLocalHostName();
        LOCAL_HOST_ADDRESS = (String)localHostAddress.get();
        LOCAL_PREFIX_LENGTH = (Short)localPrefixLength.get(Short.valueOf((short)0));
        LOOPBACK_HOST_NAME = buildLoopbackHostName();
        LOOPBACK_HOST_ADDRESS = buildLoopbackHostAddress();
        DEFAULT_NETWORK_INTERFACE = (NetworkInterface)defaultNetworkInterface.get();
        LOCAL_PROTOCOL_FAMILY = ipv4(LOCAL_HOST_ADDRESS) ? StandardProtocolFamily.INET : StandardProtocolFamily.INET6;
        LOGGER.debug("本机名称：{}", LOCAL_HOST_NAME);
        LOGGER.debug("本机地址：{}", LOCAL_HOST_ADDRESS);
        LOGGER.debug("本机子网前缀：{}", LOCAL_PREFIX_LENGTH);
        LOGGER.debug("本机环回名称：{}", LOOPBACK_HOST_NAME);
        LOGGER.debug("本机环回地址：{}", LOOPBACK_HOST_ADDRESS);
        LOGGER.debug("本机默认物理网卡：{}", DEFAULT_NETWORK_INTERFACE);
        LOGGER.debug("本机IP地址协议：{}", LOCAL_PROTOCOL_FAMILY);
    }
}
