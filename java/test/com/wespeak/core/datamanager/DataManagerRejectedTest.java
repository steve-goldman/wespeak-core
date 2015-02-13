package com.wespeak.core.datamanager;

import org.junit.Assert;
import org.junit.Test;

public class DataManagerRejectedTest extends DataManagerTestBase
{
    private void validateInvalidRejected(final String statementId)
    {
        boolean threw = false;
        try
        {
            endVoteRejected(false, true, statementId);
        }
        catch (final IllegalStateException e)
        {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void rejectedNonExistentStatement()
    {
        validateInvalidRejected(Statement1);
    }

    @Test
    public void rejectedInactiveStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        timeoutStatement(Statement1);

        validateInvalidRejected(Statement1);
    }

    @Test
    public void rejectedActiveStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        validateInvalidRejected(Statement1);
    }

    @Test
    public void rejectedAcceptedStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        endVoteAccepted(Statement1);

        validateInvalidRejected(Statement1);
    }

    @Test
    public void rejectedRejectedStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        endVoteRejected(Statement1);

        validateInvalidRejected(Statement1);
    }
    
}
