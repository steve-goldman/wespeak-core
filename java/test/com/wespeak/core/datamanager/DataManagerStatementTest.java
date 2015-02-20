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
    public void nextActiveStatementToTimeoutFirstIn()
    {
        heartbeat(T0, Steve, T2);

        Assert.assertFalse(dataManager.hasActiveStatements());

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, 50);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement1, dataManager.getNextActiveStatementIdToTimeout());

        submit(T0, Steve, Statement2, "is a series of footnotes", T2 + 1, 100, 50, 50);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement1, dataManager.getNextActiveStatementIdToTimeout());

        submit(T0, Steve, Statement3, "to plato", T2 + 2, 100, 50, 50);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement1, dataManager.getNextActiveStatementIdToTimeout());

        timeoutStatement(Statement1);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement2, dataManager.getNextActiveStatementIdToTimeout());

        timeoutStatement(Statement2);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement3, dataManager.getNextActiveStatementIdToTimeout());

        timeoutStatement(Statement3);
        Assert.assertFalse(dataManager.hasActiveStatements());
    }

    @Test
    public void nextActiveStatementToTimeoutLastIn()
    {
        heartbeat(T0, Steve, T2);

        Assert.assertFalse(dataManager.hasActiveStatements());

        submit(T0, Steve, Statement3, "to plato", T2 + 2, 100, 50, 50);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement3, dataManager.getNextActiveStatementIdToTimeout());

        submit(T0, Steve, Statement2, "is a series of footnotes", T2 + 1, 100, 50, 50);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement2, dataManager.getNextActiveStatementIdToTimeout());

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, 50);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement1, dataManager.getNextActiveStatementIdToTimeout());

        timeoutStatement(Statement1);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement2, dataManager.getNextActiveStatementIdToTimeout());

        timeoutStatement(Statement2);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement3, dataManager.getNextActiveStatementIdToTimeout());

        timeoutStatement(Statement3);
        Assert.assertFalse(dataManager.hasActiveStatements());
    }

    @Test
    public void nextActiveStatementToTimeoutMiddleInOne()
    {
        heartbeat(T0, Steve, T2);

        Assert.assertFalse(dataManager.hasActiveStatements());

        submit(T0, Steve, Statement3, "to plato", T2 + 2, 100, 50, 50);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement3, dataManager.getNextActiveStatementIdToTimeout());

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, 50);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement1, dataManager.getNextActiveStatementIdToTimeout());

        submit(T0, Steve, Statement2, "is a series of footnotes", T2 + 1, 100, 50, 50);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement1, dataManager.getNextActiveStatementIdToTimeout());

        timeoutStatement(Statement1);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement2, dataManager.getNextActiveStatementIdToTimeout());

        timeoutStatement(Statement2);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement3, dataManager.getNextActiveStatementIdToTimeout());

        timeoutStatement(Statement3);
        Assert.assertFalse(dataManager.hasActiveStatements());
    }

    @Test
    public void nextActiveStatementToTimeoutMiddleInTwo()
    {
        heartbeat(T0, Steve, T2);

        Assert.assertFalse(dataManager.hasActiveStatements());

        submit(T0, Steve, Statement2, "is a series of footnotes", T2 + 1, 100, 50, 50);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement2, dataManager.getNextActiveStatementIdToTimeout());

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, 50);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement1, dataManager.getNextActiveStatementIdToTimeout());

        submit(T0, Steve, Statement3, "to plato", T2 + 2, 100, 50, 50);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement1, dataManager.getNextActiveStatementIdToTimeout());

        timeoutStatement(Statement1);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement2, dataManager.getNextActiveStatementIdToTimeout());

        timeoutStatement(Statement2);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement3, dataManager.getNextActiveStatementIdToTimeout());

        timeoutStatement(Statement3);
        Assert.assertFalse(dataManager.hasActiveStatements());
    }

}
