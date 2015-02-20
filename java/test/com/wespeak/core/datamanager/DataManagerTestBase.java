package com.wespeak.core.datamanager;

import com.wespeak.core.StatementState;
import com.wespeak.core.Vote;
import org.junit.Assert;
import org.junit.Before;

import java.util.Iterator;

public class DataManagerTestBase
{
    protected DataManager dataManager;

    protected static final String Steve = "steve";
    protected static final String Mike  = "mike";
    protected static final String Ssor  = "ssor";

    protected static final String Statement1 = "statement1";
    protected static final String Statement2 = "statement2";
    protected static final String Statement3 = "statement3";

    private static final long Period = 1000;

    protected static final long T0 = 0;
    protected static final long T1 = T0 + Period;
    protected static final long T2 = T1 + Period;
    protected static final long T3 = T2 + Period;
    protected static final long T4 = T3 + Period;
    protected static final long T5 = T4 + Period;

    @Before
    public void setup()
    {
        dataManager = DataManagerFactory.getInstance();
    }

    private void validateFoundCount(final Iterator<String> iter, final String statementId, final int expectedCount)
    {
        int foundCount = 0;
        while (iter.hasNext())
        {
            if (statementId.equals(iter.next()))
            {
                foundCount++;
            }
        }
        Assert.assertEquals(expectedCount, foundCount);
    }

    protected void heartbeat(final long   now,
                             final String userId,
                             final long   userActiveUntil)
    {
        heartbeat(true, now, userId, userActiveUntil);
    }

    protected void heartbeat(boolean checkPostconditions,
                           final long   now,
                           final String userId,
                           final long   userActiveUntil)
    {
        // no pre-conditions

        final boolean preIsUserActive   = dataManager.isUserExists(userId) && dataManager.isUserActive(userId);
        final long    preUserActiveTime = preIsUserActive ? dataManager.getUserActiveTime(userId) : 0;

        dataManager.heartbeat(now, userId, userActiveUntil);

        if (checkPostconditions)
        {
            heartbeatPostconditions(preIsUserActive, preUserActiveTime, now, userId, userActiveUntil);
        }
    }

    private void heartbeatPostconditions(final boolean preIsUserActive,
                                         final long    preUserActiveTime,
                                         final long    now,
                                         final String  userId,
                                         final long    userActiveUntil)
    {
        // the user exists
        Assert.assertTrue(dataManager.isUserExists(userId));

        // the user is active
        Assert.assertTrue(dataManager.isUserActive(userId));

        // the system has at least one active user
        Assert.assertTrue(dataManager.hasActiveUsers());

        // the user active time and TTL make sense
        if (preIsUserActive)
        {
            Assert.assertEquals(preUserActiveTime, dataManager.getUserActiveTime(userId));
        }
        else
        {
            Assert.assertEquals(now, dataManager.getUserActiveTime(userId));
        }
        Assert.assertEquals(100, dataManager.getUserTTL(userActiveUntil - 100, userId));
    }

    protected void timeoutUser(final String userId)
    {
        timeoutUser(true, true, userId);
    }

    protected void timeoutUser(final boolean checkPreconditions, final boolean checkPostconditions, final String userId)
    {
        if (checkPreconditions)
        {
            timeoutUserPreconditions(userId);
        }

        dataManager.timeoutUser(T0, userId);

        if (checkPostconditions)
        {
            timeoutUserPostconditions(userId);
        }
    }

    private void timeoutUserPreconditions(final String userId)
    {
        // the user exists
        Assert.assertTrue(dataManager.isUserExists(userId));

        // the user is active
        Assert.assertTrue(dataManager.isUserActive(userId));
    }

    private void timeoutUserPostconditions(final String userId)
    {
        // the user exists
        Assert.assertTrue(dataManager.isUserExists(userId));

        // the user is not active
        Assert.assertFalse(dataManager.isUserActive(userId));
    }

    protected void timeoutStatement(final String statementId)
    {
        timeoutStatement(true, true, statementId);
    }

