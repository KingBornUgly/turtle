/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle.model;

import com.turtle.fromat.BEncodeDecoder;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 5:48 PM
 */
public final class TorrentInfo extends TorrentFileMatedata implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final byte PRIVATE_TORRENT = 1;
    public static final String ATTR_NAME = "name";
    public static final String ATTR_NAME_UTF8 = "name.utf-8";
    public static final String ATTR_PIECES = "pieces";
    public static final String ATTR_PIECE_LENGTH = "piece length";
    public static final String ATTR_PUBLISHER = "publisher";
    public static final String ATTR_PUBLISHER_UTF8 = "publisher.utf-8";
    public static final String ATTR_PUBLISHER_URL = "publisher-url";
    public static final String ATTR_PUBLISHER_URL_UTF8 = "publisher-url.utf-8";
    public static final String ATTR_PRIVATE = "private";
    public static final String ATTR_FILES = "files";
    private String name;
    private String nameUtf8;
    private byte[] pieces;
    private Long pieceLength;
    private String publisher;
    private String publisherUtf8;
    private String publisherUrl;
    private String publisherUrlUtf8;
    private Long privateTorrent;
    private List<TorrentFile> files;

    protected TorrentInfo() {
    }

    public static final TorrentInfo valueOf(Map<String, Object> map, String encoding) {
        Objects.requireNonNull(map, "文件信息为空");
        TorrentInfo info = new TorrentInfo();
        info.setName(BEncodeDecoder.getString(map, "name", encoding));
        info.setNameUtf8(BEncodeDecoder.getString(map, "name.utf-8"));
        info.setEd2k(BEncodeDecoder.getBytes(map, "ed2k"));
        info.setLength(BEncodeDecoder.getLong(map, "length"));
        info.setFilehash(BEncodeDecoder.getBytes(map, "filehash"));
        info.setPieces(BEncodeDecoder.getBytes(map, "pieces"));
        info.setPieceLength(BEncodeDecoder.getLong(map, "piece length"));
        info.setPublisher(BEncodeDecoder.getString(map, "publisher", encoding));
        info.setPublisherUtf8(BEncodeDecoder.getString(map, "publisher.utf-8"));
        info.setPublisherUrl(BEncodeDecoder.getString(map, "publisher-url", encoding));
        info.setPublisherUrlUtf8(BEncodeDecoder.getString(map, "publisher-url.utf-8"));
        info.setPrivateTorrent(BEncodeDecoder.getLong(map, "private"));
        info.setFiles(readFiles(BEncodeDecoder.getList(map, "files"), encoding));
        return info;
    }

    public int pieceSize() {
        return this.pieces.length / 20;
    }

    public boolean privateTorrent() {
        return this.privateTorrent != null && this.privateTorrent.byteValue() == 1;
    }

    public List<TorrentFile> files() {
        if (this.files.isEmpty()) {
            TorrentFile file = new TorrentFile();
            file.setEd2k(this.ed2k);
            file.setLength(this.length);
            file.setFilehash(this.filehash);
            if (this.name != null) {
                file.setPath(Arrays.asList(this.name));
            } else {
                file.setPath(new ArrayList<>());
            }

            if (this.nameUtf8 != null) {
                file.setPathUtf8(Arrays.asList(this.nameUtf8));
            } else {
                file.setPathUtf8(new ArrayList());
            }

            return Arrays.asList((file));
        } else {
            return this.files;
        }
    }

    private static final List<TorrentFile> readFiles(List<Object> files, String encoding) {
        return (List)(files == null ? new ArrayList() : (List)files.stream().map((value) -> {
            return (Map)value;
        }).map((value) -> {
            return TorrentFile.valueOf(value, encoding);
        }).collect(Collectors.toList()));
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameUtf8() {
        return this.nameUtf8;
    }

    public void setNameUtf8(String nameUtf8) {
        this.nameUtf8 = nameUtf8;
    }

    public byte[] getPieces() {
        return this.pieces;
    }

    public void setPieces(byte[] pieces) {
        this.pieces = pieces;
    }

    public Long getPieceLength() {
        return this.pieceLength;
    }

    public void setPieceLength(Long pieceLength) {
        this.pieceLength = pieceLength;
    }

    public String getPublisher() {
        return this.publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisherUtf8() {
        return this.publisherUtf8;
    }

    public void setPublisherUtf8(String publisherUtf8) {
        this.publisherUtf8 = publisherUtf8;
    }

    public String getPublisherUrl() {
        return this.publisherUrl;
    }

    public void setPublisherUrl(String publisherUrl) {
        this.publisherUrl = publisherUrl;
    }

    public String getPublisherUrlUtf8() {
        return this.publisherUrlUtf8;
    }

    public void setPublisherUrlUtf8(String publisherUrlUtf8) {
        this.publisherUrlUtf8 = publisherUrlUtf8;
    }

    public Long getPrivateTorrent() {
        return this.privateTorrent;
    }

    public void setPrivateTorrent(Long privateTorrent) {
        this.privateTorrent = privateTorrent;
    }

    public List<TorrentFile> getFiles() {
        return this.files;
    }

    public void setFiles(List<TorrentFile> files) {
        this.files = files;
    }
}

