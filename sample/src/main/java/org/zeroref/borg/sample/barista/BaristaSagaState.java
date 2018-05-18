package org.zeroref.borg.sample.barista;

import org.zeroref.borg.sagas.SagaState;

public class BaristaSagaState extends SagaState {
    private String name;
    private boolean drinkIsReady;
    private boolean gotPayment;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDrinkIsReady(boolean drinkIsReady) {
        this.drinkIsReady = drinkIsReady;
    }

    public boolean isDrinkIsReady() {
        return drinkIsReady;
    }

    public void setGotPayment(boolean gotPayment) {
        this.gotPayment = gotPayment;
    }

    public boolean isGotPayment() {
        return gotPayment;
    }
}