    protected void timeoutStatement(final boolean checkPreconditions, final boolean checkPostconditions, final String statementId)
    {
        if (checkPreconditions)
        {
            timeoutStatementPreconditions(statementId);
        }

        final int preNumEligibleSupporters = dataManager.getNumEligibleSupporters(statementId);
        final int preSupportCount          = dataManager.getSupportCount(statementId);

        dataManager.timeoutStatement(T0, statementId);

        if (checkPostconditions)
        {
            timeoutStatementPostconditions(preNumEligibleSupporters, preSupportCount, statementId);
        }
    }

    private void timeoutStatementPreconditions(final String statementId)
    {
        // the statement exists
        Assert.assertTrue(dataManager.isStatementExists(statementId));

        // the statement is active
        Assert.assertEquals(StatementState.ACTIVE, dataManager.getState(statementId));

        // the statement is in the list of active statements
        validateFoundCount(dataManager.getActiveStatementIds(),   statementId, 1);

        // the statement is not in the list of inactive, voting, accepted, nor rejected statements
        validateFoundCount(dataManager.getInactiveStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getVotingStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getAcceptedStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getRejectedStatementIds(), statementId, 0);
    }

    private void timeoutStatementPostconditions(final int preNumEligibleSupporters, final int preSupportCount, final String statementId)
    {
        // the statement exists
        Assert.assertTrue(dataManager.isStatementExists(statementId));

        // the statement is inactive
        Assert.assertEquals(StatementState.INACTIVE, dataManager.getState(statementId));

        // the statement is in the list of inactive statements
        validateFoundCount(dataManager.getInactiveStatementIds(),   statementId, 1);

        // the statement is not in the list of active, voting, accepted, nor rejected statements
        validateFoundCount(dataManager.getActiveStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getVotingStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getAcceptedStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getRejectedStatementIds(), statementId, 0);

        // the num eligible supporters and support count match from before
        Assert.assertEquals(preNumEligibleSupporters, dataManager.getNumEligibleSupporters(statementId));
        Assert.assertEquals(preSupportCount, dataManager.getSupportCount(statementId));
    }

    protected void submit(final long    now,
                          final String  userId,
                          final String  statementId,
                          final String  text,
                          final long    until,
                          final int     numEligibleSupporters,
                          final int     propSupportNeeded,
                          final long    userActiveUntil)
    {
        submit(true, true, now, userId, statementId, text, until, numEligibleSupporters, propSupportNeeded, userActiveUntil);
    }

    protected void submit(final boolean checkPreconditions,
                          final boolean checkPostconditions,
                          final long    now,
                          final String  userId,
                          final String  statementId,
                          final String  text,
                          final long    until,
                          final int     numEligibleSupporters,
                          final int     propSupportNeeded,
                          final long    userActiveUntil)
    {
        if (checkPreconditions)
        {
            submitPreconditions(userId, statementId);
        }

        dataManager.submit(now, userId, statementId, text, until, numEligibleSupporters, propSupportNeeded, userActiveUntil);

        if (checkPostconditions)
        {
            submitPostconditions(now, userId, statementId, text, until, numEligibleSupporters, propSupportNeeded, userActiveUntil);
        }
    }

    private void submitPreconditions(final String userId,
                                     final String statementId)
    {
        // the user exists
        Assert.assertTrue(dataManager.isUserExists(userId));

        // the statement does not exist
        Assert.assertFalse(dataManager.isStatementExists(statementId));

        // the statement is not in the list of active, inactive, voting, accepted, nor rejected statements
        validateFoundCount(dataManager.getActiveStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getInactiveStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getVotingStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getAcceptedStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getRejectedStatementIds(), statementId, 0);
    }

