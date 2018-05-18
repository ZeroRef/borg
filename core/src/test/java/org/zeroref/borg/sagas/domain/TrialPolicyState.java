package org.zeroref.borg.sagas.domain;

import org.zeroref.borg.sagas.SagaState;

public class TrialPolicyState extends SagaState {
    private boolean isCancelled;

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean expired) {
        isCancelled = expired;
    }
}
