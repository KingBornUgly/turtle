/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle.exception;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 5:42 PM
 */
public class DownloadException extends Exception {
    private static final long serialVersionUID = 1L;

    public DownloadException() {
        super("下载异常");
    }

    public DownloadException(String message) {
        super(message);
    }

    public DownloadException(Throwable cause) {
        super(cause);
    }

    public DownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