    private void submitPostconditions(final long   now,
                                      final String userId,
                                      final String statementId,
                                      final String text,
                                      final long   until,
                                      final int    numEligibleSupporters,
                                      final int    propSupportNeeded,
                                      final long   userActiveUntil)
    {
        // the user exists
        Assert.assertTrue(dataManager.isUserExists(userId));

        // the user is active
        Assert.assertTrue(dataManager.isUserActive(userId));

        // the statement exists
        Assert.assertTrue(dataManager.isStatementExists(statementId));

        // the state of the statement is ACTIVE
        Assert.assertEquals(StatementState.ACTIVE, dataManager.getState(statementId));

        // the system has at least one active user and statement
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertTrue(dataManager.hasActiveStatements());

        // the submission time and TTL make sense
        Assert.assertEquals(now, dataManager.getSubmissionTime(statementId));
        Assert.assertEquals(100, dataManager.getStatementTTL(until - 100, statementId));

        // the submitter, text, num eligible supporters and prop support needed all match
        Assert.assertEquals(userId, dataManager.getSubmitter(statementId));
        Assert.assertEquals(text, dataManager.getText(statementId));
        Assert.assertEquals(numEligibleSupporters, dataManager.getNumEligibleSupporters(statementId));
        Assert.assertEquals(propSupportNeeded, dataManager.getPropSupportNeeded(statementId));

        // the statement is new so it has no support
        Assert.assertEquals(0, dataManager.getSupportCount(statementId));

        // the user's TTL makes sense
        Assert.assertEquals(100, dataManager.getUserTTL(userActiveUntil - 100, userId));

        // the user who submitted it can support it but does not yet support it
        Assert.assertTrue(dataManager.isSupportEligible(userId, statementId));
        Assert.assertFalse(dataManager.isSupported(userId, statementId));

        // the statement is in the list associated with the user exactly once
        validateFoundCount(dataManager.getSubmittedStatementIds(userId), statementId, 1);

        // the statement is in the list of active statements exactly once
        validateFoundCount(dataManager.getActiveStatementIds(), statementId, 1);

        // the statement is not in the lists for inactive, voting, accepted, nor rejected statements
        validateFoundCount(dataManager.getInactiveStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getVotingStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getAcceptedStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getRejectedStatementIds(), statementId, 0);
    }

    protected void support(final String userId,
                           final String statementId,
                           final long userActiveUntil)
    {
        support(true, true, userId, statementId, userActiveUntil);
    }

    protected void support(final boolean checkPreconditions,
                           final boolean checkPostconditions,
                           final String userId,
                           final String statementId,
                           final long userActiveUntil)
    {
        if (checkPreconditions)
        {
            supportPreconditions(userId, statementId);
        }

        final int preSupportCount = dataManager.getSupportCount(statementId);

        dataManager.support(T0, userId, statementId, userActiveUntil);

        if (checkPostconditions)
        {
            supportPostconditions(preSupportCount, userId, statementId, userActiveUntil);
        }
    }

    private void supportPreconditions(final String userId, final String statementId)
    {
        // the user exists
        Assert.assertTrue(dataManager.isUserExists(userId));

        // the statement exists
        Assert.assertTrue(dataManager.isStatementExists(statementId));

        // the state of the statement is ACTIVE
        Assert.assertEquals(StatementState.ACTIVE, dataManager.getState(statementId));

        // the system has at least one active user and statement
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertTrue(dataManager.hasActiveStatements());

        // the user can but does not already support the statement
        Assert.assertTrue (dataManager.isSupportEligible(userId, statementId));
        Assert.assertFalse(dataManager.isSupported(userId, statementId));

        // the statement is in the list of active statements
        validateFoundCount(dataManager.getActiveStatementIds(), statementId, 1);

        // the statement is not in the list of inactive, voting, accepted, nor rejected statements
        validateFoundCount(dataManager.getInactiveStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getVotingStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getAcceptedStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getRejectedStatementIds(), statementId, 0);

        // the statement is not in the list of statements the user supports
        validateFoundCount(dataManager.getSupportedStatementsIds(userId), statementId, 0);
    }

