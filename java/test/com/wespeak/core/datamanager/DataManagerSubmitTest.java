package com.wespeak.core.datamanager;

import org.junit.Assert;
import org.junit.Test;

public class DataManagerSubmitTest extends DataManagerTestBase
{
    @Test
    public void submitByNonExistentUser()
    {
        boolean threw = false;
        try
        {
            submit(false, true, T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);
        }
        catch (final IllegalArgumentException e)
        {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void submitByInactiveUser()
    {
        heartbeat(T0, Steve, T2);
        timeoutUser(Steve);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);
    }

    private void validateInvalidSubmit(final String statementId)
    {
        boolean threw = false;
        try
        {
            submit(false, true, T0, Steve, statementId, "all of western thought", T2, 100, 50, T2);
        }
        catch (final IllegalStateException e)
        {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void submitActiveStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        validateInvalidSubmit(Statement1);
    }

    @Test
    public void submitInactiveStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        timeoutStatement(Statement1);

        validateInvalidSubmit(Statement1);
    }

    @Test
    public void submitVotingStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        validateInvalidSubmit(Statement1);
    }

    @Test
    public void submitAcceptedStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        endVoteAccepted(Statement1);

        validateInvalidSubmit(Statement1);
    }

    @Test
    public void submitRejectedStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        endVoteRejected(Statement1);

        validateInvalidSubmit(Statement1);
    }

}
