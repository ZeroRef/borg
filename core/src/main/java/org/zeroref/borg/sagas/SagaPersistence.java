package org.zeroref.borg.sagas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroref.borg.MessageBus;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SagaPersistence {
    private final SagaStorage storage;
    private final EventDispatcher dispatcher = new EventDispatcher();
    private final Map<Class, Class> evtToSagaType = new HashMap<>();
    private final Map<Class, List<Class>> confSagas = new HashMap<>();
    private final SagasMapping mapping = new SagasMapping();

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaPersistence.class);

    public SagaPersistence(SagaStorage storage) {
        this.storage = storage;
    }

    public void dispatch(MessageBus messageBus, Object o) {
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

        saga.setBus(messageBus);
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

                if(!confSagas.containsKey(sagaType)){
                    confSagas.put(sagaType, new ArrayList<>());
                }

                Constructor<?> ctor = sagaType.getConstructor();
                SagaBase sagaInfo = (SagaBase)ctor.newInstance();
                sagaInfo.howToFindSaga(mapping);

                SagasMapping discoverMapping = new SagasMapping();
                sagaInfo.howToFindSaga(discoverMapping);

                for(Class<?> evType : discoverMapping.mapExisting.keySet()){
                    evtToSagaType.put(evType, sagaType);
                    confSagas.get(sagaType).add(evType);
                }

                for(Class<?> evType : discoverMapping.mapCreate.keySet()){
                    evtToSagaType.put(evType, sagaType);
                    confSagas.get(sagaType).add(evType);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isSagaSubscription(Object evt) {
        return evtToSagaType.containsKey(evt.getClass());
    }

    public Map<Class, List<Class>> getConfSagas() {
        return confSagas;
    }
}
