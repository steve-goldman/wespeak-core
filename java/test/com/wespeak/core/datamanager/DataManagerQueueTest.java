package com.wespeak.core.datamanager;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

public class DataManagerQueueTest extends DataManagerTestBase
{
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
        heartbeat(T0, Steve, T2);
        heartbeat(T0, Mike,  T2);

        final String[] emptySet = new String[] {};

        validateQueue(dataManager.getSubmittedStatementIds(Steve), emptySet);
        validateQueue(dataManager.getSubmittedStatementIds(Mike),  emptySet);

        validateQueues(emptySet, emptySet, emptySet, emptySet, emptySet);

        submit(T1, Steve, Statement1, "all of western thought",   T3, 100, 10, T3);
        submit(T1, Mike,  Statement2, "is a series of footnotes", T3, 100, 10, T3);
        submit(T1, Steve, Statement3, "to plato",                 T3, 100, 10, T3);

        validateQueue(dataManager.getSubmittedStatementIds(Steve), new String[]{Statement1, Statement3});
        validateQueue(dataManager.getSubmittedStatementIds(Mike), new String[]{Statement2});

        validateQueues(new String[] {Statement1, Statement2, Statement3}, emptySet, emptySet, emptySet, emptySet);

        validateQueue(dataManager.getSupportedStatementsIds(Steve), emptySet);
        validateQueue(dataManager.getSupportedStatementsIds(Mike),  emptySet);

        support(Steve, Statement2, T4);
        support(Steve, Statement3, T4);
        support(Mike,  Statement1, T4);

        validateQueue(dataManager.getSupportedStatementsIds(Steve), new String[] {Statement2, Statement3});
        validateQueue(dataManager.getSupportedStatementsIds(Mike),  new String[] {Statement1});

        timeoutStatement(Statement1);

        validateQueues(new String[]{Statement2, Statement3}, new String[]{Statement1}, emptySet, emptySet, emptySet);

        validateQueue(dataManager.getVotedStatementIds(Steve), emptySet);
        validateQueue(dataManager.getVotedStatementIds(Mike),  emptySet);

        beginVote(T2, Statement2, T3, 100, 50, 50);

        validateQueues(new String[] {Statement3}, new String[] {Statement1}, new String[] {Statement2}, emptySet, emptySet);

        beginVote(T2, Statement3, T3, 100, 50, 50);

        validateQueues(emptySet, new String[] {Statement1}, new String[] {Statement2, Statement3}, emptySet, emptySet);

        // this queue contains statements the user is eligible to vote for, regardless if she voted yet
        validateQueue(dataManager.getVotedStatementIds(Steve), new String[] {Statement2, Statement3});
        validateQueue(dataManager.getVotedStatementIds(Mike),  new String[] {Statement2, Statement3});

        endVoteAccepted(Statement2);

        validateQueues(emptySet, new String[] {Statement1}, new String[] {Statement3}, new String[] {Statement2}, emptySet);

        endVoteRejected(Statement3);

        validateQueues(emptySet, new String[] {Statement1}, emptySet, new String[] {Statement2}, new String[] {Statement3});
    }

}
