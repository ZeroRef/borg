package org.zeroref.borg.transport;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransportRecordByteSerializerTest {
    TransportRecordByteSerializer serializer = new TransportRecordByteSerializer();
    private Gson recordGson = new Gson();

    @Test
    public void encode(){
        TransportRecord record = new TransportRecord(UUID.randomUUID(), new HashMap<>());
        Assert.assertEquals(serializer.serialize("", record), recordGson.toJson(record).getBytes());
    }

    @Test
    public void decode(){
        UUID uuid = UUID.randomUUID();
        HashMap<String, Object> c1 = new HashMap<>();
        c1.put("a", "b");
        TransportRecord record = new TransportRecord(uuid, c1);
        byte[] recordBytes = recordGson.toJson(record).getBytes();

        TransportRecord record1 = serializer.deserialize("", recordBytes);
        Map<String, Object> c2 = record1.getContent();

        Assert.assertEquals(record1.getUuid(), record.getUuid());
        Assert.assertEquals(c2.get("a"), "b");
    }
}
