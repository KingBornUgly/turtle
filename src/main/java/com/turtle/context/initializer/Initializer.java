/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.context.initializer;

import com.turtle.context.SystemThreadContext;
import com.turtle.exception.DownloadException;
import com.turtle.exception.NetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 初始化器
 * @author KingBornUgly
 * @date 2023/1/4 9:47 PM
 */
public abstract class Initializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Initializer.class);

    /**
     * <p>名称</p>
     */
    private final String name;
    /**
     * <p>延迟时间（秒）</p>
     *
     * @see #asyn()
     */
    private final int delay;

    /**
     * <p>初始化器</p>
     *
     * @param name 名称
     */
    protected Initializer(String name) {
        this(name, 0);
    }

    /**
     * <p>初始化器</p>
     *
     * @param name 名称
     * @param delay 延迟时间（秒）
     */
    protected Initializer(String name, int delay) {
        this.name = name;
        this.delay = delay;
    }

    /**
     * <p>同步执行初始方法</p>
     */
    public final void sync() {
        try {
            LOGGER.debug("同步执行初始方法：{}", this.name);
            this.init();
        } catch (NetException | DownloadException e) {
            LOGGER.error("同步执行初始方法异常：{}", this.name, e);
        }
    }

    /**
     * <p>异步执行初始方法</p>
     */
    public final void asyn() {
        final Runnable runnable = () -> {
            try {
                LOGGER.debug("异步执行初始方法：{}", this.name);
                this.init();
            } catch (NetException | DownloadException e) {
                LOGGER.error("异步执行初始方法异常：{}", this.name, e);
            }
        };
        if(this.delay <= 0) {
            SystemThreadContext.submit(runnable);
        } else {
            SystemThreadContext.timer(
                    this.delay,
                    TimeUnit.SECONDS,
                    runnable
            );
        }
    }

    /**
     * <p>初始方法</p>
     *
     * @throws NetException 网络异常
     * @throws DownloadException 下载异常
     */
    protected abstract void init() throws NetException, DownloadException;

}
