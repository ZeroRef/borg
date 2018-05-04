package org.zeroref.borg;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.zeroref.borg.recoverability.BackOff;

public class BackOffTest {
    @Test
    public void nextTimeout() throws Exception {
        Assert.assertEquals(BackOff.exponentialBackoff(0, 100L), 100);
        Assert.assertEquals(BackOff.exponentialBackoff(1, 100L), 200);
        Assert.assertEquals(BackOff.exponentialBackoff(2, 100L), 400);
    }
}
