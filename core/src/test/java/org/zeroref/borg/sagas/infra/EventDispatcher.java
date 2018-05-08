package org.zeroref.borg.sagas.infra;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EventDispatcher {
    private static Map<String, Method> mutatorMethods =  new HashMap<String, Method>();
    private static final String MUTATOR_METHOD_NAME = "handle";

    public void applyHandle(Object aDomainEvent, Object saga) {

        Class<?> rootType = saga.getClass();
        Class<?> eventType = aDomainEvent.getClass();

        String key = rootType.getName() + ":" + eventType.getName();

        Method mutatorMethod = mutatorMethods.get(key);

        if (mutatorMethod == null) {
            mutatorMethod = this.cacheMutatorMethodFor(key, rootType, eventType);
        }

        try {
            mutatorMethod.invoke(saga, aDomainEvent);

        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                throw new RuntimeException(
                        "Method "
                                + MUTATOR_METHOD_NAME
                                + "("
                                + eventType.getSimpleName()
                                + ") failed. See cause: "
                                + e.getMessage(),
                        e.getCause());
            }

            throw new RuntimeException(
                    "Method "
                            + MUTATOR_METHOD_NAME
                            + "("
                            + eventType.getSimpleName()
                            + ") failed. See cause: "
                            + e.getMessage(),
                    e);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                    "Method "
                            + MUTATOR_METHOD_NAME
                            + "("
                            + eventType.getSimpleName()
                            + ") failed because of illegal access. See cause: "
                            + e.getMessage(),
                    e);
        }
    }

    private Method cacheMutatorMethodFor(
            String aKey,
            Class<?> aRootType,
            Class<?> anEventType) {

        synchronized (mutatorMethods) {
            try {
                Method method = this.hiddenOrPublicMethod(aRootType, anEventType);

                method.setAccessible(true);

                mutatorMethods.put(aKey, method);

                return method;

            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "I do not understand "
                                + MUTATOR_METHOD_NAME
                                + "("
                                + anEventType.getSimpleName()
                                + ") because: "
                                + e.getClass().getSimpleName() + ">>>" + e.getMessage(),
                        e);
            }
        }
    }

    private Method hiddenOrPublicMethod(
            Class<?> aRootType,
            Class<?> anEventType)
            throws Exception {

        Method method = null;

        try {

            // assume protected or private...

            method = aRootType.getDeclaredMethod(
                    MUTATOR_METHOD_NAME,
                    anEventType);

        } catch (Exception e) {

            // then public...

            method = aRootType.getMethod(
                    MUTATOR_METHOD_NAME,
                    anEventType);
        }

        return method;
    }
}
