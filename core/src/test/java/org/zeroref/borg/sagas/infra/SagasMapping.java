package org.zeroref.borg.sagas.infra;

import java.util.HashMap;
import java.util.Map;

public class SagasMapping {
    public Map<Class, SagasMappingKey<Object>> mapExisting = new HashMap<>();
    public Map<Class, SagasMappingKey<Object>> mapCreate = new HashMap<>();

    public <T> void map(Class<T> evtType, SagasMappingKey<T> funcToSagaId) {
        if(!mapExisting.containsKey(evtType)){
            mapExisting.put(evtType, o -> funcToSagaId.key((T) o));
        }
    }

    public <T> void create(Class<T> evtType, SagasMappingKey<T> funcToSagaId) {
        if(!mapCreate.containsKey(evtType)){
            mapCreate.put(evtType, o -> funcToSagaId.key((T) o));
        }
    }

    public String readKey(Object message){
        Class<?> aClass = message.getClass();

        if(mapCreate.containsKey(aClass)){
            SagasMappingKey<Object> mappingKey = mapCreate.get(message.getClass());
            return mappingKey.key(message);
        }

        if(mapExisting.containsKey(aClass)){
            SagasMappingKey<Object> mappingKey = mapExisting.get(message.getClass());
            return mappingKey.key(message);
        }

        throw new RuntimeException("There is no saga mapping configured for " + aClass);
    }

    public boolean isCreational(Class<?> evtType) {
        return mapCreate.containsKey(evtType);
    }

    public interface SagasMappingKey<MESSAGE> {
        String key(MESSAGE message);
    }
}