    private void supportPostconditions(final int preSupportCount,
                                       final String userId,
                                       final String statementId,
                                       final long userActiveUntil)
    {
        // the follow are always true after successfully supporting a statement

        // the user exists
        Assert.assertTrue(dataManager.isUserExists(userId));

        // the user is active
        Assert.assertTrue(dataManager.isUserActive(userId));

        // the statement exists
        Assert.assertTrue(dataManager.isStatementExists(statementId));

        // the state of the statement is ACTIVE
        Assert.assertEquals(StatementState.ACTIVE, dataManager.getState(statementId));

        // the system has at least one active user and statement
        Assert.assertTrue(dataManager.hasActiveUsers());
        Assert.assertTrue(dataManager.hasActiveStatements());

        // the user now supports it
        Assert.assertTrue(dataManager.isSupported(userId, statementId));

        // the support count is one more than it was before
        Assert.assertEquals(preSupportCount + 1, dataManager.getSupportCount(statementId));

        // the user's TTL makes sense
        Assert.assertEquals(100, dataManager.getUserTTL(userActiveUntil - 100, userId));

        // the statement is in the list of active statements
        validateFoundCount(dataManager.getActiveStatementIds(), statementId, 1);

        // the statement is not in the list of inactive, voting, accepted, nor rejected statements
        validateFoundCount(dataManager.getInactiveStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getVotingStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getAcceptedStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getRejectedStatementIds(), statementId, 0);

        // the statement is in the list of statements the user supports
        validateFoundCount(dataManager.getSupportedStatementsIds(userId), statementId, 1);
    }

    protected void beginVote(final long    now,
                             final String  statementId,
                             final long    until,
                             final int     numEligibleVoters,
                             final int     propVotesNeeded,
                             final int     propYesesNeeded)
    {
        beginVote(true, true, now, statementId, until, numEligibleVoters, propVotesNeeded, propYesesNeeded);
    }

    protected void beginVote(final boolean checkPreconditions,
                             final boolean checkPostconditions,
                             final long    now,
                             final String  statementId,
                             final long    until,
                             final int     numEligibleVoters,
                             final int     propVotesNeeded,
                             final int     propYesesNeeded)
    {
        if (checkPreconditions)
        {
            beginVotePreconditions(statementId);
        }

        final int preNumEligibleSupporters = dataManager.getNumEligibleSupporters(statementId);
        final int prePropSupportNeeded     = dataManager.getPropSupportNeeded(statementId);
        final int preSupportCount          = dataManager.getSupportCount(statementId);

        dataManager.beginVote(now, statementId, until, numEligibleVoters, propVotesNeeded, propYesesNeeded);

        if (checkPostconditions)
        {
            beginVotePostconditions(preNumEligibleSupporters, prePropSupportNeeded, preSupportCount, now, statementId, until, numEligibleVoters, propVotesNeeded, propYesesNeeded);
        }
    }

    private void beginVotePreconditions(final String statementId)
    {
        // the statement exists
        Assert.assertTrue(dataManager.isStatementExists(statementId));

        // the statement state is ACTIVE
        Assert.assertEquals(StatementState.ACTIVE, dataManager.getState(statementId));

        // the system has at least one active statement
        Assert.assertTrue(dataManager.hasActiveStatements());

        // the statement is in the list of active statements
        validateFoundCount(dataManager.getActiveStatementIds(), statementId, 1);

        // the statement is not in the list of inactive, voting, accepted, nor rejected statements
        validateFoundCount(dataManager.getInactiveStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getVotingStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getAcceptedStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getRejectedStatementIds(), statementId, 0);
    }

    private void beginVotePostconditions(final int     preNumEligibleSupporters,
                                         final int     prePropSupportNeeded,
                                         final int     preSupportCount,
                                         final long    now,
                                         final String  statementId,
                                         final long    until,
                                         final int     numEligibleVoters,
                                         final int     propVotesNeeded,
                                         final int     propYesesNeeded)
    {
        // the statement exists
        Assert.assertTrue(dataManager.isStatementExists(statementId));

        // the statement state is VOTING
        Assert.assertEquals(StatementState.VOTING, dataManager.getState(statementId));

        // the statement is in the list of voting statements
        validateFoundCount(dataManager.getVotingStatementIds(), statementId, 1);

        // the statement is not in the list of active inactive, accepted, nor rejected statements
        validateFoundCount(dataManager.getActiveStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getInactiveStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getAcceptedStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getRejectedStatementIds(), statementId, 0);

        // the begin vote time, TTL, num eligible voters, prop votes needed, and prop yeses needed all match
        Assert.assertEquals(now,               dataManager.getVoteBeginTime(statementId));
        Assert.assertEquals(100,               dataManager.getVoteTTL(until - 100, statementId));
        Assert.assertEquals(numEligibleVoters, dataManager.getNumEligibleVoters(statementId));
        Assert.assertEquals(propVotesNeeded,   dataManager.getPropVotesNeeded(statementId));
        Assert.assertEquals(propYesesNeeded,   dataManager.getPropYesesNeeded(statementId));

        // there is the same num eligible supporters, prop support needed, and support count as before the vote began
        Assert.assertEquals(preNumEligibleSupporters, dataManager.getNumEligibleSupporters(statementId));
        Assert.assertEquals(prePropSupportNeeded,     dataManager.getPropSupportNeeded(statementId));
        Assert.assertEquals(preSupportCount,          dataManager.getSupportCount(statementId));

        // there are no votes for the statement
        Assert.assertEquals(0, dataManager.getVoteCount(statementId));

        // there are no yeses for the statement
        Assert.assertEquals(0, dataManager.getYesCount(statementId));
    }

