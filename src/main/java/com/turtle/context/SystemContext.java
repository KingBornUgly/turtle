/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
 */
package com.turtle.context;

import com.turtle.IContext;
import com.turtle.Thunder;
import com.turtle.net.TcpClient;
import com.turtle.net.TcpServer;
import com.turtle.net.UdpServer;
import com.turtle.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统上下文
 * @author KingBornUgly
 * @date 2023/1/4 9:44 PM
 */
public final class SystemContext implements IContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemContext.class);

    private static final SystemContext INSTANCE = new SystemContext();

    public static final SystemContext getInstance() {
        return INSTANCE;
    }

    /**
     * <p>系统类型</p>
     *
     * @author turtle
     */
    public enum SystemType {

        /**
         * <p>Mac</p>
         */
        MAC("Mac OS", "Mac OS X"),
        /**
         * <p>Linux</p>
         */
        LINUX("Linux"),
        /**
         * <p>Windows</p>
         */
        WINDOWS("Windows XP", "Windows Vista", "Windows 7", "Windows 10"),
        /**
         * <p>Android</p>
         */
        ANDROID("Android");

        /**
         * <p>系统名称</p>
         */
        private final String[] osNames;

        /**
         * <p>系统类型</p>
         *
         * @param osNames 系统名称
         */
        private SystemType(String ... osNames) {
            this.osNames = osNames;
        }

        /**
         * <p>获取系统类型</p>
         *
         * @return 系统类型
         */
        public static final SystemContext.SystemType local() {
            final String osName = SystemContext.osName();
            for (SystemContext.SystemType type : SystemContext.SystemType.values()) {
                for (String value : type.osNames) {
                    if(value.equals(osName)) {
                        return type;
                    }
                }
            }
            LOGGER.warn("未知系统类型：{}", osName);
            return null;
        }

    }

    /**
     * <p>系统名称</p>
     */
    private final String osName;

    private SystemContext() {
        this.osName = System.getProperty("os.name");
    }

    /**
     * <p>整理系统内存</p>
     */
    public static final void gc() {
        LOGGER.info("整理系统内存");
        System.gc();
    }

    /**
     * <p>系统信息</p>
     */
    public static final void info() {
        final Runtime runtime = Runtime.getRuntime();
        final String freeMemory = FileUtils.formatSize(runtime.freeMemory());
        final String totalMemory = FileUtils.formatSize(runtime.totalMemory());
        final String maxMemory = FileUtils.formatSize(runtime.maxMemory());
        LOGGER.info("操作系统名称：{}", System.getProperty("os.name"));
        LOGGER.info("操作系统架构：{}", System.getProperty("os.arch"));
        LOGGER.info("操作系统版本：{}", System.getProperty("os.version"));
        LOGGER.info("操作系统可用处理器数量：{}", runtime.availableProcessors());
        LOGGER.info("Java版本：{}", System.getProperty("java.version"));
        LOGGER.info("Java主目录：{}", System.getProperty("java.home"));
        LOGGER.info("Java库目录：{}", System.getProperty("java.library.path"));
        LOGGER.info("虚拟机名称：{}", System.getProperty("java.vm.name"));
        LOGGER.info("虚拟机空闲内存：{}", freeMemory);
        LOGGER.info("虚拟机已用内存：{}", totalMemory);
        LOGGER.info("虚拟机最大内存：{}", maxMemory);
        LOGGER.info("用户目录：{}", System.getProperty("user.home"));
        LOGGER.info("工作目录：{}", System.getProperty("user.dir"));
        LOGGER.info("文件编码：{}", System.getProperty("file.encoding"));
    }

    /**
     * <p>系统初始化</p>
     *
     * @return TURTLE
     */
    public static final Thunder build() {
        return Thunder.ThunderBuilder.newBuilder()
                .loadTask()
                .application()
                .enableAllProtocol()
                .buildAsyn();
    }

    /**
     * <p>系统关闭</p>
     * <p>所有线程都是守护线程，所以可以不用手动关闭。</p>
     *
     * @see SystemThreadContext
     */
    public static final void shutdown() {
        if(Thunder.available()) {
            SystemThreadContext.submit(() -> {
                LOGGER.info("系统关闭中");
                Thunder.shutdown();
                TcpClient.shutdown();
                TcpServer.shutdown();
                UdpServer.shutdown();
                SystemThreadContext.shutdown();
                LOGGER.info("系统已关闭");
            });
        }
    }

    /**
     * <p>获取系统名称</p>
     *
     * @return 系统名称
     */
    public static final String osName() {
        return INSTANCE.osName;
    }

}

