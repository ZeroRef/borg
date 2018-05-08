package org.zeroref.borg.sagas.infra;

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
        String sagaId = mapping.readKey(o);

        SagaBase saga = getSagaById(sagaId, o);
        dispatcher.applyHandle(o, saga);

        storage.store(saga.getState());
    }

    public SagaBase getSagaById(String sagaId, Object typeHint) {
        SagaState state = storage.getById(sagaId);

        SagaBase saga = null;

        if(state == null){
            Class sagaType = evtToSagaType.get(typeHint.getClass());
            saga = constructSagaByType(sagaType.getTypeName());
        }else {
            saga = constructSagaByType(state.getType());
        }

        return saga;
    }

    private SagaBase constructSagaByType(String type) {
        try{
            Class<?> clazz = Class.forName(type);
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

                for(Class<?> evType : discoverMapping.map.keySet()){
                    evtToSagaType.put(evType, sagaType);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
