/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.model;


import com.turtle.model.session.IStatisticsSession;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 5:27 PM
 */
public abstract class StatisticsGetter implements IStatisticsGetter {

    /**
     * <p>统计信息</p>
     */
    protected final IStatisticsSession statistics;

    /**
     * @param statistics 统计信息
     */
    protected StatisticsGetter(IStatisticsSession statistics) {
        this.statistics = statistics;
    }

    @Override
    public IStatisticsSession statistics() {
        return this.statistics;
    }

    @Override
    public long uploadSize() {
        return this.statistics.uploadSize();
    }

    @Override
    public long downloadSize() {
        return this.statistics.downloadSize();
    }

}

