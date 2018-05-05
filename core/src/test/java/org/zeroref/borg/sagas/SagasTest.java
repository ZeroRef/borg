package org.zeroref.borg.sagas;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.sagas.domain.OrderPlaced;
import org.zeroref.borg.sagas.infra.SagasMapping;

public class SagasTest {

    @Test
    public void mapping_def() {
        SagasMapping mapping = new SagasMapping();
        mapping.map(OrderPlaced.class, OrderPlaced::getOrderId);

        OrderPlaced order = new OrderPlaced("22");

        Assert.assertEquals(mapping.readKey(order), "22");
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
