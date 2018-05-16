package org.zeroref.borg.sagas;

import org.zeroref.borg.MessageBus;

public abstract class SagaBase<STATE extends SagaState> {
    private boolean completed = false;
    private STATE state;
    private MessageBus bus;

    protected void markCompleted(){
        completed = true;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public abstract void howToFindSaga(SagasMapping mapping);

    public boolean isCompleted() {
        return completed;
    }

    public void setBus(MessageBus bus) {
        this.bus = bus;
    }
}
