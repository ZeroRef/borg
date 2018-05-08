package org.zeroref.borg.sagas.domain;

import org.zeroref.borg.sagas.infra.SagaBase;
import org.zeroref.borg.sagas.infra.SagasMapping;

public class TrialPolicy extends SagaBase<OrderPolicyState> {

    public void handle(TrialActivated ev){
        System.out.println("Welcome!");
    }

    public void handle(TrialCancelled ev){
        System.out.println("Good luck!");
    }

    public void handle(TrialExpired ev){
        markCompleted();
    }

    @Override
    public void howToFindSaga(SagasMapping mapping) {
        mapping.map(TrialActivated.class, trialActivated -> trialActivated.getUserEmail());
        mapping.map(TrialExpired.class, trialExpired -> trialExpired.getUserEmail());
        mapping.map(TrialCancelled.class, trialExpired -> trialExpired.getUserEmail());
    }
}