    protected void endVoteAccepted(final String statementId)
    {
        endVoteAccepted(true, true, statementId);
    }

    protected void endVoteAccepted(final boolean checkPreconditions,
                                   final boolean checkPostconditions,
                                   final String  statementId)
    {
        if (checkPreconditions)
        {
            endVoteAcceptedPreconditions(statementId);
        }

        final int preNumEligibleSupporters = dataManager.getNumEligibleSupporters(statementId);
        final int prePropSupportNeeded     = dataManager.getPropSupportNeeded(statementId);
        final int preSupportCount          = dataManager.getSupportCount(statementId);
        final int preNumEligibleVoters     = dataManager.getNumEligibleVoters(statementId);
        final int prePropVotesNeeded       = dataManager.getPropVotesNeeded(statementId);
        final int prePropYesesNeeded       = dataManager.getPropYesesNeeded(statementId);
        final int preVoteCount             = dataManager.getVoteCount(statementId);
        final int preYesCount              = dataManager.getYesCount(statementId);

        dataManager.endVoteAccepted(T0, statementId);

        if (checkPostconditions)
        {
            endVoteAcceptedPostconditions(preNumEligibleSupporters, prePropSupportNeeded, preSupportCount, preNumEligibleVoters, prePropVotesNeeded, prePropYesesNeeded, preVoteCount, preYesCount, statementId);
        }
    }

    private void endVoteAcceptedPreconditions(final String statementId)
    {
        // the statement exists
        Assert.assertTrue(dataManager.isStatementExists(statementId));

        // the statement state is VOTING
        Assert.assertEquals(StatementState.VOTING, dataManager.getState(statementId));

        // the statement is in the list of voting statements
        validateFoundCount(dataManager.getVotingStatementIds(), statementId, 1);

        // the statement is not in the list of active, inactive, accepted, nor rejected statements
        validateFoundCount(dataManager.getActiveStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getInactiveStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getAcceptedStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getRejectedStatementIds(), statementId, 0);
    }

    private void endVoteAcceptedPostconditions(final int     preNumEligibleSupporters,
                                               final int     prePropSupportNeeded,
                                               final int     preSupportCount,
                                               final int     preNumEligibleVoters,
                                               final int     prePropVotesNeeded,
                                               final int     prePropYesesNeeded,
                                               final int     preVoteCount,
                                               final int     preYesCount,
                                               final String  statementId)
    {
        // the statement exists
        Assert.assertTrue(dataManager.isStatementExists(statementId));

        // the statement state is ACCEPTED
        Assert.assertEquals(StatementState.ACCEPTED, dataManager.getState(statementId));

        // the statement is in the list of accepted statements
        validateFoundCount(dataManager.getAcceptedStatementIds(), statementId, 1);

        // the statement is not in the list of active, inactive, voting, nor rejected statements
        validateFoundCount(dataManager.getActiveStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getInactiveStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getVotingStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getRejectedStatementIds(), statementId, 0);

        // there is the same num eligible supporters, prop support needed, and support count as before voting ended
        Assert.assertEquals(preNumEligibleSupporters, dataManager.getNumEligibleSupporters(statementId));
        Assert.assertEquals(prePropSupportNeeded,     dataManager.getPropSupportNeeded(statementId));
        Assert.assertEquals(preSupportCount,          dataManager.getSupportCount(statementId));

        // there is the same num eligible voters, prop votes needed, prop yeses needed, vote count, and yes count as before voting ended
        Assert.assertEquals(preNumEligibleVoters, dataManager.getNumEligibleVoters(statementId));
        Assert.assertEquals(prePropVotesNeeded,   dataManager.getPropVotesNeeded(statementId));
        Assert.assertEquals(prePropYesesNeeded,   dataManager.getPropYesesNeeded(statementId));
        Assert.assertEquals(preVoteCount,         dataManager.getVoteCount(statementId));
        Assert.assertEquals(preYesCount,          dataManager.getYesCount(statementId));
    }

