//package com.torrent.event.adapter;
//
//import com.torrent.TorrentContext;
//import com.torrent.context.GuiContext;
//import com.torrent.event.GuiEventArgs;
//import com.torrent.exception.DownloadException;
//import com.torrent.model.ITaskSession;
//import com.torrent.model.Torrent;
//import com.torrent.model.TorrentFile;
//import com.torrent.model.wrapper.DescriptionWrapper;
//import com.torrent.utils.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * <p>GUI选择下载文件事件</p>
// *
// * @author turtle
// */
//public class MultifileEventAdapter extends GuiEventArgs {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(MultifileEventAdapter.class);
//
//	public MultifileEventAdapter() {
//		super(Type.MULTIFILE, "选择下载文件事件");
//	}
//
//	@Override
//	protected final void executeExtend(GuiContext.Mode mode, Object ... args) {
//		if(!this.check(args, 1)) {
//			return;
//		}
//		final ITaskSession taskSession = (ITaskSession) this.getArg(args, 0);
//		if(mode == GuiContext.Mode.NATIVE) {
//			this.executeNativeExtend(taskSession);
//		} else {
//			this.executeExtendExtend(taskSession);
//		}
//	}
//
//	/**
//	 * <p>本地消息</p>
//	 *
//	 * @param taskSession 任务信息
//	 */
//	protected void executeNativeExtend(ITaskSession taskSession) {
//		this.executeExtendExtend(taskSession);
//	}
//
//	/**
//	 * <p>扩展消息</p>
//	 *
//	 * @param taskSession 任务信息
//	 */
//	protected void executeExtendExtend(ITaskSession taskSession) {
//		String files = GuiContext.getInstance().files();
//		try {
//			List<String> selectFiles;
//			final Torrent torrent = TorrentContext.getInstance().newTorrentSession(taskSession.getTorrent()).torrent();
//			if(StringUtils.isEmpty(files)) {
//				// 没有选择文件默认下载所有文件
//				selectFiles = torrent.getInfo().files().stream()
//					.filter(TorrentFile::notPaddingFile)
//					.map(TorrentFile::path)
//					.collect(Collectors.toList());
//				files = DescriptionWrapper.newEncoder(selectFiles).serialize();
//			} else {
//				// 选择文件列表
//				selectFiles = DescriptionWrapper.newDecoder(files).deserialize().stream()
//					.map(StringUtils::getString)
//					.collect(Collectors.toList());
//			}
//			// 选择文件大小
//			final long size = torrent.getInfo().files().stream()
//				.filter(file -> selectFiles.contains(file.path()))
//				.collect(Collectors.summingLong(TorrentFile::getLength));
//			taskSession.setSize(size);
//			taskSession.setDescription(files);
//		} catch (DownloadException e) {
//			LOGGER.error("设置选择下载文件异常：{}", files, e);
//		} finally {
//			GuiContext.getInstance().files(null);
//		}
//	}
//
//}
