/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.model;


import com.turtle.model.session.IStatisticsSession;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 4:29 PM
 */
public interface IStatisticsGetter {

    /**
     * <p>获取统计信息</p>
     *
     * @return 统计信息
     */
    IStatisticsSession statistics();

    /**
     * <p>获取累计上传大小</p>
     *
     * @return 累计上传大小
     */
    long uploadSize();

    /**
     * <p>获取累计下载大小</p>
     *
     * @return 累计下载大小
     */
    long downloadSize();

}
