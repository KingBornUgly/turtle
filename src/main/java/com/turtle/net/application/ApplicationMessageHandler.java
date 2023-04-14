//package com.torrent.net.application;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import com.torrent.config.SymbolConfig;
//import com.torrent.context.GuiContext;
//import com.torrent.context.SystemContext;
//import com.torrent.context.TaskContext;
//import com.torrent.exception.DownloadException;
//import com.torrent.exception.NetException;
//import com.torrent.exception.PacketSizeException;
//import com.torrent.fromat.BEncodeDecoder;
//import com.torrent.fromat.BEncodeEncoder;
//import com.torrent.model.ITaskSession;
//import com.torrent.model.message.ApplicationMessage;
//import com.torrent.model.message.GuiMessage;
//import com.torrent.net.TcpMessageHandler;
//import com.torrent.net.codec.IMessageEncoder;
//import com.torrent.net.codec.LineMessageCodec;
//import com.torrent.net.codec.StringMessageCodec;
//import com.torrent.net.torrent.codec.IMessageDecoder;
//import com.torrent.utils.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * <p>系统消息代理</p>
// *
// * @author turtle
// */
//public final class ApplicationMessageHandler extends TcpMessageHandler implements IMessageDecoder<String> {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationMessageHandler.class);
//
//    /**
//     * <p>消息编码器</p>
//     */
//    private final IMessageEncoder<String> messageEncoder;
//
//    public ApplicationMessageHandler() {
//        final LineMessageCodec lineMessageCodec = new LineMessageCodec(this, SymbolConfig.LINE_SEPARATOR_COMPAT);
//        final StringMessageCodec stringMessageCodec = new StringMessageCodec(lineMessageCodec);
//        this.messageDecoder = stringMessageCodec;
//        this.messageEncoder = lineMessageCodec;
//    }
//
//    /**
//     * <p>发送系统消息</p>
//     *
//     * @param message 系统消息
//     */
//    public void send(ApplicationMessage message) {
//        try {
//            this.send(message.toString());
//        } catch (NetException e) {
//            LOGGER.error("发送系统消息异常", e);
//        }
//    }
//
//    @Override
//    public void send(String message, String charset) throws NetException {
//        super.send(this.messageEncoder.encode(message), charset);
//    }
//
//    @Override
//    public void onMessage(String message) {
//        if (StringUtils.isEmpty(message)) {
//            LOGGER.warn("系统消息错误：{}", message);
//            return;
//        }
//        message = message.trim();
//        final ApplicationMessage applicationMessage = ApplicationMessage.valueOf(message);
//        if (applicationMessage == null) {
//            LOGGER.warn("系统消息错误（格式）：{}", message);
//            return;
//        }
//        this.execute(applicationMessage);
//    }
//
//    /**
//     * <p>处理系统消息</p>
//     *
//     * @param message 系统消息
//     */
//    private void execute(ApplicationMessage message) {
//        final ApplicationMessage.Type type = message.getType();
//        if (type == null) {
//            LOGGER.warn("系统消息错误（未知类型）：{}", type);
//            return;
//        }
//        LOGGER.debug("处理系统消息：{}", message);
//        switch (type) {
//            case GUI:
//                this.onGui();
//                break;
//            case TEXT:
//                this.onText(message);
//                break;
//            case CLOSE:
//                this.onClose();
//                break;
//            case NOTIFY:
//                this.onNotify();
//                break;
//            case SHUTDOWN:
//                this.onShutdown();
//                break;
//            case TASK_NEW:
//                this.onTaskNew(message);
//                break;
//            case TASK_LIST:
//                this.onTaskList();
//                break;
//            case TASK_START:
//                this.onTaskStart(message);
//                break;
//            case TASK_PAUSE:
//                this.onTaskPause(message);
//                break;
//            case TASK_DELETE:
//                this.onTaskDelete(message);
//                break;
//            case SHOW:
//                this.onShow();
//                break;
//            case HIDE:
//                this.onHide();
//                break;
//            case ALERT:
//                this.onAlert(message);
//                break;
//            case NOTICE:
//                this.onNotice(message);
//                break;
//            case REFRESH_TASK_LIST:
//                this.onRefreshTaskList();
//                break;
//            case REFRESH_TASK_STATUS:
//                this.onRefreshTaskStatus();
//                break;
//            case RESPONSE:
//                this.onResponse(message);
//                break;
//            default:
//                LOGGER.warn("系统消息错误（类型未适配）：{}", type);
//        }
//    }
//
//    /**
//     * <p>扩展GUI注册</p>
//     * <p>将当前连接的消息代理注册为GUI消息代理</p>
//     *
//     */
//    private void onGui() {
//        final boolean success = GuiContext.getInstance().extendGuiMessageHandler(this);
//        if (success) {
//            this.send(ApplicationMessage.Type.RESPONSE.build(ApplicationMessage.SUCCESS));
//        } else {
//            this.send(ApplicationMessage.Type.RESPONSE.build(ApplicationMessage.FAIL));
//        }
//    }
//
//    /**
//     * <p>文本消息</p>
//     *
//     * @param message 系统消息
//     */
//    private void onText(ApplicationMessage message) {
//        this.send(ApplicationMessage.Type.RESPONSE.build(message.getBody()));
//    }
//
//    /**
//     * <p>关闭连接</p>
//     */
//    private void onClose() {
//        this.close();
//    }
//
//    /**
//     * <p>唤醒窗口</p>
//     */
//    private void onNotify() {
//        GuiContext.getInstance().show();
//    }
//
//    /**
//     * <p>关闭程序</p>
//     */
//    private void onShutdown() {
//        SystemContext.shutdown();
//    }
//
//    /**
//     * <p>新建任务</p>
//     * <dl>
//     * 	<dt>body：Map（B编码）</dt>
//     * 	<dd>url：下载链接</dd>
//     * 	<dd>files：选择下载文件列表（B编码）</dd>
//     * </dl>
//     *
//     * @param message 系统消息
//     */
//    private void onTaskNew(ApplicationMessage message) {
//        final String body = message.getBody();
//        try {
//            final BEncodeDecoder decoder = BEncodeDecoder.newInstance(body).next();
//            if (decoder.isEmpty()) {
//                this.send(ApplicationMessage.Type.RESPONSE.build(ApplicationMessage.FAIL));
//                return;
//            }
//            final String url = decoder.getString("url");
//            final String files = decoder.getString("files");
//            synchronized (this) {
//                // 设置选择文件
//                GuiContext.getInstance().files(files);
//                // 开始下载任务
//                TaskContext.getInstance().download(url);
//            }
//            this.send(ApplicationMessage.Type.RESPONSE.build(ApplicationMessage.SUCCESS));
//        } catch (NetException | DownloadException e) {
//            this.send(ApplicationMessage.Type.RESPONSE.build(e.getMessage()));
//        }
//    }
//
//    /**
//     * <p>任务列表</p>
//     * <p>返回任务列表（B编码）</p>
//     */
//    private void onTaskList() {
//        final List<Map<String, Object>> list = TaskContext.getInstance().allTask().stream()
//                .map(ITaskSession::taskMessage)
//                .collect(Collectors.toList());
//        final String body = BEncodeEncoder.encodeListString(list);
//        this.send(ApplicationMessage.Type.RESPONSE.build(body));
//    }
//
//    /**
//     * <p>开始任务</p>
//     * <p>body：任务ID</p>
//     *
//     * @param message 系统消息
//     */
//    private void onTaskStart(ApplicationMessage message) {
//        final Optional<ITaskSession> optional = this.selectTaskSession(message);
//        if (optional.get() == null) {
//            this.send(ApplicationMessage.Type.RESPONSE.build(ApplicationMessage.FAIL));
//        } else {
//            try {
//                optional.get().start();
//                this.send(ApplicationMessage.Type.RESPONSE.build(ApplicationMessage.SUCCESS));
//            } catch (DownloadException e) {
//                this.send(ApplicationMessage.Type.RESPONSE.build(e.getMessage()));
//            }
//        }
//    }
//
//    /**
//     * <p>暂停任务</p>
//     * <p>body：任务ID</p>
//     *
//     * @param message 系统消息
//     */
//    private void onTaskPause(ApplicationMessage message) {
//        final Optional<ITaskSession> optional = this.selectTaskSession(message);
//        if (optional.get() == null) {
//            this.send(ApplicationMessage.Type.RESPONSE.build(ApplicationMessage.FAIL));
//        } else {
//            optional.get().pause();
//            this.send(ApplicationMessage.Type.RESPONSE.build(ApplicationMessage.SUCCESS));
//        }
//    }
//
//    /**
//     * <p>删除任务</p>
//     * <p>body：任务ID</p>
//     *
//     * @param message 系统消息
//     */
//    private void onTaskDelete(ApplicationMessage message) {
//        final Optional<ITaskSession> optional = this.selectTaskSession(message);
//        if (optional.get() == null) {
//            this.send(ApplicationMessage.Type.RESPONSE.build(ApplicationMessage.FAIL));
//        } else {
//            optional.get().delete();
//            this.send(ApplicationMessage.Type.RESPONSE.build(ApplicationMessage.SUCCESS));
//        }
//    }
//
//    /**
//     * <p>显示窗口</p>
//     */
//    private void onShow() {
//        GuiContext.getInstance().show();
//    }
//
//    /**
//     * <p>隐藏窗口</p>
//     */
//    private void onHide() {
//        GuiContext.getInstance().hide();
//    }
//
//    /**
//     * <p>窗口消息</p>
//     *
//     * @param message 系统消息
//     */
//    private void onAlert(ApplicationMessage message) {
//        try {
//            final GuiMessage guiMessage = GuiMessage.of(message);
//            if (guiMessage == null) {
//                LOGGER.warn("窗口消息错误：{}", message);
//                return;
//            }
//            GuiContext.getInstance().alert(guiMessage.getTitle(), guiMessage.getMessage(), guiMessage.getType());
//        } catch (PacketSizeException e) {
//            LOGGER.warn("处理窗口消息异常", e);
//        }
//    }
//
//    /**
//     * <p>提示消息</p>
//     *
//     * @param message 系统消息
//     */
//    private void onNotice(ApplicationMessage message) {
//        try {
//            final GuiMessage guiMessage = GuiMessage.of(message);
//            if (guiMessage == null) {
//                LOGGER.warn("提示消息错误：{}", message);
//                return;
//            }
//            GuiContext.getInstance().notice(guiMessage.getTitle(), guiMessage.getMessage(), guiMessage.getType());
//        } catch (PacketSizeException e) {
//            LOGGER.warn("处理提示消息异常", e);
//        }
//    }
//
//    /**
//     * <p>刷新任务列表</p>
//     */
//    private void onRefreshTaskList() {
//        GuiContext.getInstance().refreshTaskList();
//    }
//
//    /**
//     * <p>刷新任务状态</p>
//     */
//    private void onRefreshTaskStatus() {
//        GuiContext.getInstance().refreshTaskStatus();
//    }
//
//    /**
//     * <p>响应消息</p>
//     *
//     * @param message 系统消息
//     */
//    private void onResponse(ApplicationMessage message) {
//        GuiContext.getInstance().response(message.getBody());
//    }
//
//    /**
//     * <p>获取任务信息</p>
//     * <p>body：任务ID</p>
//     *
//     * @param message 系统消息
//     * @return 任务信息
//     */
//    private Optional<ITaskSession> selectTaskSession(ApplicationMessage message) {
//        final String body = message.getBody();
//        return TaskContext.getInstance().allTask().stream()
//                .filter(session -> session.getId().equals(body))
//                .findFirst();
//    }
//
//}
