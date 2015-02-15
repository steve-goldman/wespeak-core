package com.wespeak.core.datamanager;

import org.junit.Assert;
import org.junit.Test;

public class DataManagerUserTest extends DataManagerTestBase
{
    @Test
    public void hasNoActiveUsers()
    {
        Assert.assertFalse(dataManager.hasActiveUsers());
        Assert.assertEquals(0, dataManager.getActiveUserCount());

        heartbeat(T0, Steve, T2);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(1, dataManager.getActiveUserCount());
        timeoutUser(Steve);

        Assert.assertFalse(dataManager.hasActiveUsers());
        Assert.assertEquals(0, dataManager.getActiveUserCount());
    }

    @Test
    public void extendUserActiveTime()
    {
        Assert.assertEquals(0, dataManager.getActiveUserCount());
        heartbeat(T0, Steve, T2);
        Assert.assertEquals(1, dataManager.getActiveUserCount());
        heartbeat(T1, Steve, T3);
        Assert.assertEquals(1, dataManager.getActiveUserCount());
    }

    @Test
    public void unknownUserNotExists()
    {
        Assert.assertFalse(dataManager.isUserExists(Steve));
    }

    @Test
    public void knownUser()
    {
        heartbeat(T0, Steve, T2);
    }

    @Test
    public void timeoutActiveUser()
    {
        heartbeat(T0, Steve, T1);
        timeoutUser(Steve);
    }

    @Test
    public void timeoutNonExistentUser()
    {
        boolean threw = false;
        try
        {
            timeoutUser(false, true, Steve);
        }
        catch (final IllegalArgumentException e)
        {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void timeoutInactiveUser()
    {
        heartbeat(T0, Steve, T2);
        timeoutUser(Steve);

        boolean threw = false;
        try
        {
            timeoutUser(false, true, Steve);
        }
        catch (final IllegalStateException e)
        {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void oldestActiveUserFIFO()
    {
        // this is based on the order they are activated, not the "now" parameters

        Assert.assertFalse(dataManager.hasActiveUsers());
        Assert.assertEquals(0, dataManager.getActiveUserCount());

        heartbeat(T0, Steve, T2);
        Assert.assertEquals(Steve, dataManager.getOldestActiveUserId());
        Assert.assertEquals(1, dataManager.getActiveUserCount());

        heartbeat(T0, Mike, T2);
        Assert.assertEquals(Steve, dataManager.getOldestActiveUserId());
        Assert.assertEquals(2, dataManager.getActiveUserCount());

        heartbeat(T0, Ssor, T2);
        Assert.assertEquals(Steve, dataManager.getOldestActiveUserId());
        Assert.assertEquals(3, dataManager.getActiveUserCount());

        timeoutUser(Steve);
        Assert.assertEquals(Mike, dataManager.getOldestActiveUserId());
        Assert.assertEquals(2, dataManager.getActiveUserCount());

        timeoutUser(Mike);
        Assert.assertEquals(Ssor, dataManager.getOldestActiveUserId());
        Assert.assertEquals(1, dataManager.getActiveUserCount());

        timeoutUser(Ssor);
        Assert.assertFalse(dataManager.hasActiveUsers());
        Assert.assertEquals(0, dataManager.getActiveUserCount());
    }

    @Test
    public void oldestActiveUserLIFO()
    {
        // this is based on the order they are activated, not the "now" parameters

        Assert.assertFalse(dataManager.hasActiveUsers());
        Assert.assertEquals(0, dataManager.getActiveUserCount());

        heartbeat(T0, Steve, T2);
        Assert.assertEquals(Steve, dataManager.getOldestActiveUserId());
        Assert.assertEquals(1, dataManager.getActiveUserCount());

        heartbeat(T0, Mike, T2);
        Assert.assertEquals(Steve, dataManager.getOldestActiveUserId());
        Assert.assertEquals(2, dataManager.getActiveUserCount());

        heartbeat(T0, Ssor, T2);
        Assert.assertEquals(Steve, dataManager.getOldestActiveUserId());
        Assert.assertEquals(3, dataManager.getActiveUserCount());

        timeoutUser(Ssor);
        Assert.assertEquals(Steve, dataManager.getOldestActiveUserId());
        Assert.assertEquals(2, dataManager.getActiveUserCount());

        timeoutUser(Mike);
        Assert.assertEquals(Steve, dataManager.getOldestActiveUserId());
        Assert.assertEquals(1, dataManager.getActiveUserCount());

        timeoutUser(Steve);
        Assert.assertFalse(dataManager.hasActiveUsers());
        Assert.assertEquals(0, dataManager.getActiveUserCount());
    }

}
