/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle.model;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 5:52 PM
 */
public abstract class TorrentFileMatedata {
    public static final String ATTR_ED2K = "ed2k";
    public static final String ATTR_LENGTH = "length";
    public static final String ATTR_FILEHASH = "filehash";
    protected byte[] ed2k;
    protected Long length;
    protected byte[] filehash;

    public TorrentFileMatedata() {
    }

    public byte[] getEd2k() {
        return this.ed2k;
    }

    public void setEd2k(byte[] ed2k) {
        this.ed2k = ed2k;
    }

    public Long getLength() {
        return this.length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public byte[] getFilehash() {
        return this.filehash;
    }

    public void setFilehash(byte[] filehash) {
        this.filehash = filehash;
    }
}

