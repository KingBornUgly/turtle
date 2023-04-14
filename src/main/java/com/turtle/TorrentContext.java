/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle;

import com.turtle.exception.DownloadException;
import com.turtle.exception.NetException;
import com.turtle.fromat.BEncodeDecoder;
import com.turtle.fromat.BEncodeEncoder;
import com.turtle.model.InfoHash;
import com.turtle.model.Torrent;
import com.turtle.model.session.TorrentSession;
import com.turtle.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 5:38 PM
 */
public final class TorrentContext implements IContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(TorrentContext.class);
    private static final TorrentContext INSTANCE = new TorrentContext();
    private final Map<String, TorrentSession> torrentSessions = new ConcurrentHashMap();

    public static final TorrentContext getInstance() {
        return INSTANCE;
    }

    private TorrentContext() {
    }

    public List<InfoHash> allInfoHash() {
        return (List)this.torrentSessions.values().stream().map(TorrentSession::infoHash).collect(Collectors.toList());
    }

    public List<TorrentSession> allTorrentSession() {
        return (List)this.torrentSessions.values().stream().collect(Collectors.toList());
    }

    public TorrentSession torrentSession(String infoHashHex) {
        return (TorrentSession)this.torrentSessions.get(infoHashHex);
    }

    public TorrentSession remove(String infoHashHex) {
        LOGGER.debug("删除BT任务信息：{}", infoHashHex);
        return (TorrentSession)this.torrentSessions.remove(infoHashHex);
    }

    public boolean exist(String infoHashHex) {
        return this.torrentSessions.containsKey(infoHashHex);
    }

    public TorrentSession newTorrentSession(String path) throws DownloadException {
        Torrent torrent = loadTorrent(path);
        return this.newTorrentSession(torrent.infoHash(), torrent);
    }

    public TorrentSession newTorrentSession(String infoHashHex, String path) throws DownloadException {
        TorrentSession session = this.torrentSession(infoHashHex);
        if (session != null) {
            return session;
        } else {
            return StringUtils.isEmpty(path) ? this.newTorrentSession((InfoHash)InfoHash.newInstance(infoHashHex), (Torrent)null) : this.newTorrentSession(path);
        }
    }

    private TorrentSession newTorrentSession(InfoHash infoHash, Torrent torrent) throws DownloadException {
        if (infoHash == null) {
            throw new DownloadException("新建TorrentSession失败（InfoHash为空）");
        } else {
            String infoHashHex = infoHash.infoHashHex();
            TorrentSession torrentSession = this.torrentSession(infoHashHex);
            if (torrentSession == null) {
                torrentSession = TorrentSession.newInstance(infoHash, torrent);
                this.torrentSessions.put(infoHashHex, torrentSession);
            }

            return torrentSession;
        }
    }

    public static final Torrent loadTorrent(String path) throws DownloadException {
        File file = new File(path);
        if (!file.exists()) {
            throw new DownloadException("不存在的种子文件");
        } else {
            try {
                byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                BEncodeDecoder decoder = BEncodeDecoder.newInstance(bytes).next();
                if (decoder.isEmpty()) {
                    throw new DownloadException("种子文件格式错误");
                } else {
                    Torrent torrent = Torrent.valueOf(decoder);
                    Map<String, Object> info = decoder.getMap("info");
                    InfoHash infoHash = InfoHash.newInstance(BEncodeEncoder.encodeMap(info));
                    torrent.infoHash(infoHash);
                    return torrent;
                }
            } catch (IOException | NetException var7) {
                throw new DownloadException("种子文件加载失败", var7);
            }
        }
    }
}
