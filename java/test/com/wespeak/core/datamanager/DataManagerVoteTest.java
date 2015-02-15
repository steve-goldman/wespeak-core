package com.wespeak.core.datamanager;

import com.wespeak.core.Vote;
import org.junit.Assert;
import org.junit.Test;

public class DataManagerVoteTest extends DataManagerTestBase
{
    private void validateInvalidVote(final String userId, final String statementId)
    {
        boolean threwOrCannotVote;
        try
        {
            threwOrCannotVote = !dataManager.isVoteEligible(userId, statementId);
            if (!threwOrCannotVote)
            {
                dataManager.vote(T0, userId, statementId, Vote.YES, T2);
            }
        }
        catch (final Exception e)
        {
            threwOrCannotVote = true;
        }
        Assert.assertTrue(threwOrCannotVote);
    }

    @Test
    public void voteByNonExistentUser()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        validateInvalidVote(Mike, Statement1);
    }

    @Test
    public void voteByNewerUser()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        heartbeat(T1, Mike, T3);

        validateInvalidVote(Mike, Statement1);
    }

    @Test
    public void voteByInactiveUserBeforeVoteBegins()
    {
        heartbeat(T0, Steve, T2);
        heartbeat(T0, Mike, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        timeoutUser(Mike);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        validateInvalidVote(Mike, Statement1);

        boolean threw = false;
        try
        {
            dataManager.getVote(Mike, Statement1);
        }
        catch (final IllegalStateException e)
        {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void voteByInactiveUserAfterVoteBegins()
    {
        heartbeat(T0, Steve, T2);
        heartbeat(T0, Mike, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        timeoutUser(Mike);

        vote(T2, Mike, Statement1, Vote.YES, T4);
    }

    @Test
    public void voteAlreadyVoted()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        vote(T2, Steve, Statement1, Vote.YES, T4);

        validateInvalidVote(Steve, Statement1);
    }

    @Test
    public void voteInactiveStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        timeoutStatement(Statement1);

        validateInvalidVote(Steve, Statement1);
    }

    @Test
    public void voteActiveStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        validateInvalidVote(Steve, Statement1);
    }

    @Test
    public void voteAcceptedStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        endVoteAccepted(Statement1);

        validateInvalidVote(Steve, Statement1);
    }

    @Test
    public void voteRejectedStatement()
    {
        heartbeat(T0, Steve, T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        endVoteAccepted(Statement1);

        validateInvalidVote(Steve, Statement1);
    }

    @Test
    public void manyVote()
    {
        heartbeat(T0, Steve, T2);
        heartbeat(T0, Mike,  T2);
        heartbeat(T0, Ssor,  T2);

        submit(T0, Steve, Statement1, "all of western thought", T2, 100, 50, T2);

        beginVote(T1, Statement1, T3, 100, 50, 50);

        vote(T1, Steve, Statement1, Vote.YES, T4);
        vote(T1, Mike,  Statement1, Vote.NO,  T4);
        vote(T1, Ssor,  Statement1, Vote.YES, T4);
    }
}
