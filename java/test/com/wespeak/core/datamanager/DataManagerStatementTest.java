package com.wespeak.core.datamanager;

import org.junit.Assert;
import org.junit.Test;

public class DataManagerStatementTest extends DataManagerTestBase
{
    @Test
    public void unknownStatementNotExists()
    {
        Assert.assertFalse(dataManager.isStatementExists(Statement1));
    }

    @Test
    public void oldestActiveStatementFIFO()
    {
        // this is based on the order they are activated, not the "now" parameters

        heartbeat(T0, Steve, T2);

        Assert.assertFalse(dataManager.hasActiveStatements());

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, 50);
        Assert.assertEquals(Statement1, dataManager.getOldestActiveStatementId());

        submit(T0, Steve, Statement2, "is a series of footnotes", T2, 100, 50, 50);
        Assert.assertEquals(Statement1, dataManager.getOldestActiveStatementId());

        submit(T0, Steve, Statement3, "to plato",                 T2, 100, 50, 50);
        Assert.assertEquals(Statement1, dataManager.getOldestActiveStatementId());

        timeoutStatement(Statement1);
        Assert.assertEquals(Statement2, dataManager.getOldestActiveStatementId());

        timeoutStatement(Statement2);
        Assert.assertEquals(Statement3, dataManager.getOldestActiveStatementId());

        timeoutStatement(Statement3);
        Assert.assertFalse(dataManager.hasActiveStatements());
    }

    @Test
    public void oldestActiveStatementLIFO()
    {
        // this is based on the order they are activated, not the "now" parameters

        heartbeat(T0, Steve, T2);

        Assert.assertFalse(dataManager.hasActiveStatements());

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, 50);
        Assert.assertEquals(Statement1, dataManager.getOldestActiveStatementId());

        submit(T0, Steve, Statement2, "is a series of footnotes", T2, 100, 50, 50);
        Assert.assertEquals(Statement1, dataManager.getOldestActiveStatementId());

        submit(T0, Steve, Statement3, "to plato",                 T2, 100, 50, 50);
        Assert.assertEquals(Statement1, dataManager.getOldestActiveStatementId());

        timeoutStatement(Statement3);
        Assert.assertEquals(Statement1, dataManager.getOldestActiveStatementId());

        timeoutStatement(Statement2);
        Assert.assertEquals(Statement1, dataManager.getOldestActiveStatementId());

        timeoutStatement(Statement1);
        Assert.assertFalse(dataManager.hasActiveStatements());
    }

}
