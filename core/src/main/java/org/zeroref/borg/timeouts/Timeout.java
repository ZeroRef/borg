package org.zeroref.borg.timeouts;

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.Date;

public final class Timeout implements Serializable {
    private static final long serialVersionUID = 1L;

    private TimeoutId id;
    private String sagaId;
    private Date expiredAt;
    private String name;
    private Object data;

    private Timeout() {
    }

    public TimeoutId getId() {
        return id;
    }

    public String getSagaId() {
        return sagaId;
    }

    public Date getExpiredAt() {
        return expiredAt != null ? new Date(expiredAt.getTime()) : null;
    }

    public String getName() {
        return name;
    }

    public Object getData() {
        return data;
    }

    public static Timeout create(final TimeoutId id, final String sagaId, final String name, final Date expiredAt) {
        return Timeout.create(id, sagaId, name, expiredAt, null);
    }

    public static Timeout create(final TimeoutId id, final String sagaId, final String name, final Date expiredAt, final Object data) {
        Timeout timeout = new Timeout();
        timeout.id = id;
        timeout.sagaId = sagaId;
        timeout.expiredAt = expiredAt;
        timeout.name = name;
        timeout.data = data;

        return timeout;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("sagaId", sagaId)
                .add("expiredAt", expiredAt)
                .add("name", name)
                .add("data", data)
                .toString();
    }
}
