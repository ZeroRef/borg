package org.zeroref.borg.sagas;

import org.zeroref.borg.MessageBus;
import org.zeroref.borg.timeouts.TimeoutManager;

public abstract class SagaBase<STATE extends SagaState> {
    private boolean completed = false;
    private STATE state;
    protected MessageBus bus;
    protected TimeoutManager timeouts;

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

    public void setTimeouts(TimeoutManager timeouts) {
        this.timeouts = timeouts;
    }
}
