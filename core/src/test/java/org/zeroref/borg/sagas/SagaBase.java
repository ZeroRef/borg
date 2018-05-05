package org.zeroref.borg.sagas;

import org.zeroref.borg.sagas.infra.KeyReader;

import java.util.Collection;

public abstract class SagaBase<STATE extends SagaState> {
    private boolean completed = false;
    private STATE state;

    protected void markCompleted(){
        completed = true;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public abstract Collection<KeyReader> keyReaders();
}
