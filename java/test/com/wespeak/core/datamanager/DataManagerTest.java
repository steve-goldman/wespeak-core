package com.wespeak.core.datamanager;

import com.wespeak.core.StatementState;
import com.wespeak.core.Vote;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

public class DataManagerTest
{
    private DataManager dataManager;

    private static final String Steve = "steve";
    private static final String Mike  = "mike";
    private static final String Ssor  = "ssor";

    private static final String Statement1 = "statement1";
    private static final String Statement2 = "statement2";
    private static final String Statement3 = "statement3";

    private static final long Period = 1000;

    private static final long T0 = 0;
    private static final long T1 = T0 + Period;
    private static final long T2 = T1 + Period;
    private static final long T3 = T2 + Period;
    private static final long T4 = T3 + Period;
    private static final long T5 = T4 + Period;


    @Before
    public void setup()
    {
        dataManager = DataManagerFactory.getInstance();
    }

    @Test
    public void unknownUserNotExists()
    {
        Assert.assertFalse(dataManager.isUserExists(Steve));
    }

    @Test
    public void knownUser()
    {
        dataManager.heartbeat(T0, Steve, T2);
        Assert.assertTrue(dataManager.isUserExists(Steve));
        Assert.assertTrue(dataManager.isUserActive(Steve));
        Assert.assertEquals(T0, dataManager.getUserActiveTime(Steve));
        Assert.assertEquals(T2 - T0, dataManager.getUserTTL(T0, Steve));
        Assert.assertEquals(T2 - T1, dataManager.getUserTTL(T1, Steve));
        Assert.assertEquals(0, dataManager.getUserTTL(T2, Steve));
    }

    @Test
    public void inactiveUserInactive()
    {
        dataManager.heartbeat(T0, Steve, T1);
        dataManager.timeoutUser(Steve);
        Assert.assertFalse(dataManager.isUserActive(Steve));
    }

    @Test
    public void userDoubleInactive()
    {
        dataManager.heartbeat(T0, Steve, T1);
        dataManager.timeoutUser(Steve);

        boolean threw = false;
        try
        {
            dataManager.timeoutUser(Steve);
        }
        catch (final IllegalStateException e)
        {
            threw = true;
        }

        Assert.assertTrue(threw);
    }

    @Test
    public void hasNoActiveUsers()
    {
        Assert.assertFalse(dataManager.hasActiveUsers());
    }

    @Test
    public void hasActiveUsers()
    {
        dataManager.heartbeat(T0, Steve, Period);
        dataManager.heartbeat(T1, Mike, Period);

        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(Steve, dataManager.getOldestActiveUserId());

        dataManager.timeoutUser(Steve);

        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertEquals(Mike, dataManager.getOldestActiveUserId());

        dataManager.timeoutUser(Mike);

        Assert.assertFalse(dataManager.hasActiveUsers());
    }

    @Test
    public void unknownStatementNotExists()
    {
        Assert.assertFalse(dataManager.isStatementExists(Statement1));
    }

    @Test
    public void statementLifeCycle()
    {
        dataManager.heartbeat(T0, Steve, T2);

        Assert.assertFalse(dataManager.hasActiveStatements());
        dataManager.submit(T1, Steve, Statement1, "all of western thought", T3, 100, 10, T3);
        Assert.assertTrue(dataManager.hasActiveStatements());
        Assert.assertEquals(Statement1, dataManager.getOldestActiveStatementId());

        Assert.assertTrue(dataManager.isStatementExists(Statement1));
        Assert.assertEquals(StatementState.ACTIVE, dataManager.getState(Statement1));
        Assert.assertEquals("all of western thought", dataManager.getText(Statement1));
        Assert.assertEquals(Steve, dataManager.getSubmitter(Statement1));
        Assert.assertEquals(T1, dataManager.getSubmissionTime(Statement1));
        Assert.assertEquals(T3 - T1, dataManager.getSubmissionTTL(T1, Statement1));
        Assert.assertEquals(T3 - T2, dataManager.getSubmissionTTL(T2, Statement1));
        Assert.assertEquals(0      , dataManager.getSubmissionTTL(T3, Statement1));
        Assert.assertEquals(100, dataManager.getNumEligibleSupporters(Statement1));
        Assert.assertEquals(10,  dataManager.getPropSupportNeeded(Statement1));

        Assert.assertEquals(0, dataManager.getSupportCount(Statement1));
        Assert.assertTrue(dataManager.canSupport(Steve, Statement1));
        Assert.assertFalse(dataManager.isSupported(Steve, Statement1));
        dataManager.support(Steve, Statement1, T3);
        Assert.assertTrue(dataManager.isSupported(Steve, Statement1));
        Assert.assertEquals(1, dataManager.getSupportCount(Statement1));

        dataManager.heartbeat(T2, Mike, T3);
        Assert.assertFalse(dataManager.canSupport(Mike, Statement1));

        dataManager.beginVote(T3, Statement1, T4, 500, 50, 60);
        Assert.assertFalse(dataManager.hasActiveStatements());
        Assert.assertEquals(StatementState.VOTING, dataManager.getState(Statement1));
        Assert.assertEquals(500, dataManager.getNumEligibleVoters(Statement1));
        Assert.assertEquals(50, dataManager.getPropVotesNeeded(Statement1));
        Assert.assertEquals(60, dataManager.getPropYesesNeeded(Statement1));
        Assert.assertEquals(T3, dataManager.getVoteBeginTime(Statement1));
        Assert.assertEquals(T4 - T3, dataManager.getVoteTTL(T3, Statement1));
        Assert.assertEquals(0      , dataManager.getVoteTTL(T4, Statement1));

        Assert.assertEquals(0, dataManager.getVoteCount(Statement1));

        Assert.assertTrue(dataManager.canVote(Steve, Statement1));
        Assert.assertEquals(Vote.ABSTAIN, dataManager.getVote(Steve, Statement1));
        dataManager.vote(Steve, Statement1, Vote.YES, T4);
        Assert.assertEquals(Vote.YES, dataManager.getVote(Steve, Statement1));
        Assert.assertEquals(1, dataManager.getVoteCount(Statement1));
        Assert.assertEquals(1, dataManager.getYesCount(Statement1));

        Assert.assertTrue(dataManager.canVote(Mike, Statement1));
        Assert.assertEquals(Vote.ABSTAIN, dataManager.getVote(Mike, Statement1));
        dataManager.vote(Mike, Statement1, Vote.NO, T4);
        Assert.assertEquals(Vote.NO, dataManager.getVote(Mike, Statement1));
        Assert.assertEquals(2, dataManager.getVoteCount(Statement1));
        Assert.assertEquals(1, dataManager.getYesCount(Statement1));

        dataManager.heartbeat(T4, Ssor, T5);
        Assert.assertFalse(dataManager.canVote(Ssor, Statement1));

        dataManager.endVoteAccepted(Statement1);
        Assert.assertEquals(StatementState.ACCEPTED, dataManager.getState(Statement1));
    }

