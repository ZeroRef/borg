package org.zeroref.borg.timeouts;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimeoutManagerTest {

    ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(10);
    AtomicBoolean fired = new AtomicBoolean(false);

    @Test
    public void request_timeout_and_watch_fire() throws Exception {
        InMemoryTimeoutManager tm = new InMemoryTimeoutManager(scheduledService, new SystemClock());
        tm.addExpiredCallback(timeout -> fired.set(true));

        tm.requestTimeout("1", 1, TimeUnit.SECONDS, "t1", "");

        Assert.assertFalse(fired.get());
        Thread.sleep(1100);
        Assert.assertTrue(fired.get());
    }

    @Test
    public void cancel_will_not_fire() throws Exception {
        InMemoryTimeoutManager tm = new InMemoryTimeoutManager(scheduledService, new SystemClock());
        tm.addExpiredCallback(timeout -> fired.set(true));

        TimeoutId t1 = tm.requestTimeout("1", 1, TimeUnit.SECONDS, "t1", "");
        tm.cancelTimeout(t1);

        Assert.assertFalse(fired.get());
        Thread.sleep(1100);
        Assert.assertFalse(fired.get());
    }
}
