package org.zeroref.borg;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.runtime.EndpointId;

public class EndpointIdTest {
    EndpointId id = new EndpointId("component1");

    @Test
    public void creates_events() throws Exception {
        Assert.assertEquals(id.getEventsTopicName(), "component1.events");
    }

    @Test
    public void creates_errors() throws Exception {
        Assert.assertEquals(id.getErrorsTopicName(), "component1.errors");
    }

    @Test
    public void creates_slr() throws Exception {
        Assert.assertEquals(id.getSlrTopicName(), "component1.slr");
    }

    @Test
    public void creates_commands() throws Exception {
        Assert.assertEquals(id.getInputTopicName(), "component1");
    }
}
