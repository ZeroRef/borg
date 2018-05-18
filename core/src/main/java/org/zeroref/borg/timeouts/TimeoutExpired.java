package org.zeroref.borg.timeouts;

public interface TimeoutExpired {
    void expired(Timeout timeout);
}
