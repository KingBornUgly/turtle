/**
 * Copyright(c) 2018 Sunyur.com, All Rights Reserved. Author: KingBornUgly Create date: 2023/1/3
 */
package com.turtle.exception;

/**
 * xxxxx
 * @author KingBornUgly
 * @date 2023/1/3 5:43 PM
 */
public class NetException extends Exception {
    private static final long serialVersionUID = 1L;

    public NetException() {
        super("网络异常");
    }

    public NetException(String message) {
        super(message);
    }

    public NetException(Throwable cause) {
        super(cause);
    }

    public NetException(String message, Throwable cause) {
        super(message, cause);
    }
}
