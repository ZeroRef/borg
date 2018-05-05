package org.zeroref.borg.sagas.infra;

import java.util.HashMap;
import java.util.Map;

public class SagasMapping {
    public Map<Class, SagasMappingKey<Object>> map = new HashMap<>();

    public <T> void map(Class<T> evtType, SagasMappingKey<T> funcToSagaId) {
        if(!map.containsKey(evtType)){
            map.put(evtType, o -> funcToSagaId.key((T) o));
        }
    }

    public String readKey(Object message){
        SagasMappingKey<Object> mappingKey = map.get(message.getClass());
        return mappingKey.key(message);
    }

    public interface SagasMappingKey<MESSAGE> {
        String key(MESSAGE message);
    }
}
