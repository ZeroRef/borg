package org.zeroref.borg.timeouts;

import java.util.Date;

public class SystemClock implements Clock {
    @Override
    public Date now() {
        return new Date();
    }
}
