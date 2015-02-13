package com.wespeak.core.datamanager;

import org.junit.Assert;
import org.junit.Test;

public class DataManagerSupportTest extends DataManagerTestBase
{
    private void validateInvalidSupport(final String userId, final String statementId)
    {
        boolean threwOrCannotSupport;
        try
        {
            threwOrCannotSupport = !dataManager.canSupport(userId, statementId);
            if (!threwOrCannotSupport)
            {
                dataManager.support(userId, statementId, T2);
            }
        }
        catch (final Exception e)
        {
            threwOrCannotSupport = true;
        }
        Assert.assertTrue(threwOrCannotSupport);
    }

    @Test
    public void supportByNonExistentUser()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        validateInvalidSupport(Mike, Statement1);
    }

    @Test
    public void supportByNewerUser()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        heartbeat(T1, Mike, T3);

        validateInvalidSupport(Mike, Statement1);
    }

    @Test
    public void supportByInactiveUser()
    {
        heartbeat(T0, Steve, T2);
        heartbeat(T0, Mike,  T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        timeoutUser(Mike);

        validateInvalidSupport(Mike, Statement1);
    }

    @Test
    public void supportAlreadySupported()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        support(Steve, Statement1, T4);

        validateInvalidSupport(Steve, Statement1);
    }

    @Test
    public void supportInactiveStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        timeoutStatement(Statement1);

        validateInvalidSupport(Steve, Statement1);
    }

    @Test
    public void supportVotingStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        validateInvalidSupport(Steve, Statement1);
    }

    @Test
    public void supportAcceptedStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        endVoteAccepted(Statement1);

        validateInvalidSupport(Steve, Statement1);
    }

    @Test
    public void supportRejectedStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        endVoteAccepted(Statement1);

        validateInvalidSupport(Steve, Statement1);
    }

    @Test
    public void manySupport()
    {
        heartbeat(T0, Steve, T2);
        heartbeat(T0, Mike,  T2);
        heartbeat(T0, Ssor,  T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        support(Steve, Statement1, T4);
        support(Mike,  Statement1, T4);
        support(Ssor,  Statement1, T4);
    }
}
