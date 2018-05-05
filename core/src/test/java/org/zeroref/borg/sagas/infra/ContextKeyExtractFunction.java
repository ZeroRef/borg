package org.zeroref.borg.sagas.infra;

import javax.annotation.Nullable;

public interface ContextKeyExtractFunction<MESSAGE, KEY> {
    /**
     * Returns the key value to identify a running saga.
     */
    KEY key(MESSAGE message);
}
