package org.zeroref.borg.sample.cashier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroref.borg.MessageBus;
import org.zeroref.borg.sagas.SagaBase;
import org.zeroref.borg.sagas.SagasMapping;
import org.zeroref.borg.sample.barista.BaristaSaga;
import org.zeroref.borg.sample.barista.BaristaSagaState;

import java.util.UUID;

public class CashierSaga extends SagaBase<CashierSagaState> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CashierSaga.class);

    public void handle(NewOrder message){
        LOGGER.info("Cashier: got new order");

        CashierSagaState state = new CashierSagaState();
        state.setSagaId(message.correlationId);
        setState(state);

        bus.publish(new PrepareDrink(
                message.drinkName,
                message.size,
                message.customerName,
                message.correlationId
        ));

        bus.reply(new PaymentDue(
                message.customerName,
                message.correlationId,
                message.size * 1.25
        ));
    }

    public void handle(SubmitPayment message){
        LOGGER.info("Cashier: got payment");
        bus.publish(new PaymentComplete(message.correlationId));
    }

    @Override
    public void howToFindSaga(SagasMapping mapping) {
        mapping.create(NewOrder.class, order -> String.valueOf(order.correlationId));
        mapping.map(SubmitPayment.class, payment -> String.valueOf(payment.correlationId));
    }
}
