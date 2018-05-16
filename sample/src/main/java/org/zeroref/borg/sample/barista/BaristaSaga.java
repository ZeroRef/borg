package org.zeroref.borg.sample.barista;

import org.zeroref.borg.sagas.SagaBase;
import org.zeroref.borg.sagas.SagasMapping;
import org.zeroref.borg.sample.cashier.PaymentComplete;
import org.zeroref.borg.sample.cashier.PrepareDrink;

import java.util.UUID;

public class BaristaSaga extends SagaBase<BaristaSagaState> {

    public void handle(PrepareDrink message)
    {
        BaristaSagaState state = new BaristaSagaState();
        setState(state);
        state.setSagaId(message.correlationId.toString());
        state.setName(message.drinkName);

        for (int i = 0; i < 10; i++)
        {
            System.out.println("Barista: preparing drink: " + message.drinkName);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        state.setDrinkIsReady(true);
        SubmitOrderIfDone();
    }

    public void handle(PaymentComplete message)
    {
        System.out.println("Barista: got payment notification");
        getState().setGotPayment(true);
        SubmitOrderIfDone();
    }

    private void SubmitOrderIfDone()
    {
        BaristaSagaState state = getState();

        if (state.isGotPayment() && state.isDrinkIsReady())
        {
            System.out.println("Barista: drink is ready");

            UUID correlationId = UUID.fromString(state.getSagaId());
            bus.publish(new DrinkReady(correlationId, state.getName()));
        }
    }

    @Override
    public void howToFindSaga(SagasMapping mapping) {
        mapping.create(PrepareDrink.class, prep -> String.valueOf(prep.correlationId));
        mapping.map(PaymentComplete.class, pay -> String.valueOf(pay.correlationId));
    }
}
