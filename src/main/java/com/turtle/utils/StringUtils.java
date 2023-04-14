/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle.utils;

import com.turtle.config.SymbolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 5:46 PM
 */
public final class StringUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtils.class);
    private static final String NUMERIC_REGEX = "\\-?[0-9]+";
    private static final String DECIMAL_REGEX = "\\-?[0-9]+(\\.[0-9]+)?";
    private static final String BLANK_REGEX = "\\s";

    private StringUtils() {
    }

    public static final boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static final boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static final boolean isNumeric(String value) {
        return regex(value, "\\-?[0-9]+", true);
    }

    public static final boolean isDecimal(String value) {
        return regex(value, "\\-?[0-9]+(\\.[0-9]+)?", true);
    }

    public static final boolean startsWith(String value, String prefix) {
        return value != null && prefix != null && value.startsWith(prefix);
    }

    public static final boolean endsWith(String value, String suffix) {
        return value != null && suffix != null && value.endsWith(suffix);
    }

    public static final String hex(byte[] bytes) {
        if (bytes == null) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder();

            for(int index = 0; index < bytes.length; ++index) {
                String hex = Integer.toHexString(bytes[index] & 255);
                if (hex.length() < 2) {
                    builder.append(SymbolConfig.Symbol.ZERO.toString());
                }

                builder.append(hex);
            }

            return builder.toString().toLowerCase();
        }
    }

    public static final byte[] unhex(String content) {
        if (content == null) {
            return null;
        } else {
            int length = content.length();
            if (length % 2 != 0) {
                ++length;
                String var10000 = SymbolConfig.Symbol.ZERO.toString();
                content = var10000 + content;
            }

            int jndex = 0;
            byte[] hexBytes = new byte[length / 2];

            for(int index = 0; index < length; index += 2) {
                hexBytes[jndex] = (byte)Integer.parseInt(content.substring(index, index + 2), 16);
                ++jndex;
            }

            return hexBytes;
        }
    }

    public static final String sha1Hex(byte[] bytes) {
        return hex(DigestUtils.sha1(bytes));
    }

    public static final String charsetFrom(String value, String from) {
        return charset(value, from, (String)null);
    }

    public static final String charsetTo(String value, String to) {
        return charset(value, (String)null, to);
    }

    public static final String charset(String value, String from, String to) {
        if (isEmpty(value)) {
            return value;
        } else {
            try {
                if (from == null && to == null) {
                    return value;
                } else if (from == null) {
                    return new String(value.getBytes(), to);
                } else {
                    return to == null ? new String(value.getBytes(from)) : new String(value.getBytes(from), to);
                }
            } catch (UnsupportedEncodingException var4) {
                LOGGER.error("字符串编码转换异常：{}-{}-{}", new Object[]{value, from, to, var4});
                return value;
            }
        }
    }

    public static final boolean regex(String value, String regex, boolean ignoreCase) {
        if (value != null && regex != null) {
            Pattern pattern;
            if (ignoreCase) {
                pattern = Pattern.compile(regex, 2);
            } else {
                pattern = Pattern.compile(regex);
            }

            return pattern.matcher(value).matches();
        } else {
            return false;
        }
    }

    public static final boolean equals(String source, String target) {
        if (source == null) {
            return target == null;
        } else {
            return source.equals(target);
        }
    }

    public static final boolean equalsIgnoreCase(String source, String target) {
        if (source == null) {
            return target == null;
        } else {
            return source.equalsIgnoreCase(target);
        }
    }

    public static final byte[] toBytes(String message, String charset) {
        if (charset == null) {
            return message.getBytes();
        } else {
            try {
                return message.getBytes(charset);
            } catch (UnsupportedEncodingException var3) {
                LOGGER.error("字符编码异常:{}-{}", new Object[]{charset, message, var3});
                return message.getBytes();
            }
        }
    }

