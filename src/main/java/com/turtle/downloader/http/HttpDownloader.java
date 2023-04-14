package com.turtle.downloader.http;

import com.turtle.downloader.SingleFileDownloader;
import com.turtle.exception.NetException;
import com.turtle.model.ITaskSession;
import com.turtle.model.wrapper.HttpHeaderWrapper;
import com.turtle.net.http.HttpClient;
import com.turtle.utils.FileUtils;
import com.turtle.utils.IoUtils;

import java.nio.channels.Channels;

/**
 * <p>HTTP任务下载器</p>
 * 
 * @author turtle
 */
public final class HttpDownloader extends SingleFileDownloader {

	/**
	 * @param taskSession 任务信息
	 */
	private HttpDownloader(ITaskSession taskSession) {
		super(taskSession);
	}

	/**
	 * <p>新建HTTP任务下载器</p>
	 * 
	 * @param taskSession 任务信息
	 * 
	 * @return {@link HttpDownloader}
	 */
	public static final HttpDownloader newInstance(ITaskSession taskSession) {
		return new HttpDownloader(taskSession);
	}
	
	@Override
	public void release() {
		IoUtils.close(this.input);
		IoUtils.close(this.output);
		super.release();
	}
	
	@Override
	protected void buildInput() throws NetException {
		final long downloadSize = FileUtils.fileSize(this.taskSession.getFile());
		final HttpClient client = HttpClient
			.newDownloader(this.taskSession.getUrl())
			.range(downloadSize)
			.get();
		if(client.downloadable()) {
			final HttpHeaderWrapper headers = client.responseHeader();
			this.input = Channels.newChannel(client.response());
			if(headers.range()) {
				this.taskSession.downloadSize(downloadSize);
			} else {
				this.taskSession.downloadSize(0L);
			}
		} else if(client.requestedRangeNotSatisfiable()) {
			if(this.taskSession.downloadSize() == this.taskSession.getSize()) {
				this.completed = true;
			} else {
				this.fail("无法满足文件下载范围：" + downloadSize);
			}
		} else {
			this.fail("HTTP请求失败：" + client.code());
		}
	}

}
