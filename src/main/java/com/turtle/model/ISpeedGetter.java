/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.model;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 4:29 PM
 */
public interface ISpeedGetter {

    /**
     * <p>获取上传速度</p>
     *
     * @return 上传速度
     */
    long uploadSpeed();

    /**
     * <p>获取下载速度</p>
     *
     * @return 下载速度
     */
    long downloadSpeed();

    /**
     * <p>重置上传速度</p>
     */
    void resetUploadSpeed();

    /**
     * <p>重置下载速度</p>
     */
    void resetDownloadSpeed();

}
