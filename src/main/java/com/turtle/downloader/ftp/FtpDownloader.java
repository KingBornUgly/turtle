package com.turtle.downloader.ftp;

import com.turtle.downloader.SingleFileDownloader;
import com.turtle.exception.NetException;
import com.turtle.model.ITaskSession;
import com.turtle.net.ftp.FtpClient;
import com.turtle.utils.FileUtils;
import com.turtle.utils.IoUtils;

import java.nio.channels.Channels;

/**
 * <p>FTP任务下载器</p>
 * 
 * @author turtle
 */
public final class FtpDownloader extends SingleFileDownloader {
	
	/**
	 * <p>FTP客户端</p>
	 */
	private FtpClient client;
	
	/**
	 * @param taskSession 任务信息
	 */
	private FtpDownloader(ITaskSession taskSession) {
		super(taskSession);
	}

	/**
	 * <p>新建FTP任务下载器</p>
	 * 
	 * @param taskSession 任务信息
	 * 
	 * @return {@link FtpDownloader}
	 */
	public static final FtpDownloader newInstance(ITaskSession taskSession) {
		return new FtpDownloader(taskSession);
	}

	@Override
	public void release() {
		if(this.client != null) {
			this.client.close();
		}
		IoUtils.close(this.input);
		IoUtils.close(this.output);
		super.release();
	}
	
	@Override
	protected void buildInput() throws NetException {
		this.client = FtpClient.newInstance(this.taskSession.getUrl());
		final boolean success = this.client.connect();
		if(success) {
			final long downloadSize = FileUtils.fileSize(this.taskSession.getFile());
			this.input = Channels.newChannel(this.client.download(downloadSize));
			if(this.client.range()) {
				this.taskSession.downloadSize(downloadSize);
			} else {
				this.taskSession.downloadSize(0L);
			}
		} else {
			this.fail("FTP服务器连接失败");
		}
	}
	
}
