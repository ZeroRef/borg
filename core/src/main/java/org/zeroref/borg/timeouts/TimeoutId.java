package org.zeroref.borg.timeouts;

import java.io.Serializable;
import java.util.UUID;

public final class TimeoutId implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;

    public TimeoutId(final UUID id) {
        this.id = id;
    }

    public static TimeoutId generateNewId() {
        return new TimeoutId(UUID.randomUUID());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        boolean isEqual = false;
        if (this == obj) {
            isEqual = true;
        } else if (obj instanceof TimeoutId) {
            isEqual = id.equals(((TimeoutId) obj).id);
        }

        return isEqual;
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
