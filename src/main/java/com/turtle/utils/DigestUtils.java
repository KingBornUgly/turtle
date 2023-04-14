/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 5:53 PM
 */
public final class DigestUtils {
    public static final String ALGO_MD5 = "MD5";
    public static final String ALGO_SHA1 = "SHA-1";

    private DigestUtils() {
    }

    public static final MessageDigest md5() {
        return digest("MD5");
    }

    public static final MessageDigest sha1() {
        return digest("SHA-1");
    }

    public static final MessageDigest digest(String algo) {
        try {
            return MessageDigest.getInstance(algo);
        } catch (NoSuchAlgorithmException var2) {
            throw new IllegalArgumentException("不支持的散列算法：" + algo, var2);
        }
    }

    public static final byte[] md5(byte[] bytes) {
        return md5().digest(bytes);
    }

    public static final byte[] sha1(byte[] bytes) {
        return sha1().digest(bytes);
    }
}

