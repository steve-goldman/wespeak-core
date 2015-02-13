package com.wespeak.core.datamanager;

import org.junit.Assert;
import org.junit.Test;

public class DataManagerAcceptedTest extends DataManagerTestBase
{
    private void validateInvalidAccepted(final String statementId)
    {
        boolean threw = false;
        try
        {
            endVoteAccepted(false, true, statementId);
        }
        catch (final IllegalStateException e)
        {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void acceptedNonExistentStatement()
    {
        validateInvalidAccepted(Statement1);
    }

    @Test
    public void acceptedInactiveStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        timeoutStatement(Statement1);

        validateInvalidAccepted(Statement1);
    }

    @Test
    public void acceptedActiveStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        validateInvalidAccepted(Statement1);
    }

    @Test
    public void acceptedAcceptedStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        endVoteAccepted(Statement1);

        validateInvalidAccepted(Statement1);
    }

    @Test
    public void acceptedRejectedStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        endVoteRejected(Statement1);

        validateInvalidAccepted(Statement1);
    }

}
