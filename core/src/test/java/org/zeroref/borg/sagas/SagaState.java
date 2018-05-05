package org.zeroref.borg.sagas;

import java.util.HashSet;
import java.util.Set;

public class SagaState<KEY> {

    private String sagaId;
    private String type;

    private final Set<KEY> instanceKeys = new HashSet<>(8);


    public String getSagaId() {
        return sagaId;
    }

    public void setSagaId(String sagaId) {
        this.sagaId = sagaId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }





    public Set<KEY> instanceKeys(){
        return instanceKeys;
    }


    public void addInstanceKey(final KEY key) {
        instanceKeys.add(key);
    }

    public void removeInstanceKey(final KEY key) {
        instanceKeys.remove(key);
    }

    public void clearInstanceKeys() {
        instanceKeys.clear();
    }
}
