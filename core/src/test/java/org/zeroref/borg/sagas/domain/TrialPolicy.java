package org.zeroref.borg.sagas.domain;

import org.zeroref.borg.sagas.SagaBase;
import org.zeroref.borg.sagas.SagasMapping;

public class TrialPolicy extends SagaBase<TrialPolicyState> {

    public void handle(TrialActivated ev){

        TrialPolicyState state = new TrialPolicyState();
        state.setSagaId(ev.getUserEmail());
        setState(state);

        System.out.println("Welcome!");
    }

    public void handle(TrialCancelled ev){
        System.out.println("Good luck!");
        getState().setCancelled(true);

    }

    public void handle(TrialExpired ev){
        markCompleted();
    }

    @Override
    public void howToFindSaga(SagasMapping mapping) {
        mapping.create(TrialActivated.class, activated -> activated.getUserEmail());
        mapping.map(TrialExpired.class, expired -> expired.getUserEmail());
        mapping.map(TrialCancelled.class, cancelled -> cancelled.getUserEmail());
    }
}
