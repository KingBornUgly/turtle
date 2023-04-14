package com.turtle.downloader;

import com.turtle.TorrentContext;
import com.turtle.exception.DownloadException;
import com.turtle.exception.NetException;
import com.turtle.model.ITaskSession;
import com.turtle.model.Magnet;
import com.turtle.model.session.TorrentSession;
import com.turtle.protocol.maget.MagnetBuilder;

/**
 * <p>BT任务下载器</p>
 * 
 * @author turtle
 */
public abstract class TorrentSessionDownloader extends MultifileDownloader {
	
	/**
	 * <p>BT任务信息</p>
	 */
	protected TorrentSession torrentSession;
	
	/**
	 * @param taskSession 任务信息
	 */
	protected TorrentSessionDownloader(ITaskSession taskSession) {
		super(taskSession);
	}
	
	@Override
	public void open() throws NetException, DownloadException {
		// 不能在构造函数中初始化：防止种子被删除后还能点击下载
		this.torrentSession = this.loadTorrentSession();
		super.open();
	}
	
	/**
	 * <p>加载BT任务信息</p>
	 * 
	 * @return BT任务信息
	 * 
	 * @throws DownloadException 下载异常
	 */
	protected TorrentSession loadTorrentSession() throws DownloadException {
		final Magnet magnet = MagnetBuilder.newInstance(this.taskSession.getUrl()).build();
		return TorrentContext.getInstance().newTorrentSession(magnet.getHash(), this.taskSession.getTorrent());
	}
	
	@Override
	protected boolean checkCompleted() {
		return this.torrentSession.checkCompleted();
	}

}
