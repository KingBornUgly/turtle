/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.context;

import com.turtle.IContext;
import com.turtle.model.ISpeedGetter;
import com.turtle.model.StatisticsGetter;
import com.turtle.model.session.StatisticsSession;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/4 5:38 PM
 */
public final class StatisticsContext extends StatisticsGetter implements IContext, ISpeedGetter {

    private static final StatisticsContext INSTANCE = new StatisticsContext();

    public static final StatisticsContext getInstance() {
        return INSTANCE;
    }

    private StatisticsContext() {
        super(new StatisticsSession());
    }

    @Override
    public long uploadSpeed() {
        return this.statistics.uploadSpeed();
    }

    @Override
    public long downloadSpeed() {
        return this.statistics.downloadSpeed();
    }

    @Override
    public void resetUploadSpeed() {
        this.statistics.resetUploadSpeed();
    }

    @Override
    public void resetDownloadSpeed() {
        this.statistics.resetDownloadSpeed();
    }

}

