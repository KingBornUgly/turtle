/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle.model;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 5:40 PM
 */
import com.turtle.exception.DownloadException;
import com.turtle.protocol.Protocol;
import com.turtle.utils.*;

import java.io.Serializable;

public final class InfoHash implements Serializable {
    private static final long serialVersionUID = 1L;
    private int size;
    private byte[] info;
    private final byte[] infoHash;
    private final String infoHashHex;
    private final String infoHashUrl;

    private InfoHash(byte[] infoHash) {
        this.infoHash = infoHash;
        this.infoHashHex = StringUtils.hex(this.infoHash);
        this.infoHashUrl = PeerUtils.urlEncode(this.infoHash);
    }

    public static final InfoHash newInstance(byte[] data) {
        InfoHash infoHash = new InfoHash(DigestUtils.sha1(data));
        infoHash.info = data;
        infoHash.size = data.length;
        return infoHash;
    }

    public static final InfoHash newInstance(String hash) throws DownloadException {
        if (StringUtils.isEmpty(hash)) {
            throw new DownloadException("不支持的Hash：" + hash);
        } else {
            hash = hash.trim();
            if (Protocol.Type.verifyMagnetHash40(hash)) {
                return new InfoHash(StringUtils.unhex(hash));
            } else if (Protocol.Type.verifyMagnetHash32(hash)) {
                return new InfoHash(Base32Utils.decode(hash));
            } else {
                throw new DownloadException("不支持的Hash：" + hash);
            }
        }
    }

    public int size() {
        return this.size;
    }

    public void size(int size) {
        this.size = size;
    }

    public byte[] info() {
        return this.info;
    }

    public void info(byte[] info) {
        this.info = info;
    }

    public byte[] infoHash() {
        return this.infoHash;
    }

    public String infoHashHex() {
        return this.infoHashHex;
    }

    public String infoHashUrl() {
        return this.infoHashUrl;
    }

    public String toString() {
        return BeanUtils.toString(this, new Object[]{this.infoHashHex});
    }
}

