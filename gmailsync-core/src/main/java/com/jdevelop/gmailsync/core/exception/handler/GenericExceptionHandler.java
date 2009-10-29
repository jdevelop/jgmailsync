package com.jdevelop.gmailsync.core.exception.handler;

import java.util.ArrayList;
import java.util.List;

public class GenericExceptionHandler {

    private static final List<ExceptionHandler<Throwable>> handlers = new ArrayList<ExceptionHandler<Throwable>>();

    public static <T extends Throwable> void handleException(T exeption)
            throws T {
        boolean rethrowException = true;
        for (ExceptionHandler<Throwable> handler : handlers) {
            if (handler.canHandle(exeption)) {
                if (handler.processException(exeption))
                    rethrowException = false;
            }
        }
        if (rethrowException)
            throw exeption;
    }

    public static void registerExceptionHandler(
            ExceptionHandler<Throwable> handler) {
        handlers.add(handler);
    }

    public static void unregisterExceptionHandler(
            ExceptionHandler<Throwable> handler) {
        handlers.remove(handler);
    }

}
