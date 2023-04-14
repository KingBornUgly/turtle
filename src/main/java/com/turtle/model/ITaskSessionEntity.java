/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.model;


import com.turtle.protocol.Protocol;

import java.util.Date;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 5:29 PM
 */
public interface ITaskSessionEntity {

    /**
     * <p>获取任务ID</p>
     *
     * @return 任务ID
     */
    String getId();

    /**
     * <p>设置任务ID</p>
     *
     * @param id 任务ID
     */
    void setId(String id);

    /**
     * <p>获取任务名称</p>
     *
     * @return 任务名称
     */
    String getName();

    /**
     * <p>设置任务名称</p>
     *
     * @param name 任务名称
     */
    void setName(String name);

    /**
     * <p>获取协议类型</p>
     *
     * @return 协议类型
     */
    Protocol.Type getType();

    /**
     * <p>设置协议类型</p>
     *
     * @param type 协议类型
     */
    void setType(Protocol.Type type);

    /**
     * <p>获取文件类型</p>
     *
     * @return 文件类型
     */
    ITaskSession.FileType getFileType();

    /**
     * <p>设置文件类型</p>
     *
     * @param fileType 文件类型
     */
    void setFileType(ITaskSession.FileType fileType);

    /**
     * <p>获取文件路径或目录路径</p>
     *
     * @return 文件路径或目录路径
     */
    String getFile();

    /**
     * <p>设置文件路径或目录路径</p>
     *
     * @param file 文件路径或目录路径
     */
    void setFile(String file);

    /**
     * <p>获取下载链接</p>
     *
     * @return 下载链接
     */
    String getUrl();

    /**
     * <p>设置下载链接</p>
     *
     * @param url 下载链接
     */
    void setUrl(String url);

    /**
     * <p>获取BT任务种子文件路径</p>
     *
     * @return BT任务种子文件路径
     */
    String getTorrent();

    /**
     * <p>设置BT任务种子文件路径</p>
     *
     * @param torrent BT任务种子文件路径
     */
    void setTorrent(String torrent);

    /**
     * <p>获取任务状态</p>
     *
     * @return 任务状态
     */
    ITaskSessionStatus.Status getStatus();

    /**
     * <p>设置任务状态</p>
     *
     * @param status 任务状态
     */
    void setStatus(ITaskSessionStatus.Status status);

    /**
     * <p>获取文件大小（B）</p>
     *
     * @return 文件大小（B）
     */
    Long getSize();

    /**
     * <p>设置文件大小（B）</p>
     *
     * @param size 文件大小（B）
     */
    void setSize(Long size);

    /**
     * <p>获取完成时间</p>
     *
     * @return 完成时间
     */
    Date getEndDate();

    /**
     * <p>设置完成时间</p>
     *
     * @param endDate 完成时间
     */
    void setEndDate(Date endDate);

    /**
     * <p>获取下载描述</p>
     *
     * @return 下载描述
     */
    String getDescription();

    /**
     * <p>设置下载描述</p>
     *
     * @param description 下载描述
     */
    void setDescription(String description);

    /**
     * <p>获取任务负载</p>
     *
     * @return 任务负载
     */
    byte[] getPayload();

    /**
     * <p>设置任务负载</p>
     *
     * @param payload 任务负载
     */
    void setPayload(byte[] payload);

}