    protected void endVoteRejected(final String statementId)
    {
        endVoteRejected(true, true, statementId);
    }

    protected void endVoteRejected(final boolean checkPreconditions,
                                   final boolean checkPostconditions,
                                   final String  statementId)
    {
        if (checkPreconditions)
        {
            endVoteRejectedPreconditions(statementId);
        }

        final int preNumEligibleSupporters = dataManager.getNumEligibleSupporters(statementId);
        final int prePropSupportNeeded     = dataManager.getPropSupportNeeded(statementId);
        final int preSupportCount          = dataManager.getSupportCount(statementId);
        final int preNumEligibleVoters     = dataManager.getNumEligibleVoters(statementId);
        final int prePropVotesNeeded       = dataManager.getPropVotesNeeded(statementId);
        final int prePropYesesNeeded       = dataManager.getPropYesesNeeded(statementId);
        final int preVoteCount             = dataManager.getVoteCount(statementId);
        final int preYesCount              = dataManager.getYesCount(statementId);

        dataManager.endVoteRejected(T0, statementId);

        if (checkPostconditions)
        {
            endVoteRejectedPostconditions(preNumEligibleSupporters, prePropSupportNeeded, preSupportCount, preNumEligibleVoters, prePropVotesNeeded, prePropYesesNeeded, preVoteCount, preYesCount, statementId);
        }
    }

    private void endVoteRejectedPreconditions(final String statementId)
    {
        // the statement exists
        Assert.assertTrue(dataManager.isStatementExists(statementId));

        // the statement state is VOTING
        Assert.assertEquals(StatementState.VOTING, dataManager.getState(statementId));

        // the statement is in the list of voting statements
        validateFoundCount(dataManager.getVotingStatementIds(), statementId, 1);

        // the statement is not in the list of active, inactive, accepted, nor rejected statements
        validateFoundCount(dataManager.getActiveStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getInactiveStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getAcceptedStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getRejectedStatementIds(), statementId, 0);
    }

    private void endVoteRejectedPostconditions(final int     preNumEligibleSupporters,
                                               final int     prePropSupportNeeded,
                                               final int     preSupportCount,
                                               final int     preNumEligibleVoters,
                                               final int     prePropVotesNeeded,
                                               final int     prePropYesesNeeded,
                                               final int     preVoteCount,
                                               final int     preYesCount,
                                               final String  statementId)
    {
        // the statement exists
        Assert.assertTrue(dataManager.isStatementExists(statementId));

        // the statement state is REJECTED
        Assert.assertEquals(StatementState.REJECTED, dataManager.getState(statementId));

        // the statement is in the list of rejected statements
        validateFoundCount(dataManager.getRejectedStatementIds(), statementId, 1);

        // the statement is not in the list of active, inactive, voting, nor accepted statements
        validateFoundCount(dataManager.getActiveStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getInactiveStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getVotingStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getAcceptedStatementIds(), statementId, 0);

        // there is the same num eligible supporters, prop support needed, and support count as before voting ended
        Assert.assertEquals(preNumEligibleSupporters, dataManager.getNumEligibleSupporters(statementId));
        Assert.assertEquals(prePropSupportNeeded,     dataManager.getPropSupportNeeded(statementId));
        Assert.assertEquals(preSupportCount,          dataManager.getSupportCount(statementId));

        // there is the same num eligible voters, prop votes needed, prop yeses needed, vote count, and yes count as before voting ended
        Assert.assertEquals(preNumEligibleVoters, dataManager.getNumEligibleVoters(statementId));
        Assert.assertEquals(prePropVotesNeeded,   dataManager.getPropVotesNeeded(statementId));
        Assert.assertEquals(prePropYesesNeeded,   dataManager.getPropYesesNeeded(statementId));
        Assert.assertEquals(preVoteCount,         dataManager.getVoteCount(statementId));
        Assert.assertEquals(preYesCount,          dataManager.getYesCount(statementId));
    }

