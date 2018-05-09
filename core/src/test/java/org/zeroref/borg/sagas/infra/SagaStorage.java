package org.zeroref.borg.sagas.infra;

import java.util.HashMap;
import java.util.Map;

public class SagaStorage {

    public Map<String, SagaState> sagas = new HashMap<>();

    public SagaState getById(String sagaId) {
        return sagas.get(sagaId);
    }

    public void store(SagaState state) {
        sagas.put(state.getSagaId(), state);
    }

    public void discontinue(String sagaId) {
        sagas.remove(sagaId);
    }
}
