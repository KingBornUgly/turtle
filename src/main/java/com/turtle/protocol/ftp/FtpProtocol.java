package com.turtle.protocol.ftp;

import com.turtle.downloader.IDownloader;
import com.turtle.downloader.ftp.FtpDownloader;
import com.turtle.exception.DownloadException;
import com.turtle.exception.NetException;
import com.turtle.model.ITaskSession;
import com.turtle.net.ftp.FtpClient;
import com.turtle.protocol.Protocol;

/**
 * <p>FTP协议</p>
 * 
 * @author turtle
 */
public final class FtpProtocol extends Protocol {
	
	private static final FtpProtocol INSTANCE = new FtpProtocol();
	
	public static final FtpProtocol getInstance() {
		return INSTANCE;
	}
	
	private FtpProtocol() {
		super(Type.FTP, "FTP");
	}
	
	@Override
	public boolean available() {
		return true;
	}
	
	@Override
	public IDownloader buildDownloader(ITaskSession taskSession) {
		return FtpDownloader.newInstance(taskSession);
	}

	@Override
	protected void buildSize() throws DownloadException {
		final FtpClient client = FtpClient.newInstance(this.url);
		try {
			client.connect();
			final long size = client.size();
			this.taskEntity.setSize(size);
		} catch (NetException e) {
			throw new DownloadException(e);
		} finally {
			client.close();
		}
	}

}