    protected void vote(final long   now,
                        final String userId,
                        final String statementId,
                        final Vote   vote,
                        final long   userActiveUntil)
    {
        vote(true, true, now, userId, statementId, vote, userActiveUntil);
    }

    protected void vote(final boolean checkPreconditions,
                        final boolean checkPostconditions,
                        final long   now,
                        final String userId,
                        final String statementId,
                        final Vote   vote,
                        final long   userActiveUntil)
    {
        if (checkPreconditions)
        {
            votePreconditions(userId, statementId);
        }

        final int preVoteCount = dataManager.getVoteCount(statementId);
        final int preYesCount  = dataManager.getYesCount(statementId);

        dataManager.vote(now, userId, statementId, vote, userActiveUntil);

        if (checkPostconditions)
        {
            votePostconditions(preVoteCount, preYesCount, userId, statementId, vote, userActiveUntil);
        }
    }

    private void votePreconditions(final String userId, final String statementId)
    {
        // the user exists
        Assert.assertTrue(dataManager.isUserExists(userId));

        // the user need not be active (in the case she was active when the vote began)

        // the statement exists
        Assert.assertTrue(dataManager.isStatementExists(statementId));

        // the state of the statement is VOTING
        Assert.assertEquals(StatementState.VOTING, dataManager.getState(statementId));

        // the statement is in the list of voting statements
        validateFoundCount(dataManager.getVotingStatementIds(), statementId, 1);

        // the statement is not in the list of active, inactive, accepted, nor rejected statements
        validateFoundCount(dataManager.getActiveStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getInactiveStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getAcceptedStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getRejectedStatementIds(), statementId, 0);

        // the user can vote for the statement but has not yet done so
        Assert.assertTrue(dataManager.isVoteEligible(userId, statementId));
        Assert.assertEquals(Vote.ABSTAIN, dataManager.getVote(userId, statementId));

        // the statement is in the list of statements the user is eligible to vote for
        validateFoundCount(dataManager.getVotedStatementIds(userId), statementId, 1);
    }

    private void votePostconditions(final int preVoteCount,
                                    final int preYesCount,
                                    final String userId,
                                    final String statementId,
                                    final Vote vote,
                                    final long userActiveUntil)
    {
        // the user exists
        Assert.assertTrue(dataManager.isUserExists(userId));

        // the user is active
        Assert.assertTrue(dataManager.isUserActive(userId));

        // the user TTL makes sense
        Assert.assertEquals(100, dataManager.getUserTTL(userActiveUntil - 100, userId));

        // the statement exists
        Assert.assertTrue(dataManager.isStatementExists(statementId));

        // the state of the statement is VOTING
        Assert.assertEquals(StatementState.VOTING, dataManager.getState(statementId));

        // the statement is in the list of voting statements
        validateFoundCount(dataManager.getVotingStatementIds(), statementId, 1);

        // the statement is not in the list of active, inactive, accepted, nor rejected statements
        validateFoundCount(dataManager.getActiveStatementIds(),   statementId, 0);
        validateFoundCount(dataManager.getInactiveStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getAcceptedStatementIds(), statementId, 0);
        validateFoundCount(dataManager.getRejectedStatementIds(), statementId, 0);

        // the user has voted and can no longer do so
        Assert.assertEquals(vote, dataManager.getVote(userId, statementId));
        Assert.assertFalse(dataManager.isVoteEligible(userId, statementId));

        // the statement is in the list of statements the user is eligible to vote for
        validateFoundCount(dataManager.getVotedStatementIds(userId), statementId, 1);

        // the vote counts make sense
        Assert.assertEquals(preVoteCount + 1, dataManager.getVoteCount(statementId));
        if (vote == Vote.YES)
        {
            Assert.assertEquals(preYesCount + 1, dataManager.getYesCount(statementId));
        }
        else
        {
            Assert.assertEquals(preYesCount, dataManager.getYesCount(statementId));
        }
    }
}
