package org.zeroref.borg.sagas;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.sagas.domain.OrderPlaced;
import org.zeroref.borg.sagas.infra.FunctionKeyReader;
import org.zeroref.borg.sagas.infra.KeyReader;

public class SagasTest {

    @Test
    public void message_function_key() throws Exception {

        KeyReader reader = FunctionKeyReader.create(
                OrderPlaced.class,
                OrderPlaced::getOrderId
        );

        OrderPlaced order = new OrderPlaced("22");

        Assert.assertEquals(reader.readKey(order), "22");
    }
}
