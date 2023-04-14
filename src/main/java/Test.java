/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2022/11/26
 */


import com.turtle.Thunder;

/**
 * xxxxx
 *
 * @author KingBornUgly
 * @date 2022/11/26 5:53 PM
 */
public class Test {

    public static void main(String[] args) throws Exception {
        final String torrentPath = "/Users/zhangyinshan/Desktop/终末的女武神.Record.of.Ragnarok.S01E01.1080p.H265-官方中字.mp4.torrent";
        final Thunder TURTLE = Thunder.ThunderBuilder.newBuilder()
                .enableTorrent()
                .buildSync();
// 解析种子文件
//        final Torrent torrent = TorrentContext.loadTorrent(torrentPath);
//// 过滤下载文件
//        final List list = torrent.getInfo().files().stream()
//                .filter(TorrentFile::notPaddingFile)
//                .map(TorrentFile::path)
//                .filter(path -> path.endsWith(".mkv"))
//                .collect(Collectors.toList());
// 注册文件选择事件
//        GuiContext.register(new MultifileEventAdapter());
// 开始下载
        TURTLE.download(torrentPath);
        TURTLE.lockDownload();
    }
}
