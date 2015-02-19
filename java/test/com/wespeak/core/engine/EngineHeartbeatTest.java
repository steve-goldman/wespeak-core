package com.wespeak.core.engine;

import com.wespeak.core.CommandResponse;
import org.junit.Assert;
import org.junit.Test;

public class EngineHeartbeatTest extends EngineTestBase
{
    @Test
    public void testHeartbeat()
    {
        primeParameters(3 * OneDay, 2 * OneDay, OneDay, 50, 50, 50);

        Assert.assertFalse(dataManager.isUserExists(Steve));

        engine.heartbeat(T0, Steve);
        Assert.assertEquals(CommandResponse.Code.OK, engine.getLastResponse().getCode());

        Assert.assertTrue(dataManager.isUserExists(Steve));
        Assert.assertTrue(dataManager.isUserActive(Steve));
        Assert.assertEquals(T0, dataManager.getUserActiveTime(Steve));
        Assert.assertEquals(3 * OneDay, dataManager.getUserTTL(T0, Steve));
    }

    @Test
    public void testHeartbeatExtend()
    {
        primeParameters(3 * OneDay, 2 * OneDay, OneDay, 50, 50, 50);

        engine.heartbeat(T0, Steve);
        engine.heartbeat(T0 + OneDay, Steve);
        Assert.assertEquals(CommandResponse.Code.OK, engine.getLastResponse().getCode());

        Assert.assertTrue(dataManager.isUserActive(Steve));
        Assert.assertEquals(T0, dataManager.getUserActiveTime(Steve));
        Assert.assertEquals(3 * OneDay, dataManager.getUserTTL(T0 + OneDay, Steve));
    }

    @Test
    public void testHeartbeatRenew()
    {
        primeParameters(3 * OneDay, 2 * OneDay, OneDay, 50, 50, 50);

        engine.heartbeat(T0, Steve);
        engine.pulse(T0 + 3 * OneDay);

        Assert.assertFalse(dataManager.isUserActive(Steve));

        engine.heartbeat(T0 + 4 * OneDay, Steve);
        Assert.assertEquals(CommandResponse.Code.OK, engine.getLastResponse().getCode());

        Assert.assertTrue(dataManager.isUserActive(Steve));
        Assert.assertEquals(T0 + 4 * OneDay, dataManager.getUserActiveTime(Steve));
        Assert.assertEquals(3 * OneDay, dataManager.getUserTTL(T0 + 4 * OneDay, Steve));
    }
}
