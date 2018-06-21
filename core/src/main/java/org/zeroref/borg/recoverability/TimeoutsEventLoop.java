package org.zeroref.borg.recoverability;

import org.zeroref.borg.timeouts.TimeoutManager;

import java.io.Closeable;
import java.io.IOException;

public class TimeoutsEventLoop implements ManagedEventLoop, Closeable {
    private final TimeoutManager timeouts;
    private final Dispatcher forwardError;

    public TimeoutsEventLoop(TimeoutManager timeouts, Dispatcher forwardError) {
        this.timeouts = timeouts;
        this.forwardError = forwardError;
    }

    @Override
    public void start() {
        timeouts.addExpiredCallback(timeout -> forwardError.dispatch(timeout));
    }

    @Override
    public void close() {
        try {
            timeouts.close();
        } catch (IOException e) {

        }
    }
}
