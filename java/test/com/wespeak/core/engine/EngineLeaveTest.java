package com.wespeak.core.engine;

import com.wespeak.core.CommandResponse;
import org.junit.Assert;
import org.junit.Test;

public class EngineLeaveTest extends EngineTestBase
{
    @Test
    public void testLeave()
    {
        primeParameters(3 * OneDay, 2 * OneDay, OneDay, 50, 50, 50);

        engine.heartbeat(T0, Steve);

        Assert.assertTrue(dataManager.isUserActive(Steve));

        engine.leave(T0, Steve);
        Assert.assertEquals(CommandResponse.Code.OK, engine.getLastResponse().getCode());

        Assert.assertFalse(dataManager.isUserActive(Steve));
    }

    @Test
    public void testLeaveUnknownUser()
    {
        primeParameters(3 * OneDay, 2 * OneDay, OneDay, 50, 50, 50);

        Assert.assertFalse(dataManager.isUserExists(Steve));

        engine.leave(T0, Steve);
        Assert.assertEquals(CommandResponse.Code.BAD_COMMAND, engine.getLastResponse().getCode());

        Assert.assertFalse(dataManager.isUserExists(Steve));
    }

    @Test
    public void testLeaveInactiveUser()
    {
        primeParameters(3 * OneDay, 2 * OneDay, OneDay, 50, 50, 50);

        engine.heartbeat(T0, Steve);
        engine.pulse(T0 + 3 * OneDay);

        Assert.assertFalse(dataManager.isUserActive(Steve));

        engine.leave(T0 + 3 * OneDay, Steve);
        Assert.assertEquals(CommandResponse.Code.COMMAND_REJECT, engine.getLastResponse().getCode());
    }

}
