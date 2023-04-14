/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle.fromat;

import com.turtle.exception.PacketSizeException;
import com.turtle.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 5:43 PM
 */
public final class BEncodeDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(BEncodeDecoder.class);
    public static final char TYPE_E = 'e';
    public static final char TYPE_I = 'i';
    public static final char TYPE_L = 'l';
    public static final char TYPE_D = 'd';
    public static final char SEPARATOR = ':';
    private static final int MIN_CONTENT_LENGTH = 2;
    private BEncodeDecoder.Type type;
    private List<Object> list;
    private Map<String, Object> map;
    private final ByteArrayInputStream inputStream;

    private BEncodeDecoder(byte[] bytes) {
        Objects.requireNonNull(bytes, "B编码内容错误");
        if (bytes.length < 2) {
            throw new IllegalArgumentException("B编码内容错误");
        } else {
            this.inputStream = new ByteArrayInputStream(bytes);
        }
    }

    public static final BEncodeDecoder newInstance(byte[] bytes) {
        return new BEncodeDecoder(bytes);
    }

    public static final BEncodeDecoder newInstance(String content) {
        Objects.requireNonNull(content, "B编码内容错误");
        return new BEncodeDecoder(content.getBytes());
    }

    public static final BEncodeDecoder newInstance(ByteBuffer buffer) {
        Objects.requireNonNull(buffer, "B编码内容错误");
        byte[] bytes = ByteUtils.remainingToBytes(buffer);
        return new BEncodeDecoder(bytes);
    }

    public boolean isEmpty() {
        if (this.type == null) {
            return true;
        } else {
            boolean var10000;
            switch (this.type) {
                case MAP:
                    var10000 = MapUtils.isEmpty(this.map);
                    break;
                case LIST:
                    var10000 = CollectionUtils.isEmpty(this.list);
                    break;
                default:
                    var10000 = true;
            }

            return var10000;
        }
    }

    public boolean isNotEmpty() {
        return !this.isEmpty();
    }

    public BEncodeDecoder next() throws PacketSizeException {
        this.nextType();
        return this;
    }

    public BEncodeDecoder.Type nextType() throws PacketSizeException {
        boolean noneData = this.inputStream == null || this.inputStream.available() <= 0;
        if (noneData) {
            this.type = BEncodeDecoder.Type.NONE;
            return this.type;
        } else {
            char charType = (char)this.inputStream.read();
            switch (charType) {
                case 'd':
                    this.map = readMap(this.inputStream);
                    this.type = BEncodeDecoder.Type.MAP;
                    break;
                case 'l':
                    this.list = readList(this.inputStream);
                    this.type = BEncodeDecoder.Type.LIST;
                    break;
                default:
                    LOGGER.warn("B编码错误（未知类型）：{}", charType);
                    this.type = BEncodeDecoder.Type.NONE;
            }

            return this.type;
        }
    }

    public List<Object> nextList() throws PacketSizeException {
        BEncodeDecoder.Type nextType = this.nextType();
        return nextType == BEncodeDecoder.Type.LIST ? this.list : Arrays.asList();
    }

    public Map<String, Object> nextMap() throws PacketSizeException {
        BEncodeDecoder.Type nextType = this.nextType();
        return nextType == BEncodeDecoder.Type.MAP ? this.map : new HashMap<>();
    }

    public byte[] oddBytes() {
        List list = new ArrayList();
        byte[] bytes = new byte[1024];
        try {
            while(this.inputStream.read(bytes)!=-1){
                list.addAll(Arrays.asList(bytes));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] result = new byte[list.size()-1];
        int index = 0;
        Iterator it= list.iterator();
        while (it.hasNext()){
            result[index] = (byte)it.next();
            index++;
        }
        return this.inputStream == null ? new byte[0] : result;
    }



    private static final Long readLong(ByteArrayInputStream inputStream) {
        StringBuilder valueBuilder = new StringBuilder();

        int index;
        while((index = inputStream.read()) != -1) {
            char indexChar = (char)index;
            if (indexChar == 'e') {
                String number = valueBuilder.toString();
                if (!StringUtils.isNumeric(number)) {
                    throw new IllegalArgumentException("B编码错误（数值）：" + number);
                }

                return Long.valueOf(number);
            }

            valueBuilder.append(indexChar);
        }

        return 0L;
    }

    private static final List<Object> readList(ByteArrayInputStream inputStream) throws PacketSizeException {
        List<Object> list = new ArrayList();
        StringBuilder lengthBuilder = new StringBuilder();

        int index;
        while((index = inputStream.read()) != -1) {
            char indexChar = (char)index;
            switch (indexChar) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    lengthBuilder.append(indexChar);
                    break;
                case ':':
                    if (lengthBuilder.length() > 0) {
                        list.add(readBytes(lengthBuilder, inputStream));
                    } else {
                        LOGGER.warn("B编码错误（长度）：{}", lengthBuilder);
                    }
                    break;
                case ';':
                case '<':
                case '=':
                case '>':
                case '?':
                case '@':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '[':
                case '\\':
                case ']':
                case '^':
                case '_':
                case '`':
                case 'a':
                case 'b':
                case 'c':
                case 'f':
                case 'g':
                case 'h':
                case 'j':
                case 'k':
                default:
                    LOGGER.warn("B编码错误（未知类型）：{}", indexChar);
                    break;
                case 'd':
                    list.add(readMap(inputStream));
                    break;
                case 'e':
                    return list;
                case 'i':
                    list.add(readLong(inputStream));
                    break;
                case 'l':
                    list.add(readList(inputStream));
            }
        }

        return list;
    }

    private static final Map<String, Object> readMap(ByteArrayInputStream inputStream) throws PacketSizeException {
        String key = null;
        Map<String, Object> map = new LinkedHashMap();
        StringBuilder lengthBuilder = new StringBuilder();

        int index;
        while((index = inputStream.read()) != -1) {
            char indexChar = (char)index;
            switch (indexChar) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    lengthBuilder.append(indexChar);
                    break;
                case ':':
                    if (lengthBuilder.length() > 0) {
                        byte[] bytes = readBytes(lengthBuilder, inputStream);
                        if (key == null) {
                            key = new String(bytes);
                        } else {
                            map.put(key, bytes);
                            key = null;
                        }
                    } else {
                        LOGGER.warn("B编码错误（长度）：{}", lengthBuilder);
                    }
                    break;
                case ';':
                case '<':
                case '=':
                case '>':
                case '?':
                case '@':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '[':
                case '\\':
                case ']':
                case '^':
                case '_':
                case '`':
                case 'a':
                case 'b':
                case 'c':
                case 'f':
                case 'g':
                case 'h':
                case 'j':
                case 'k':
                default:
                    LOGGER.warn("B编码错误（未知类型）：{}", indexChar);
                    break;
                case 'd':
                    if (key != null) {
                        map.put(key, readMap(inputStream));
                        key = null;
                    } else {
                        LOGGER.warn("B编码key为空跳过（D）");
                    }
                    break;
                case 'e':
                    return map;
                case 'i':
                    if (key != null) {
                        map.put(key, readLong(inputStream));
                        key = null;
                    } else {
                        LOGGER.warn("B编码key为空跳过（I）");
                    }
                    break;
                case 'l':
                    if (key != null) {
                        map.put(key, readList(inputStream));
                        key = null;
                    } else {
                        LOGGER.warn("B编码key为空跳过（L）");
                    }
            }
        }

        return map;
    }

    private static final byte[] readBytes(StringBuilder lengthBuilder, ByteArrayInputStream inputStream) throws PacketSizeException {
        String number = lengthBuilder.toString();
        if (!StringUtils.isNumeric(number)) {
            throw new IllegalArgumentException("B编码错误（数值）：" + number);
        } else {
            int length = Integer.parseInt(number);
            PacketSizeException.verify(length);
            lengthBuilder.setLength(0);
            byte[] bytes = new byte[length];

            try {
                int readLength = inputStream.read(bytes);
                if (readLength != length) {
                    LOGGER.warn("B编码错误（读取长度和实际长度不符）：{}-{}", length, readLength);
                }
            } catch (IOException var6) {
                LOGGER.error("B编码读取异常", var6);
            }

            return bytes;
        }
    }

    public Object get(String key) {
        return get(this.map, key);
    }

    public static final Object get(Map<?, ?> map, String key) {
        return map == null ? null : map.get(key);
    }

    public Byte getByte(String key) {
        return getByte(this.map, key);
    }

    public static final Byte getByte(Map<?, ?> map, String key) {
        Long value = getLong(map, key);
        return value == null ? null : value.byteValue();
    }

    public Integer getInteger(String key) {
        return getInteger(this.map, key);
    }

    public static final Integer getInteger(Map<?, ?> map, String key) {
        Long value = getLong(map, key);
        return value == null ? null : value.intValue();
    }

    public Long getLong(String key) {
        return getLong(this.map, key);
    }

    public static final Long getLong(Map<?, ?> map, String key) {
        return map == null ? null : (Long)map.get(key);
    }

    public String getString(String key) {
        return getString(this.map, key);
    }

    public String getString(String key, String encoding) {
        return getString(this.map, key, encoding);
    }

    public static final String getString(Map<?, ?> map, String key) {
        return getString(map, key, (String)null);
    }

    public static final String getString(Map<?, ?> map, String key, String encoding) {
        byte[] bytes = getBytes(map, key);
        return bytes == null ? null : StringUtils.getCharsetString(bytes, encoding);
    }

    public byte[] getBytes(String key) {
        return getBytes(this.map, key);
    }

    public static final byte[] getBytes(Map<?, ?> map, String key) {
        return map == null ? null : (byte[])map.get(key);
    }

    public List<Object> getList(String key) {
        return getList(this.map, key);
    }

    public static final List<Object> getList(Map<?, ?> map, String key) {
        if (map == null) {
            return Arrays.asList();
        } else {
            List<?> result = (List)map.get(key);
            return result == null ? Arrays.asList() : (List)result.stream().collect(Collectors.toList());
        }
    }

    public Map<String, Object> getMap(String key) {
        return MapUtils.getMap(this.map, key);
    }

    public String toString() {
        return new String(this.oddBytes());
    }

    public static enum Type {
        MAP,
        LIST,
        NONE;

        private Type() {
        }
    }
}
