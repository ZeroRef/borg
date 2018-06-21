package org.zeroref.borg.timeouts.domain;

import org.zeroref.borg.sagas.SagaBase;
import org.zeroref.borg.sagas.SagasMapping;

import java.util.concurrent.TimeUnit;

public class OrderCancellationPolicy extends SagaBase<OrderCancellationPolicyState> {

    public OrderCancellationPolicy() {
    }

    public void handle(OrderBilled evt){
        OrderCancellationPolicyState state = new OrderCancellationPolicyState();
        state.setSagaId(evt.getId());
        state.setQualifiedRefund(100);
        setState(state);

        timeouts.requestTimeout(
                evt.getId().toString(),
                1, TimeUnit.SECONDS,
                "",
                new FullRefundExpiredTimeout(evt.getId()));

        timeouts.requestTimeout(
                evt.getId().toString(),
                5, TimeUnit.SECONDS,
                "",
                new PartialRefundExpiredTimeout(evt.getId()));}

    public void handle(OrderShipped evt){
        OrderCancellationPolicyState state = getState();
    }

    public void handle(CancelOrder cmd){
        OrderCancellationPolicyState state = getState();
        state.setCancelled(true);

        bus.publish(new OrderRefundAllowance(state.getQualifiedRefund()));

        markCompleted();
    }

    public void handle(FullRefundExpiredTimeout cmd){
        OrderCancellationPolicyState state = getState();
        state.setQualifiedRefund(70);
    }

    public void handle(PartialRefundExpiredTimeout cmd){
        OrderCancellationPolicyState state = getState();
        state.setQualifiedRefund(30);
    }

    @Override
    public void howToFindSaga(SagasMapping mapping) {
        mapping.create(OrderBilled.class, bill -> bill.getId());
        mapping.map(OrderShipped.class, shipping -> shipping.getId());
        mapping.map(CancelOrder.class, attempt -> attempt.getId());
        mapping.map(FullRefundExpiredTimeout.class, t1 -> t1.getId());
        mapping.map(PartialRefundExpiredTimeout.class, t2 -> t2.getId());
    }
}
