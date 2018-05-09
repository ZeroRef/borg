package org.zeroref.borg.sagas;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.sagas.domain.*;
import org.zeroref.borg.sagas.infra.*;

public class SagasTest {

    @Test
    public void mapping_red_key() {
        SagasMapping mapping = new SagasMapping();
        mapping.map(OrderPlaced.class, OrderPlaced::getOrderId);

        Assert.assertEquals(mapping.readKey(new OrderPlaced("22")), "22");
    }

    @Test
    public void storage_state_not_found_null() {
        SagaStorage storage = new SagaStorage();

        Assert.assertNull(storage.getById("22"));
    }

    @Test
    public void storage_get_by_id() {
        SagaStorage storage = new SagaStorage();
        storage.sagas.put("22", new OrderPolicyState());

        Assert.assertNotNull(storage.getById("22"));
    }

    @Test
    public void storage_save() {
        SagaStorage storage = new SagaStorage();

        OrderPolicyState state = new OrderPolicyState();
        state.setSagaId("22");

        storage.store(state);

        Assert.assertTrue(storage.sagas.containsKey("22"));
    }

    @Test
    public void dispatch_test() {
        EventDispatcher dispatcher = new EventDispatcher();
        OrderPolicy orderPolicy = new OrderPolicy();

        dispatcher.applyHandle(new OrderPlaced("ORD2014"), orderPolicy);

        Assert.assertEquals(orderPolicy.getState().getSagaId(), "ORD2014");
    }

    @Test
    public void dispatch_with_map_will_apply_to_existing() {
        SagaStorage storage = new SagaStorage();
        TrialPolicyState policyState = new TrialPolicyState();
        policyState.setSagaId("user@gmail.com");
        policyState.setType(TrialPolicy.class.getTypeName());
        storage.sagas.put("user@gmail.com", policyState);

        SagaPersistence persistence = new SagaPersistence(storage);
        persistence.register(TrialPolicy.class);

        TrialCancelled o = new TrialCancelled();
        o.setUserEmail("user@gmail.com");
        persistence.dispatch(o);

        Assert.assertTrue(policyState.isCancelled());
    }

    @Test
    public void dispatch_with_map_will_no_op() {
        SagaStorage storage = new SagaStorage();
        SagaPersistence persistence = new SagaPersistence(storage);
        persistence.register(TrialPolicy.class);

        TrialCancelled o = new TrialCancelled();
        o.setUserEmail("user@gmail.com");
        persistence.dispatch(o);
    }

    @Test
    public void dispatch_with_create_mapping_will_create_saga_if_missing() {
        SagaStorage storage = new SagaStorage();

        SagaPersistence persistence = new SagaPersistence(storage);
        persistence.register(TrialPolicy.class);

        TrialActivated o = new TrialActivated();
        o.setUserEmail("user@gmail.com");
        persistence.dispatch(o);


        Assert.assertNotNull(storage.sagas.get("user@gmail.com"));
    }

    @Test
    public void dispatch_for_mark_completed_will_purge_storage() {
        SagaStorage storage = new SagaStorage();
        TrialPolicyState policyState = new TrialPolicyState();
        policyState.setSagaId("user@gmail.com");
        policyState.setType(TrialPolicy.class.getTypeName());
        storage.sagas.put("user@gmail.com", policyState);

        SagaPersistence persistence = new SagaPersistence(storage);
        persistence.register(TrialPolicy.class);

        TrialExpired o = new TrialExpired();
        o.setUserEmail("user@gmail.com");
        persistence.dispatch(o);


        Assert.assertEquals(storage.sagas.size(), 0);
    }








    /*
    * how it works
    *
    *
    * - wire persistence
    * - read all saga definitions
    *       + diagnose what starts a saga
    *       + diagnose parity between mapping_red_key and handlers
    *       + read all mappings
    *
    * - on message
    *       is a saga subscription
    *           saga = get saga by id ?? new saga for id
    *
    *           apply message
    *
    *           store saga
    *
    *
    * */
}
