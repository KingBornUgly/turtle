//package com.torrent.net.application;
//
//
//import com.torrent.config.SystemConfig;
//import com.torrent.net.TcpServer;
//
///**
// * <p>系统服务端</p>
// *
// * @author turtle
// */
//public final class ApplicationServer extends TcpServer<ApplicationMessageHandler> {
//
//	private static final ApplicationServer INSTANCE = new ApplicationServer();
//
//	public static final ApplicationServer getInstance() {
//		return INSTANCE;
//	}
//
//	private ApplicationServer() {
//		super("Application Server", ApplicationMessageHandler.class);
//	}
//
//	@Override
//	public boolean listen() {
//		return this.listen(SystemConfig.getServicePort());
//	}
//
//}