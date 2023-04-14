package com.turtle.protocol.http;

import com.turtle.downloader.IDownloader;
import com.turtle.downloader.http.HttpDownloader;
import com.turtle.exception.DownloadException;
import com.turtle.exception.NetException;
import com.turtle.model.ITaskSession;
import com.turtle.model.wrapper.HttpHeaderWrapper;
import com.turtle.net.http.HttpClient;
import com.turtle.protocol.Protocol;

/**
 * <p>HTTP协议</p>
 * 
 * @author turtle
 */
public final class HttpProtocol extends Protocol {

	private static final HttpProtocol INSTANCE = new HttpProtocol();
	
	public static final HttpProtocol getInstance() {
		return INSTANCE;
	}

	/**
	 * <p>HTTP头部信息</p>
	 */
	private HttpHeaderWrapper httpHeaderWrapper;
	
	private HttpProtocol() {
		super(Type.HTTP, "HTTP");
	}
	
	@Override
	public boolean available() {
		return true;
	}
	
	@Override
	public IDownloader buildDownloader(ITaskSession taskSession) {
		return HttpDownloader.newInstance(taskSession);
	}

	@Override
	protected void prep() throws DownloadException {
		this.buildHttpHeader();
	}
	
	@Override
	protected String buildFileName() throws DownloadException {
		// 优先使用头部信息中的文件名称
		final String defaultName = super.buildFileName();
		return this.httpHeaderWrapper.fileName(defaultName);
	}

	@Override
	protected void buildSize() {
		this.taskEntity.setSize(this.httpHeaderWrapper.fileSize());
	}
	
	@Override
	protected void release(boolean success) {
		this.httpHeaderWrapper = null;
		super.release(success);
	}

	/**
	 * <p>获取HTTP头部信息</p>
	 * 
	 * @throws DownloadException 下载异常
	 */
	private void buildHttpHeader() throws DownloadException {
		try {
			this.httpHeaderWrapper = HttpClient
				.newInstance(this.url)
				.head()
				.responseHeader();
		} catch (NetException e) {
			throw new DownloadException("获取HTTP头部信息失败", e);
		}
	}
	
}
