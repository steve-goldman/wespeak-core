package com.wespeak.core.datamanager;

import org.junit.Assert;
import org.junit.Test;

public class DataManagerBeginVoteTest extends DataManagerTestBase
{
    private void validateInvalidBeginVote(final String statementId)
    {
        boolean threw = false;
        try
        {
            beginVote(false, true, T0, statementId, T2, 100, 50, 50);
        }
        catch (final IllegalStateException e)
        {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void beginVoteNonExistentStatement()
    {
        validateInvalidBeginVote(Statement1);
    }

    @Test
    public void beginVoteInactiveStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        timeoutStatement(Statement1);

        validateInvalidBeginVote(Statement1);
    }

    @Test
    public void beginVoteVotingStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        validateInvalidBeginVote(Statement1);
    }

    @Test
    public void beginVoteAcceptedStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        endVoteAccepted(Statement1);

        validateInvalidBeginVote(Statement1);
    }

    @Test
    public void beginVoteRejectedStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        endVoteRejected(Statement1);

        validateInvalidBeginVote(Statement1);
    }

}