    private void validateQueue(final Iterator<String> iter, final String[] statements)
    {
        for (final String statement : statements)
        {
            Assert.assertTrue(iter.hasNext());
            Assert.assertEquals(statement, iter.next());
        }
        Assert.assertFalse(iter.hasNext());
    }

    private void validateQueues(final String[] activeStatements,
                                final String[] inactiveStatements,
                                final String[] votingStatements,
                                final String[] acceptedStatements,
                                final String[] rejectedStatements)
    {
        validateQueue(dataManager.getActiveStatementIds(),   activeStatements);
        validateQueue(dataManager.getInactiveStatementIds(), inactiveStatements);
        validateQueue(dataManager.getVotingStatementIds(),   votingStatements);
        validateQueue(dataManager.getAcceptedStatementIds(), acceptedStatements);
        validateQueue(dataManager.getRejectedStatementIds(), rejectedStatements);
    }

    @Test
    public void statementQueues()
    {
        dataManager.heartbeat(T0, Steve, T2);
        dataManager.heartbeat(T0, Mike,  T2);

        final String[] emptySet = new String[] {};

        validateQueue(dataManager.getSubmittedStatementIds(Steve), emptySet);
        validateQueue(dataManager.getSubmittedStatementIds(Mike),  emptySet);

        validateQueues(emptySet, emptySet, emptySet, emptySet, emptySet);

        dataManager.submit(T1, Steve, Statement1, "all of western thought",   T3, 100, 10, T3);
        dataManager.submit(T1, Mike,  Statement2, "is a series of footnotes", T3, 100, 10, T3);
        dataManager.submit(T1, Steve, Statement3, "to plato",                 T3, 100, 10, T3);

        validateQueue(dataManager.getSubmittedStatementIds(Steve), new String[]{Statement1, Statement3});
        validateQueue(dataManager.getSubmittedStatementIds(Mike), new String[]{Statement2});

        validateQueues(new String[] {Statement1, Statement2, Statement3}, emptySet, emptySet, emptySet, emptySet);

        validateQueue(dataManager.getSupportedStatementsIds(Steve), emptySet);
        validateQueue(dataManager.getSupportedStatementsIds(Mike),  emptySet);

        dataManager.support(Steve, Statement2, T4);
        dataManager.support(Steve, Statement3, T4);
        dataManager.support(Mike,  Statement1, T4);

        validateQueue(dataManager.getSupportedStatementsIds(Steve), new String[] {Statement2, Statement3});
        validateQueue(dataManager.getSupportedStatementsIds(Mike),  new String[] {Statement1});

        dataManager.timeoutStatement(Statement1);

        validateQueues(new String[]{Statement2, Statement3}, new String[]{Statement1}, emptySet, emptySet, emptySet);

        validateQueue(dataManager.getVotedStatementIds(Steve), emptySet);
        validateQueue(dataManager.getVotedStatementIds(Mike),  emptySet);

        dataManager.beginVote(T2, Statement2, T3, 100, 50, 50);

        validateQueues(new String[] {Statement3}, new String[] {Statement1}, new String[] {Statement2}, emptySet, emptySet);

        dataManager.beginVote(T2, Statement3, T3, 100, 50, 50);

        validateQueues(emptySet, new String[] {Statement1}, new String[] {Statement2, Statement3}, emptySet, emptySet);

        // this queue contains statements the user is eligible to vote for, regardless if she voted yet
        validateQueue(dataManager.getVotedStatementIds(Steve), new String[] {Statement2, Statement3});
        validateQueue(dataManager.getVotedStatementIds(Mike),  new String[] {Statement2, Statement3});

        dataManager.endVoteAccepted(Statement2);

        validateQueues(emptySet, new String[] {Statement1}, new String[] {Statement3}, new String[] {Statement2}, emptySet);

        dataManager.endVoteRejected(Statement3);

        validateQueues(emptySet, new String[] {Statement1}, emptySet, new String[] {Statement2}, new String[] {Statement3});
    }
}
