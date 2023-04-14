/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle.model;

import com.turtle.fromat.BEncodeDecoder;
import com.turtle.utils.NetUtils;
import com.turtle.utils.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 5:44 PM
 */
public final class Torrent implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String ATTR_COMMENT = "comment";
    public static final String ATTR_COMMENT_UTF8 = "comment.utf-8";
    public static final String ATTR_ENCODING = "encoding";
    public static final String ATTR_CREATED_BY = "created by";
    public static final String ATTR_CREATION_DATE = "creation date";
    public static final String ATTR_ANNOUNCE = "announce";
    public static final String ATTR_ANNOUNCE_LIST = "announce-list";
    public static final String ATTR_INFO = "info";
    public static final String ATTR_NODES = "nodes";
    private static final int NODE_LIST_LENGTH = 2;
    private String comment;
    private String commentUtf8;
    private String encoding;
    private String createdBy;
    private Long creationDate;
    private String announce;
    private List<String> announceList;
    private TorrentInfo info;
    private Map<String, Integer> nodes;
    private transient InfoHash infoHash;

    protected Torrent() {
    }

    public static final Torrent valueOf(BEncodeDecoder decoder) {
        Objects.requireNonNull(decoder, "种子信息为空");
        Torrent torrent = new Torrent();
        String encoding = decoder.getString("encoding");
        torrent.setEncoding(encoding);
        torrent.setComment(decoder.getString("comment", encoding));
        torrent.setCommentUtf8(decoder.getString("comment.utf-8"));
        torrent.setCreatedBy(decoder.getString("created by"));
        torrent.setCreationDate(decoder.getLong("creation date"));
        torrent.setAnnounce(decoder.getString("announce"));
        torrent.setAnnounceList(readAnnounceList(decoder.getList("announce-list")));
        torrent.setInfo(TorrentInfo.valueOf(decoder.getMap("info"), encoding));
        torrent.setNodes(readNodes(decoder.getList("nodes")));
        return torrent;
    }

    public String name() {
        String name = this.info.getNameUtf8();
        if (StringUtils.isEmpty(name)) {
            name = this.info.getName();
        }

        return name;
    }

    public InfoHash infoHash() {
        return this.infoHash;
    }

    public void infoHash(InfoHash infoHash) {
        this.infoHash = infoHash;
    }

    private static final List<String> readAnnounceList(List<Object> announceList) {
        return (List)(announceList == null ? new ArrayList(0) : (List)announceList.stream().flatMap((value) -> {
            return ((List)value).stream();
        }).map(StringUtils::getString).collect(Collectors.toList()));
    }

    private static final Map<String, Integer> readNodes(List<Object> nodes) {
        return (Map)(nodes == null ? new LinkedHashMap() : (Map)nodes.stream().map(Torrent::readNode).filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> {
            return b;
        }, LinkedHashMap::new)));
    }

    private static Map.Entry<String, Integer> readNode(Object value) {
        List<?> values = (List)value;
        if (values.size() == 2) {
            String host = StringUtils.getString(values.get(0));
            if (StringUtils.isNumeric(host)) {
                host = NetUtils.intToIP(Integer.parseInt(host));
            }

            Long port = (Long)values.get(1);
            HashMap<String, Integer> hashMap = new HashMap();
            hashMap.put(host,port.intValue());
            return hashMap.entrySet().stream().findFirst().get();
        } else {
            return null;
        }
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentUtf8() {
        return this.commentUtf8;
    }

    public void setCommentUtf8(String commentUtf8) {
        this.commentUtf8 = commentUtf8;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public String getAnnounce() {
        return this.announce;
    }

    public void setAnnounce(String announce) {
        this.announce = announce;
    }

    public List<String> getAnnounceList() {
        return this.announceList;
    }

    public void setAnnounceList(List<String> announceList) {
        this.announceList = announceList;
    }

    public TorrentInfo getInfo() {
        return this.info;
    }

    public void setInfo(TorrentInfo info) {
        this.info = info;
    }

    public Map<String, Integer> getNodes() {
        return this.nodes;
    }

    public void setNodes(Map<String, Integer> nodes) {
        this.nodes = nodes;
    }
}

