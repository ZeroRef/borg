package org.zeroref.borg.sagas.domain;

import org.zeroref.borg.sagas.SagaWiredTest;
import org.zeroref.borg.sagas.SagaBase;
import org.zeroref.borg.sagas.SagaState;
import org.zeroref.borg.sagas.SagasMapping;

public class MiniSaga extends SagaBase<MiniSagaState> {

    public MiniSaga() {
    }

    public void handle(Booom evt){
        MiniSagaState state = new MiniSagaState();
        state.setSagaId(evt.getId());
        setState(state);
        MiniSagaState.called = true;
    }

    @Override
    public void howToFindSaga(SagasMapping mapping) {
        mapping.create(Booom.class, booom -> booom.getId());
    }
}


