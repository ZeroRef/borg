package org.zeroref.borg.sagas.domain;

import org.zeroref.borg.sagas.infra.SagaBase;
import org.zeroref.borg.sagas.infra.SagasMapping;

public class OrderPolicy extends SagaBase<OrderPolicyState> {

    public void handle(OrderPlaced evt){
        OrderPolicyState state = new OrderPolicyState();
        state.setSagaId(evt.getOrderId());

        setState(state);
    }

    @Override
    public void howToFindSaga(SagasMapping mapping) {
        mapping.map(OrderPlaced.class, OrderPlaced::getOrderId);
    }
}
