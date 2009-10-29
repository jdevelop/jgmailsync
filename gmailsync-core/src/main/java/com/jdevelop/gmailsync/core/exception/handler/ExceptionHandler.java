package com.jdevelop.gmailsync.core.exception.handler;

public interface ExceptionHandler<T extends Throwable> {

    boolean canHandle(T exception);

    boolean processException(T exception);

}
