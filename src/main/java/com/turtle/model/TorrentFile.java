/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle.model;


import com.turtle.config.SymbolConfig;
import com.turtle.fromat.BEncodeDecoder;
import com.turtle.utils.CollectionUtils;
import com.turtle.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 5:49 PM
 */
public final class TorrentFile extends TorrentFileMatedata implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String SEPARATOR;
    public static final String PADDING_FILE_PREFIX = "_____padding_file";
    public static final String ATTR_PATH = "path";
    public static final String ATTR_PATH_UTF8 = "path.utf-8";
    private List<String> path;
    private List<String> pathUtf8;
    private transient boolean selected = false;
    private transient boolean paddingFile = false;

    protected TorrentFile() {
    }

    public static final TorrentFile valueOf(Map<?, ?> map, String encoding) {
        Objects.requireNonNull(map, "文件信息为空");
        TorrentFile file = new TorrentFile();
        file.setEd2k(BEncodeDecoder.getBytes(map, "ed2k"));
        file.setLength(BEncodeDecoder.getLong(map, "length"));
        file.setFilehash(BEncodeDecoder.getBytes(map, "filehash"));
        List<Object> path = BEncodeDecoder.getList(map, "path");
        List<String> pathList = readPath(path, encoding);
        file.setPath(pathList);
        List<Object> pathUtf8 = BEncodeDecoder.getList(map, "path.utf-8");
        List<String> pathUtf8List = readPath(pathUtf8, "UTF-8");
        file.setPathUtf8(pathUtf8List);
        file.paddingFile = readPaddingFile(pathList, pathUtf8List);
        return file;
    }

    public boolean selected() {
        return this.selected;
    }

    public void selected(boolean selected) {
        this.selected = selected;
    }

    public String path() {
        return CollectionUtils.isNotEmpty(this.pathUtf8) ? String.join(SEPARATOR, this.pathUtf8) : String.join(SEPARATOR, this.path);
    }

    public boolean paddingFile() {
        return this.paddingFile;
    }

    public boolean notPaddingFile() {
        return !this.paddingFile();
    }

    private static final List<String> readPath(List<Object> path, String encoding) {
        return (List)(path == null ? new ArrayList() : (List)path.stream().map((value) -> {
            return StringUtils.getCharsetString(value, encoding);
        }).collect(Collectors.toList()));
    }

    private static final boolean readPaddingFile(List<String> pathList, List<String> pathUtf8List) {
        String fileName = null;
        if (CollectionUtils.isNotEmpty(pathUtf8List)) {
            fileName = (String)pathUtf8List.get(pathUtf8List.size() - 1);
        } else if (CollectionUtils.isNotEmpty(pathList)) {
            fileName = (String)pathList.get(pathList.size() - 1);
        }

        return fileName != null && fileName.startsWith("_____padding_file");
    }

    public List<String> getPath() {
        return this.path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public List<String> getPathUtf8() {
        return this.pathUtf8;
    }

    public void setPathUtf8(List<String> pathUtf8) {
        this.pathUtf8 = pathUtf8;
    }

    static {
        SEPARATOR = SymbolConfig.Symbol.SLASH.toString();
    }
}
