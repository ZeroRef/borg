package org.zeroref.borg.sagas.domain;

import org.zeroref.borg.sagas.SagaBase;
import org.zeroref.borg.sagas.domain.OrderPlaced;
import org.zeroref.borg.sagas.infra.FunctionKeyReader;
import org.zeroref.borg.sagas.infra.KeyReader;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class OrderPolicy extends SagaBase<OrderPolicyState> {

    public void handle(OrderPlaced evt){

    }

    @Override
    public Collection<KeyReader> keyReaders() {
        KeyReader reader = FunctionKeyReader.create(
                OrderPlaced.class,
                OrderPlaced::getOrderId
        );

        Set<KeyReader> readers = Collections.emptySet();
        readers.add(reader);
        return readers;
    }
}
