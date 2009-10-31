package com.jdevelop.gmailsync.cli.facade.transport;

import com.jdevelop.gmailsync.core.email.EmailMessage;
import com.jdevelop.gmailsync.core.exception.handler.GenericExceptionHandler;
import com.jdevelop.gmailsync.core.transport.TransportInterface;
import com.jdevelop.gmailsync.core.transport.exception.MessageTransferException;
import com.jdevelop.gmailsync.core.transport.exception.TransportException;

public class RetryingTransportImpl implements TransportInterface {

    private int currentDelayMS;

    private final int tries;

    private final int delayValueMS;

    private final TransportInterface transport;

    public RetryingTransportImpl(int tries, int delayValueMS,
            TransportInterface transport) {
        this.tries = tries;
        this.delayValueMS = this.currentDelayMS = delayValueMS;
        this.transport = transport;
    }

    @Override
    public void uploadMessage(EmailMessage message) throws TransportException {
        TransportException lastException = null;
        for (int i = 0; i < tries; i++) {
            try {
                transport.uploadMessage(message);
                lastException = null;
                resetState();
                break;
            } catch (TransportException e) {
                lastException = e;
                try {
                    System.err.println("Sleeping for " + currentDelayMS);
                    Thread.sleep(currentDelayMS);
                } catch (InterruptedException e1) {
                    GenericExceptionHandler
                            .handleException(new MessageTransferException(e));
                }
                currentDelayMS *= 2;
            }
        }
        if (lastException != null)
            throw lastException;
    }

    private void resetState() {
        currentDelayMS = delayValueMS;
    }

}