//    public static final String toUnicode(String content) {
//        StringBuilder builder = new StringBuilder();
//
//        for(int index = 0; index < content.length(); ++index) {
//            builder.append("\\u");
//            char value = content.charAt(index);
//            String hex = Integer.toHexString(value);
//            int length = hex.length();
//            if (length < 4) {
//                builder.append(SymbolConfig.Symbol.ZERO.toString().repeat(4 - length));
//            }
//
//            builder.append(Integer.toHexString(value));
//        }
//
//        return builder.toString();
//    }

    public static final String ofUnicode(String unicode) {
        String[] hex = unicode.split("\\\\u");
        StringBuilder builder = new StringBuilder();

        for(int index = 1; index < hex.length; ++index) {
            builder.append((char)Integer.parseInt(hex[index], 16));
        }

        return builder.toString();
    }

    public static final String ofByteBuffer(ByteBuffer buffer) {
        return ofByteBuffer(buffer, "UTF-8");
    }

    public static final String ofByteBuffer(ByteBuffer buffer, String charset) {
        if (buffer == null) {
            return null;
        } else {
            if (charset == null) {
                charset = "UTF-8";
            }

            if (buffer.position() != 0) {
                buffer.flip();
            }

            String content = null;
            CharsetDecoder decoder = Charset.forName(charset).newDecoder();
            decoder.onMalformedInput(CodingErrorAction.IGNORE);

            try {
                content = decoder.decode(buffer).toString();
                buffer.compact();
            } catch (CharacterCodingException var5) {
                LOGGER.error("将ByteBuffer转为字符串异常", var5);
            }

            return content;
        }
    }

    public static final String ofInputStream(InputStream input, String charset) {
        if (input == null) {
            return null;
        } else {
            if (charset == null) {
                charset = "UTF-8";
            }

            char[] chars = new char[1024];
            StringBuilder builder = new StringBuilder();

            try {
                InputStreamReader reader = new InputStreamReader(input, charset);

                int index;
                while((index = reader.read(chars)) != -1) {
                    builder.append(new String(chars, 0, index));
                }
            } catch (IOException var6) {
                LOGGER.error("将输入流转为字符串异常", var6);
            }

            return builder.toString();
        }
    }

    public static final String argValue(String arg, String key) {
        if (startsWith(arg, key)) {
            String value = arg.substring(key.length()).trim();
            String equalsSign = SymbolConfig.Symbol.EQUALS.toString();
            if (startsWith(value, equalsSign)) {
                return value.substring(equalsSign.length()).trim();
            }
        }

        return null;
    }

    public static final String getCharset(String content) {
        if (isEmpty(content)) {
            return "UTF-8";
        } else {
            CharsetEncoder gbkEncoder = Charset.forName("GBK").newEncoder();
            if (gbkEncoder.canEncode(content)) {
                return "UTF-8";
            } else {
                String gbkContent = charsetTo(content, "GBK");
                return gbkEncoder.canEncode(gbkContent) ? "GBK" : "UTF-8";
            }
        }
    }

    public static final String getString(Object object) {
        return getString(object, (String)null);
    }

    public static final String getString(Object object, String encoding) {
        if (object == null) {
            return null;
        } else if (!(object instanceof byte[])) {
            return object.toString();
        } else {
            byte[] bytes = (byte[])object;
            if (encoding != null) {
                try {
                    return new String(bytes, encoding);
                } catch (UnsupportedEncodingException var4) {
                    LOGGER.error("字符编码异常：{}", encoding, var4);
                }
            }

            return new String(bytes);
        }
    }

    public static final String getCharsetString(Object object, String encoding) {
        if (encoding != null) {
            return getString(object, encoding);
        } else {
            String utf8String = getString(object, encoding);
            String charset = getCharset(utf8String);
            return "GBK".equals(charset) ? getString(object, "GBK") : utf8String;
        }
    }

    public static final String trimAllBlank(String content) {
        return content == null ? content : content.replaceAll("\\s", "");
    }

    public static final List<String> readLines(String content) {
        return content == null ? Arrays.asList() : (List) Stream.of(content.split(SymbolConfig.Symbol.LINE_SEPARATOR.toString())).map(String::trim).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
    }

    public static String repeat(int repeat,String value){
        StringBuilder builder = new StringBuilder(value);
        for (int i=1;i<repeat;i++){
            builder.append(value);
        }
        return builder.toString();
    }
}
