package com.wespeak.core.datamanager;

import org.junit.Assert;
import org.junit.Test;

public class DataManagerTimeoutTest extends DataManagerTestBase
{
    private void validateInvalidTimeout(final String statementId)
    {
        boolean threw = false;
        try
        {
            timeoutStatement(false, true, statementId);
        }
        catch (final IllegalStateException e)
        {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void timeoutNonExistentStatement()
    {
        validateInvalidTimeout(Statement1);
    }

    @Test
    public void timeoutInactiveStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        timeoutStatement(Statement1);

        validateInvalidTimeout(Statement1);
    }

    @Test
    public void timeoutVotingStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        validateInvalidTimeout(Statement1);
    }

    @Test
    public void timeoutAcceptedStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        endVoteAccepted(Statement1);

        validateInvalidTimeout(Statement1);
    }

    @Test
    public void timeoutRejectedStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        endVoteRejected(Statement1);

        validateInvalidTimeout(Statement1);
    }

}
