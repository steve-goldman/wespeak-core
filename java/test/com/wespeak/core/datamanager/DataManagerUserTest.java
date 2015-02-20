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
    public void nextActiverUserToTimeoutFirstIn()
    {
        Assert.assertFalse(dataManager.hasActiveUsers());
        Assert.assertEquals(0, dataManager.getActiveUserCount());

        heartbeat(T0, Steve, T2);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(1, dataManager.getActiveUserCount());
        Assert.assertEquals(Steve, dataManager.getNextActiveUserIdToTimeout());

        heartbeat(T0, Mike, T2 + 1);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(2, dataManager.getActiveUserCount());
        Assert.assertEquals(Steve, dataManager.getNextActiveUserIdToTimeout());

        heartbeat(T0, Ssor, T2 + 2);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(3, dataManager.getActiveUserCount());
        Assert.assertEquals(Steve, dataManager.getNextActiveUserIdToTimeout());

        timeoutUser(Steve);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(2, dataManager.getActiveUserCount());
        Assert.assertEquals(Mike, dataManager.getNextActiveUserIdToTimeout());

        timeoutUser(Mike);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(1, dataManager.getActiveUserCount());
        Assert.assertEquals(Ssor, dataManager.getNextActiveUserIdToTimeout());

        timeoutUser(Ssor);
        Assert.assertFalse(dataManager.hasActiveUsers());
        Assert.assertEquals(0, dataManager.getActiveUserCount());
    }

    @Test
    public void nextActiverUserToTimeoutLastIn()
    {
        Assert.assertFalse(dataManager.hasActiveUsers());
        Assert.assertEquals(0, dataManager.getActiveUserCount());

        heartbeat(T0, Ssor, T2 + 2);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(1, dataManager.getActiveUserCount());
        Assert.assertEquals(Ssor, dataManager.getNextActiveUserIdToTimeout());

        heartbeat(T0, Mike, T2 + 1);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(2, dataManager.getActiveUserCount());
        Assert.assertEquals(Mike, dataManager.getNextActiveUserIdToTimeout());

        heartbeat(T0, Steve, T2);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(3, dataManager.getActiveUserCount());
        Assert.assertEquals(Steve, dataManager.getNextActiveUserIdToTimeout());

        timeoutUser(Steve);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(2, dataManager.getActiveUserCount());
        Assert.assertEquals(Mike, dataManager.getNextActiveUserIdToTimeout());

        timeoutUser(Mike);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(1, dataManager.getActiveUserCount());
        Assert.assertEquals(Ssor, dataManager.getNextActiveUserIdToTimeout());

        timeoutUser(Ssor);
        Assert.assertFalse(dataManager.hasActiveUsers());
        Assert.assertEquals(0, dataManager.getActiveUserCount());
    }

    @Test
    public void nextActiverUserToTimeoutMiddleInOne()
    {
        Assert.assertFalse(dataManager.hasActiveUsers());
        Assert.assertEquals(0, dataManager.getActiveUserCount());

        heartbeat(T0, Ssor, T2 + 2);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(1, dataManager.getActiveUserCount());
        Assert.assertEquals(Ssor, dataManager.getNextActiveUserIdToTimeout());

        heartbeat(T0, Steve, T2);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(2, dataManager.getActiveUserCount());
        Assert.assertEquals(Steve, dataManager.getNextActiveUserIdToTimeout());

        heartbeat(T0, Mike, T2 + 1);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(3, dataManager.getActiveUserCount());
        Assert.assertEquals(Steve, dataManager.getNextActiveUserIdToTimeout());

        timeoutUser(Steve);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(2, dataManager.getActiveUserCount());
        Assert.assertEquals(Mike, dataManager.getNextActiveUserIdToTimeout());

        timeoutUser(Mike);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(1, dataManager.getActiveUserCount());
        Assert.assertEquals(Ssor, dataManager.getNextActiveUserIdToTimeout());

        timeoutUser(Ssor);
        Assert.assertFalse(dataManager.hasActiveUsers());
        Assert.assertEquals(0, dataManager.getActiveUserCount());
    }

    @Test
    public void nextActiverUserToTimeoutMiddleInTwo()
    {
        Assert.assertFalse(dataManager.hasActiveUsers());
        Assert.assertEquals(0, dataManager.getActiveUserCount());

        heartbeat(T0, Mike, T2 + 1);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(1, dataManager.getActiveUserCount());
        Assert.assertEquals(Mike, dataManager.getNextActiveUserIdToTimeout());

        heartbeat(T0, Steve, T2);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(2, dataManager.getActiveUserCount());
        Assert.assertEquals(Steve, dataManager.getNextActiveUserIdToTimeout());

        heartbeat(T0, Ssor, T2 + 2);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(3, dataManager.getActiveUserCount());
        Assert.assertEquals(Steve, dataManager.getNextActiveUserIdToTimeout());

        timeoutUser(Steve);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(2, dataManager.getActiveUserCount());
        Assert.assertEquals(Mike, dataManager.getNextActiveUserIdToTimeout());

        timeoutUser(Mike);
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(1, dataManager.getActiveUserCount());
        Assert.assertEquals(Ssor, dataManager.getNextActiveUserIdToTimeout());

        timeoutUser(Ssor);
        Assert.assertFalse(dataManager.hasActiveUsers());
        Assert.assertEquals(0, dataManager.getActiveUserCount());
    }

}
