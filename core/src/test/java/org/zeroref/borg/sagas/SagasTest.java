package org.zeroref.borg.sagas;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.sagas.domain.OrderPlaced;
import org.zeroref.borg.sagas.domain.OrderPolicy;
import org.zeroref.borg.sagas.domain.OrderPolicyState;
import org.zeroref.borg.sagas.domain.TrialPolicy;
import org.zeroref.borg.sagas.infra.*;

public class SagasTest {

    @Test
    public void mapping_def() {
        SagasMapping mapping = new SagasMapping();
        mapping.map(OrderPlaced.class, OrderPlaced::getOrderId);

        OrderPlaced order = new OrderPlaced("22");

        Assert.assertEquals(mapping.readKey(order), "22");
    }

    @Test
    public void persistence_not_found_def() {
        SagaStorage persistence = new SagaStorage();
        SagaState saga = persistence.getById("22");

        Assert.assertNull(saga);
    }

    @Test
    public void persistence_get_by_id_def() {
        SagaStorage persistence = new SagaStorage();
        persistence.sagas.put("22", new OrderPolicyState());

        Object saga = persistence.getById("22");

        Assert.assertNotNull(saga);
    }

    @Test
    public void persistence_save_def() {
        SagaStorage persistence = new SagaStorage();

        OrderPolicyState state = new OrderPolicyState();
        state.setSagaId("22");

        persistence.store(state);

        Assert.assertTrue(persistence.sagas.containsKey("22"));
    }

    @Test
    public void dispatch_test() {
        EventDispatcher dispatcher = new EventDispatcher();
        OrderPolicy orderPolicy = new OrderPolicy();

        dispatcher.applyHandle(new OrderPlaced("ORD2014"), orderPolicy);

        OrderPolicyState state = orderPolicy.getState();
        Assert.assertEquals(state.getSagaId(), "ORD2014");
    }

    @Test
    public void register_mapping() {
        SagaStorage storage = new SagaStorage();
        SagaPersistence persistence = new SagaPersistence(storage);
        persistence.register(OrderPolicy.class, TrialPolicy.class);


        //Assert.assertTrue(storage.sagas.containsKey("ORD2014"));
    }

    @Test
    public void wip_def() {
        SagaStorage storage = new SagaStorage();
        SagaPersistence persistence = new SagaPersistence(storage);


        persistence.dispatch(new OrderPlaced("ORD2014"));

        Assert.assertTrue(storage.sagas.containsKey("ORD2014"));
    }


    /*
    * how it works
    *
    *
    * - wire persistence
    * - read all saga definitions
    *       + diagnose what starts a saga
    *       + diagnose parity between mapping and handlers
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
