/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.model;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 5:45 PM
 */
public interface ITaskSessionTable {

    /**
     * <p>获取任务名称</p>
     *
     * @return 任务名称
     */
    String getNameValue();

    /**
     * <p>获取任务状态</p>
     * <p>下载任务：下载速度</p>
     * <p>其他情况：任务状态</p>
     *
     * @return 任务状态
     */
    String getStatusValue();

    /**
     * <p>获取任务进度</p>
     *
     * @return 任务进度
     */
    String getProgressValue();

    /**
     * <p>获取创建时间</p>
     *
     * @return 创建时间
     */
    String getCreateDateValue();

    /**
     * <p>获取完成时间</p>
     *
     * @return 完成时间
     */
    String getEndDateValue();

}
