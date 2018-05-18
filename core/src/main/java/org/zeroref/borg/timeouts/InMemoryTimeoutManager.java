package org.zeroref.borg.timeouts;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class InMemoryTimeoutManager implements TimeoutManager, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryTimeoutManager.class);
    private static final int TIMER_THREAD_POOL_SIZE = 2;
    private final Object sync = new Object();

    private final Collection<TimeoutExpired> callbacks = Collections.synchronizedCollection(new ArrayList<>());
    private final Table<TimeoutId, String, ScheduledFuture> openTimeouts = HashBasedTable.create();

    private final ScheduledExecutorService scheduledService;
    private final Clock clock;

    public InMemoryTimeoutManager() {
        this(TIMER_THREAD_POOL_SIZE);
    }

    public InMemoryTimeoutManager(final int timerThreadPoolSize) {
        this.scheduledService = Executors.newScheduledThreadPool(
                timerThreadPoolSize,
                r -> {
                    Thread thread = new Thread(r, "saga-timeout-");
                    thread.setDaemon(true);
                    return thread;
                });
        this.clock = new SystemClock();
    }

    public InMemoryTimeoutManager(final ScheduledExecutorService scheduledService, final Clock clock) {
        this.scheduledService = scheduledService;
        this.clock = clock;
    }

    @Override
    public void addExpiredCallback(final TimeoutExpired callback) {
        callbacks.add(callback);
    }

    @Override
    public TimeoutId requestTimeout(final String sagaId, final long delay, final TimeUnit timeUnit, final String name,
                                    final Object data) {
        TimeoutId id = TimeoutId.generateNewId();

        SagaTimeoutTask timeoutTask = new SagaTimeoutTask(id, sagaId, name, timeout -> timeoutExpired(timeout), clock, data);
        ScheduledFuture future = scheduledService.schedule(timeoutTask, delay, timeUnit);

        synchronized (sync) {
            openTimeouts.put(id, sagaId, future);
        }

        return id;
    }

    @Override
    public void cancelTimeouts(final String sagaId) {
        synchronized (sync) {
            Collection<TimeoutId> timeoutsToRemove = new ArrayList<>();
            Map<TimeoutId, ScheduledFuture> sagaTimeouts = openTimeouts.column(sagaId);

            for (Map.Entry<TimeoutId, ScheduledFuture> timeout : sagaTimeouts.entrySet()) {
                timeout.getValue().cancel(false);
                timeoutsToRemove.add(timeout.getKey());
            }

            for (TimeoutId idToRemove : timeoutsToRemove) {
                openTimeouts.remove(idToRemove, sagaId);
            }
        }
    }

    @Override
    public void cancelTimeout(final TimeoutId id) {
        synchronized (sync) {
            Map<String, ScheduledFuture> timeouts = openTimeouts.row(id);
            Collection<String> sagaIdForRemoval = new ArrayList<>(1);

            for (Map.Entry<String, ScheduledFuture> timeout : timeouts.entrySet()) {
                timeout.getValue().cancel(false);
                sagaIdForRemoval.add(timeout.getKey());
            }

            for (String sagaId : sagaIdForRemoval) {
                openTimeouts.remove(id, sagaId);
            }
        }
    }

    private void timeoutExpired(final Timeout timeout) {
        try {
            removeExpiredTimeout(timeout);

            for (TimeoutExpired callback : callbacks) {
                callback.expired(timeout);
            }
        } catch (Exception ex) {
            LOG.error("Error handling timeout.", ex);
        }
    }

    private void removeExpiredTimeout(final Timeout timeout) {
        synchronized (sync) {
            openTimeouts.remove(timeout.getId(), timeout.getSagaId());
        }
    }

    @Override
    public void close() {
        scheduledService.shutdown();
    }
}
