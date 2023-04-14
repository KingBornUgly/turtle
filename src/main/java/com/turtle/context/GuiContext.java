///**
// * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/4
// */
//package com.torrent.context;
//
//import com.torrent.IContext;
//import com.torrent.config.SymbolConfig;
//import com.torrent.event.GuiEvent;
//import com.torrent.exception.NetException;
//import com.torrent.model.ITaskSession;
//import com.torrent.model.message.ApplicationMessage;
//import com.torrent.net.IMessageSender;
//import com.torrent.utils.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.EnumMap;
//
///**
// * GUI上下文
// * @author KingBornUgly
// * @date 2023/1/4 9:53 PM
// */
//public final class GuiContext implements IContext {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(GuiContext.class);
//
//    private static final GuiContext INSTANCE = new GuiContext();
//
//    public static final GuiContext getInstance() {
//        return INSTANCE;
//    }
//
//    /**
//     * <p>运行模式</p>
//     *
//     * @author turtle
//     */
//    public enum Mode {
//
//        /**
//         * <p>本地模式：本地GUI（JavaFX）</p>
//         */
//        NATIVE,
//        /**
//         * <p>后台模式：扩展GUI</p>
//         * <p>通过系统消息和系统通知来完成系统管理和任务管理</p>
//         *
//         * @see ApplicationMessage.Type
//         */
//        EXTEND;
//
//    }
//
//    /**
//     * <p>消息类型</p>
//     *
//     * @author turtle
//     */
//    public enum MessageType {
//
//        /**
//         * <p>普通</p>
//         */
//        NONE,
//        /**
//         * <p>提示</p>
//         */
//        INFO,
//        /**
//         * <p>警告</p>
//         */
//        WARN,
//        /**
//         * <p>确认</p>
//         */
//        CONFIRM,
//        /**
//         * <p>错误</p>
//         */
//        ERROR;
//
//        /**
//         * <p>获取消息类型</p>
//         *
//         * @param value 消息类型
//         *
//         * @return 消息类型
//         */
//        public static final GuiContext.MessageType of(String value) {
//            final MessageType[] types = GuiContext.MessageType.values();
//            for (GuiContext.MessageType type : types) {
//                if(type.name().equalsIgnoreCase(value)) {
//                    return type;
//                }
//            }
//            return null;
//        }
//
//    }
//
//    /**
//     * <p>启动参数：运行模式</p>
//     */
//    private static final String ARGS_MODE = "mode";
//
//    /**
//     * <p>运行模式</p>
//     */
//    private GuiContext.Mode mode = GuiContext.Mode.NATIVE;
//    /**
//     * <p>选择下载文件列表（B编码）</p>
//     *
//     * @see GuiEvent.Type#MULTIFILE
//     */
//    private String files;
//    /**
//     * <p>扩展GUI阻塞锁</p>
//     * <p>使用扩展GUI时阻止程序关闭</p>
//     */
//    private final Object lock;
//    /**
//     * <p>扩展GUI消息代理</p>
//     */
//    private IMessageSender extendGuiMessageSender;
//    /**
//     * <p>事件Map</p>
//     * <p>事件类型=事件</p>
//     */
//    private final Map<GuiEvent.Type, GuiEvent> events;
//
//    private GuiContext() {
//        this.lock = new Object();
//        this.events = new EnumMap<>(GuiEvent.Type.class);
//    }
//
//    /**
//     * <p>注册GUI事件</p>
//     *
//     * @param event GUI事件
//     */
//    public static final void register(GuiEvent event) {
//        if(LOGGER.isDebugEnabled()) {
//            LOGGER.debug("注册GUI事件：{}-{}", event.type(), event.name());
//        }
//        INSTANCE.events.put(event.type(), event);
//    }
//
//    /**
//     * <p>注册GUI事件默认适配器</p>
//     */
//    public static final void registerAdapter() {
////        GuiContext.register(new ShowEventAdapter());
////        GuiContext.register(new HideEventAdapter());
////        GuiContext.register(new ExitEventAdapter());
////        GuiContext.register(new BuildEventAdapter());
////        GuiContext.register(new AlertEventAdapter());
////        GuiContext.register(new NoticeEventAdapter());
////        GuiContext.register(new ResponseEventAdapter());
//        GuiContext.register(new MultifileEventAdapter());
////        GuiContext.register(new RefreshTaskListEventAdapter());
////        GuiContext.register(new RefreshTaskStatusEventAdapter());
//    }
//
//    /**
//     * <p>初始化GUI上下文</p>
//     *
//     * @param args 启动参数
//     *
//     * @return GuiContext
//     */
//    public GuiContext init(String ... args) {
//        if(args == null) {
//            LOGGER.debug("没有设置启动参数");
//        } else {
//            if(LOGGER.isInfoEnabled()) {
//                LOGGER.info("启动参数：{}", SymbolConfig.Symbol.COMMA.join(args));
//            }
//            String value;
//            for (String arg : args) {
//                // 运行模式
//                value = StringUtils.argValue(arg, ARGS_MODE);
//                if(GuiContext.Mode.EXTEND.name().equalsIgnoreCase(value)) {
//                    this.mode = GuiContext.Mode.EXTEND;
//                }
//            }
//            LOGGER.debug("运行模式：{}", this.mode);
//        }
//        return this;
//    }
//
//    /**
//     * <p>显示窗口</p>
//     *
//     * @return GuiContext
//     */
//    public GuiContext show() {
//        return this.event(GuiEvent.Type.SHOW);
//    }
//
//    /**
//     * <p>隐藏窗口</p>
//     *
//     * @return GuiContext
//     */
//    public GuiContext hide() {
//        return this.event(GuiEvent.Type.HIDE);
//    }
//
//    /**
//     * <p>退出窗口</p>
//     *
//     * @return GuiContext
//     */
//    public GuiContext exit() {
//        return this.event(GuiEvent.Type.EXIT);
//    }
//
//    /**
//     * <p>新建窗口</p>
//     *
//     * @return GuiContext
//     */
//    public GuiContext build() {
//        return this.event(GuiEvent.Type.BUILD);
//    }
//
//    /**
//     * <p>窗口消息</p>
//     *
//     * @param title 标题
//     * @param message 内容
//     *
//     * @return GuiContext
//     */
//    public GuiContext alert(String title, String message) {
//        return this.alert(title, message, GuiContext.MessageType.INFO);
//    }
//
//    /**
//     * <p>窗口消息</p>
//     *
//     * @param title 标题
//     * @param message 内容
//     * @param type 类型
//     *
//     * @return GuiContext
//     */
//    public GuiContext alert(String title, String message, GuiContext.MessageType type) {
//        return this.event(GuiEvent.Type.ALERT, title, message, type);
//    }
//
//    /**
//     * <p>提示消息</p>
//     *
//     * @param title 标题
//     * @param message 内容
//     *
//     * @return GuiContext
//     */
//    public GuiContext notice(String title, String message) {
//        return this.notice(title, message, GuiContext.MessageType.INFO);
//    }
//
//    /**
//     * <p>提示消息</p>
//     *
//     * @param title 标题
//     * @param message 内容
//     * @param type 类型
//     *
//     * @return GuiContext
//     */
//    public GuiContext notice(String title, String message, GuiContext.MessageType type) {
//        return this.event(GuiEvent.Type.NOTICE, title, message, type);
//    }
//
//    /**
//     * <p>响应消息</p>
//     *
//     * @param message 消息
//     *
//     * @return GuiContext
//     */
//    public GuiContext response(String message) {
//        return this.event(GuiEvent.Type.RESPONSE, message);
//    }
//
//    /**
//     * <p>选择下载文件</p>
//     *
//     * @param taskSession 任务信息
//     *
//     * @return GuiContext
//     */
//    public GuiContext multifile(ITaskSession taskSession) {
//        return this.event(GuiEvent.Type.MULTIFILE, taskSession);
//    }
//
//    /**
//     * <p>刷新任务列表</p>
//     *
//     * @return GuiContext
//     */
//    public GuiContext refreshTaskList() {
//        return this.event(GuiEvent.Type.REFRESH_TASK_LIST);
//    }
//
//    /**
//     * <p>刷新任务状态</p>
//     *
//     * @return GuiContext
//     */
//    public GuiContext refreshTaskStatus() {
//        return this.event(GuiEvent.Type.REFRESH_TASK_STATUS);
//    }
//
//    /**
//     * <p>执行事件</p>
//     *
//     * @param type 类型
//     * @param args 参数
//     *
//     * @return GuiContext
//     */
//    public GuiContext event(GuiEvent.Type type, Object ... args) {
//        if(type == null) {
//            LOGGER.warn("错误GUI事件：{}", type);
//            return this;
//        }
//        final GuiEvent event = this.events.get(type);
//        if(event == null) {
//            LOGGER.warn("未知GUI事件：{}", type);
//            return this;
//        }
//        LOGGER.debug("执行GUI事件：{}", type);
//        event.execute(this.mode, args);
//        return this;
//    }
//
//    /**
//     * <p>获取选择下载文件列表（B编码）</p>
//     *
//     * @return 选择下载文件列表（B编码）
//     */
//    public String files() {
//        return this.files;
//    }
//
//    /**
//     * <p>设置选择下载文件列表（B编码）</p>
//     *
//     * @param files 选择下载文件列表（B编码）
//     */
//    public void files(String files) {
//        this.files = files;
//    }
//
//    /**
//     * <p>注册扩展GUI消息代理</p>
//     *
//     * @param extendGuiMessageSender 扩展GUI消息代理
//     *
//     * @return 是否注册成功
//     */
//    public boolean extendGuiMessageHandler(IMessageSender extendGuiMessageSender) {
//        if(this.mode == GuiContext.Mode.NATIVE) {
//            LOGGER.debug("已经启用本地GUI：忽略注册扩展GUI消息代理");
//            return false;
//        } else {
//            LOGGER.debug("注册扩展GUI消息代理");
//            this.extendGuiMessageSender = extendGuiMessageSender;
//            return true;
//        }
//    }
//
//    /**
//     * <p>发送扩展GUI消息</p>
//     *
//     * @param message 扩展GUI消息
//     */
//    public void sendExtendGuiMessage(ApplicationMessage message) {
//        if(message == null) {
//            LOGGER.warn("扩展GUI消息错误：{}", message);
//            return;
//        }
//        if(this.extendGuiMessageSender != null) {
//            try {
//                this.extendGuiMessageSender.send(message.toString());
//            } catch (NetException e) {
//                LOGGER.error("发送扩展GUI消息异常", e);
//            }
//        } else {
//            LOGGER.warn("扩展GUI消息代理没有注册");
//        }
//    }
//
//    /**
//     * <p>添加扩展GUI阻塞锁</p>
//     */
//    public void lock() {
//        synchronized (this.lock) {
//            try {
//                this.lock.wait(Long.MAX_VALUE);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                LOGGER.debug("线程等待异常", e);
//            }
//        }
//    }
//
//    /**
//     * <p>释放扩展GUI阻塞锁</p>
//     */
//    public void unlock() {
//        synchronized (this.lock) {
//            this.lock.notifyAll();
//        }
//    }
//
//}
//
