package org.zeroref.borg.timeouts;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

public interface TimeoutManager extends Closeable {
    void addExpiredCallback(TimeoutExpired callback);

    TimeoutId requestTimeout(String sagaId, long delay, TimeUnit timeUnit, String name, Object data);

    void cancelTimeouts(String sagaId);
    void cancelTimeout(final TimeoutId id);
}
