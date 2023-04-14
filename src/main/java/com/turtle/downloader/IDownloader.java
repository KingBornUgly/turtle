/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle.downloader;


import com.turtle.exception.DownloadException;
import com.turtle.exception.NetException;
import com.turtle.model.ITaskSession;
import com.turtle.model.ITaskSessionStatus;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 6:38 PM
 */
public interface IDownloader extends Runnable, ITaskSessionStatus {

    /**
     * <p>获取任务ID</p>
     *
     * @return 任务ID
     */
    String id();

    /**
     * <p>获取任务名称</p>
     *
     * @return 任务名称
     */
    String name();

    /**
     * <p>获取任务信息</p>
     *
     * @return 任务信息
     */
    ITaskSession taskSession();

    /**
     * <p>刷新任务</p>
     *
     * @throws DownloadException 下载异常
     */
    void refresh() throws DownloadException;

    /**
     * <p>校验下载文件</p>
     *
     * @return 校验结果
     *
     * @throws DownloadException 下载异常
     */
    boolean verify() throws DownloadException;

    /**
     * <p>标记失败</p>
     *
     * @param message 失败信息
     */
    void fail(String message);

    /**
     * <dl>
     * 	<dt>打开任务</dt>
     * 	<dd>初始化下载信息</dd>
     * 	<dd>打开下载数据流</dd>
     * 	<dd>打开本地文件流</dd>
     * </dl>
     *
     * @throws NetException 网络异常
     * @throws DownloadException 下载异常
     */
    void open() throws NetException, DownloadException;

    /**
     * <p>下载任务</p>
     *
     * @throws DownloadException 下载异常
     */
    void download() throws DownloadException;

    /**
     * <p>释放下载锁</p>
     */
    void unlockDownload();

    /**
     * <p>释放资源</p>
     */
    void release();

    /**
     * <p>删除任务</p>
     */
    void delete();

}

