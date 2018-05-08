package org.zeroref.borg.sagas.infra;

import org.zeroref.borg.sagas.domain.OrderPolicy;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class SagaPersistence {
    private final SagaStorage storage;
    private final EventDispatcher dispatcher = new EventDispatcher();
    private final Map<Class, Class> evtToSagaType = new HashMap<>();
    private final SagasMapping mapping = new SagasMapping();

    public SagaPersistence(SagaStorage storage) {
        this.storage = storage;
    }

    public void dispatch(Object o) {
        OrderPolicy orderPolicy = new OrderPolicy();
        dispatcher.applyHandle(o, orderPolicy);

        storage.store(orderPolicy.getState());
    }

    public void register(Class<? extends SagaBase> ... sagaTypes) {
        for(Class<?> cl : sagaTypes){
            try {
                Constructor<?> ctor = cl.getConstructor();
                SagaBase sagaInfo = (SagaBase)ctor.newInstance();
                sagaInfo.howToFindSaga(mapping);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
