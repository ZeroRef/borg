package org.zeroref.borg.timeouts;

public class SagaTimeoutTask implements Runnable {
    private final TimeoutId timeoutId;
    private final String sagaId;
    private final String name;
    private final TimeoutExpired expiredCallback;
    private final Clock clock;
    private final Object data;

    public SagaTimeoutTask(
            final TimeoutId timeoutId,
            final String sagaId,
            final String name,
            final TimeoutExpired expiredCallback,
            final Clock clock,
            final Object data) {
        this.timeoutId = timeoutId;
        this.sagaId = sagaId;
        this.name = name;
        this.expiredCallback = expiredCallback;
        this.clock = clock;
        this.data = data;
    }

    @Override
    public void run() {
        Timeout timeout = Timeout.create(timeoutId, sagaId, name, clock.now(), data);
        expiredCallback.expired(timeout);
    }
}
