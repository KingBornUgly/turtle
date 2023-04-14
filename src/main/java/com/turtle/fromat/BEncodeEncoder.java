/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle.fromat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 5:43 PM
 */
public final class BEncodeEncoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(BEncodeEncoder.class);
    private List<Object> list;
    private Map<String, Object> map;
    private BEncodeDecoder.Type type;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private BEncodeEncoder() {
    }

    public static final BEncodeEncoder newInstance() {
        return new BEncodeEncoder();
    }

    public BEncodeEncoder newList() {
        this.type = BEncodeDecoder.Type.LIST;
        this.list = new ArrayList();
        return this;
    }

    public BEncodeEncoder newMap() {
        this.type = BEncodeDecoder.Type.MAP;
        this.map = new LinkedHashMap();
        return this;
    }

    public BEncodeEncoder put(Object value) {
        if (this.type == BEncodeDecoder.Type.LIST) {
            this.list.add(value);
        }

        return this;
    }

    public BEncodeEncoder put(List<?> list) {
        if (this.type == BEncodeDecoder.Type.LIST) {
            this.list.addAll(list);
        }

        return this;
    }

    public BEncodeEncoder put(String key, Object value) {
        if (this.type == BEncodeDecoder.Type.MAP) {
            this.map.put(key, value);
        }

        return this;
    }

    public BEncodeEncoder put(Map<String, Object> map) {
        if (this.type == BEncodeDecoder.Type.MAP) {
            this.map.putAll(map);
        }

        return this;
    }

    public BEncodeEncoder flush() {
        if (this.type == BEncodeDecoder.Type.MAP) {
            this.writeMap(this.map);
        } else if (this.type == BEncodeDecoder.Type.LIST) {
            this.writeList(this.list);
        } else {
            LOGGER.warn("B编码错误（未知类型）：{}", this.type);
        }

        return this;
    }

    public BEncodeEncoder write(byte[] bytes) {
        try {
            if (bytes != null) {
                this.outputStream.write(bytes);
            }
        } catch (IOException var3) {
            LOGGER.error("B编码输出异常", var3);
        }

        return this;
    }

    private void write(char value) {
        this.outputStream.write(value);
    }

    private BEncodeEncoder writeList(List<?> list) {
        if (list == null) {
            return this;
        } else {
            this.write('l');
            list.forEach(this::writeObject);
            this.write('e');
            return this;
        }
    }

    private BEncodeEncoder writeMap(Map<?, ?> map) {
        if (map == null) {
            return this;
        } else {
            this.write('d');
            map.forEach((key, value) -> {
                this.writeObject(key);
                this.writeObject(value);
            });
            this.write('e');
            return this;
        }
    }

    private void writeObject(Object value) {
        if (value instanceof String) {
            this.writeBytes(((String) value).getBytes());
        } else if (value instanceof Number) {
            this.writeNumber((Number)value);
        } else if (value instanceof byte[]) {
            this.writeBytes((byte[]) value);
        } else if (value instanceof Map<?, ?> ) {
            this.writeMap((Map<?, ?>) value);
        } else if (value instanceof List<?>) {
            this.writeList((List<?>) value);
        } else if (value == null) {
            this.writeBytes(new byte[0]);
        } else {
            this.writeBytes(value.toString().getBytes());
        }

    }

    private void writeNumber(Number number) {
        this.write('i');
        this.write(number.toString().getBytes());
        this.write('e');
    }

    private void writeBytes(byte[] bytes) {
        this.write(String.valueOf(bytes.length).getBytes());
        this.write(':');
        this.write(bytes);
    }

    public byte[] bytes() {
        return this.outputStream.toByteArray();
    }

    public String toString() {
        return new String(this.bytes());
    }

    public static final byte[] encodeList(List<?> list) {
        return newInstance().writeList(list).bytes();
    }

    public static final String encodeListString(List<?> list) {
        return new String(encodeList(list));
    }

    public static final byte[] encodeMap(Map<?, ?> map) {
        return newInstance().writeMap(map).bytes();
    }

    public static final String encodeMapString(Map<?, ?> map) {
        return new String(encodeMap(map));
    }
}
