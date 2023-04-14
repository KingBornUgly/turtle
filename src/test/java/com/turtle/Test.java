/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: zhangyinshan Create date: 2023/4/14
 */
package com.turtle;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/4/14 4:21 PM
 */
public class Test {
    public static void main(String[] args) throws Exception {
        final String torrentPath = "fileName";
        final Thunder turtle = Thunder.ThunderBuilder.newBuilder()
                .enableTorrent()
                .buildSync();
        turtle.download(torrentPath);
        turtle.lockDownload();
    }
}
