package org.zeroref.borg.timeouts.domain;

import org.zeroref.borg.sagas.SagaState;

public class OrderCancellationPolicyState extends SagaState {
    private int qualifiedRefund;
    private boolean cancelled;

    public void setQualifiedRefund(int qualifiedRefund) {
        this.qualifiedRefund = qualifiedRefund;
    }

    public int getQualifiedRefund() {
        return qualifiedRefund;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
