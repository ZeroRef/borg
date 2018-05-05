package org.zeroref.borg.sagas.infra;

import javax.annotation.Nullable;

public interface KeyExtractFunction<MESSAGE, KEY> {
    KEY key(MESSAGE message);
}
