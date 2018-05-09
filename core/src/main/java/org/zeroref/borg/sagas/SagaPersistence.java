package org.zeroref.borg.sagas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class SagaPersistence {
    private final SagaStorage storage;
    private final EventDispatcher dispatcher = new EventDispatcher();
    private final Map<Class, Class> evtToSagaType = new HashMap<>();
    private final SagasMapping mapping = new SagasMapping();

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaPersistence.class);

    public SagaPersistence(SagaStorage storage) {
        this.storage = storage;
    }

    public void dispatch(Object o) {
        String sagaId = mapping.readKey(o);
        Class sagaType = evtToSagaType.get(o.getClass());

        SagaState initialState = storage.getById(sagaId);
        SagaBase saga;

        if (initialState != null) {
            saga = constructSagaByType(sagaType);
            saga.setState(initialState);
        } else if (initialState == null && mapping.isCreational(o.getClass())) {
            saga = constructSagaByType(sagaType);
        }else {
            LOGGER.warn("Saga {} id#{} missing or has been terminated", sagaType,sagaId);
            return;
        }

        dispatcher.applyHandle(o, saga);

        if(saga.isCompleted()){
            storage.discontinue(sagaId);
            return;
        }

        storage.store(saga.getState());
    }

    private SagaBase constructSagaByType(Class clazz) {
        try{
            Constructor<?> ctor = clazz.getConstructor();
            return  (SagaBase) ctor.newInstance();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public void register(Class<? extends SagaBase> ... sagaTypes) {
        for(Class<?> sagaType : sagaTypes){
            try {
                Constructor<?> ctor = sagaType.getConstructor();
                SagaBase sagaInfo = (SagaBase)ctor.newInstance();
                sagaInfo.howToFindSaga(mapping);

                SagasMapping discoverMapping = new SagasMapping();
                sagaInfo.howToFindSaga(discoverMapping);

                for(Class<?> evType : discoverMapping.mapExisting.keySet()){
                    evtToSagaType.put(evType, sagaType);
                }

                for(Class<?> evType : discoverMapping.mapCreate.keySet()){
                    evtToSagaType.put(evType, sagaType);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
