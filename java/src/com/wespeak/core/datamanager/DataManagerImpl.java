package com.wespeak.core.datamanager;

import com.wespeak.core.Vote;

import java.util.Iterator;

public class DataManagerImpl implements DataManager
{
    private final UsersTable      usersTable;
    private final StatementsTable statementsTable;
    private final SupportTable    supportTable;
    private final VotesTable      votesTable;

    public DataManagerImpl(final UsersTable      usersTable,
                           final StatementsTable statementsTable,
                           final SupportTable    supportTable,
                           final VotesTable      votesTable)
    {
        this.usersTable      = usersTable;
        this.statementsTable = statementsTable;
        this.supportTable    = supportTable;
        this.votesTable      = votesTable;
    }

    @Override
    public void heartbeat(final long now, final String userId, final long until)
    {
        validateTimeOrdering(now, until);

        // if the user is currently active, keep the original "from" time
        if (usersTable.exists(userId) && usersTable.isActive(userId))
        {
            usersTable.setActive(userId, usersTable.getActiveTime(userId), until);
        }
        // otherwise use "now" as the "from" time
        else
        {
            usersTable.setActive(userId, now, until);
        }
    }

    @Override
    public void submit(final long now,
                       final String userId,
                       final String statementId,
                       final String text,
                       final long until,
                       final int numEligibleSupporters,
                       final int numSupportNeeded)
    {
        validateUserExists(userId);

        validateTimeOrdering(now, until);

        validateSupportCounts(numEligibleSupporters, numSupportNeeded);

        statementsTable.addStatement(statementId, userId, text, now, until, numEligibleSupporters, numSupportNeeded);
    }

    @Override
    public void support(final String userId, final String statementId)
    {
        // pre-conditions are validated in canSupport, which the client must call

        supportTable.support(userId, statementId);
    }

    @Override
    public void vote(final String userId, final String statementId, final Vote vote)
    {
        // pre-conditions are validated in canVote, which the client must call

        votesTable.vote(userId, statementId, vote);
    }

    @Override
    public void timeoutUser(final String userId)
    {
        validateUserExists(userId);

        usersTable.setInactive(userId);
    }

    @Override
    public void timeoutStatement(final String statementId)
    {
        validateStatementExists(statementId);

        statementsTable.setInactive(statementId);
    }

    @Override
    public void beginVote(final long   now,
                          final String statementId,
                          final long   until,
                          final int    numEligibleVoters,
                          final int    numVotesNeeded,
                          final int    numYesesNeeded)
    {
        validateStatementExists(statementId);

        validateStatementActive(statementId);

        validateTimeOrdering(now, until);

        validateVoteCounts(numEligibleVoters, numVotesNeeded, numYesesNeeded);

        final Iterator<String> iter = usersTable.getActiveUsers();
        while (iter.hasNext())
        {
            votesTable.setEligible(iter.next(), statementId);
        }

        statementsTable.beginVote(statementId, now, until, numEligibleVoters, numVotesNeeded, numYesesNeeded);
    }

    @Override
    public void endVote(final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementVoting(statementId);

        statementsTable.endVote(statementId);
    }

    @Override
    public boolean isUserActive(final String userId)
    {
        validateUserExists(userId);

        return usersTable.isActive(userId);
    }

    @Override
    public boolean isStatementActive(final String statementId)
    {
        validateStatementExists(statementId);

        return statementsTable.getState(statementId) == StatementsTable.State.ACTIVE;
    }

    @Override
    public boolean isVoting(final String statementId)
    {
        validateStatementExists(statementId);

        return statementsTable.getState(statementId) == StatementsTable.State.VOTING;
    }

    @Override
    public boolean isAccepted(final String statementId)
    {
        validateStatementExists(statementId);

        return statementsTable.getState(statementId) == StatementsTable.State.ACCEPTED;
    }

    @Override
    public boolean isRejected(final String statementId)
    {
        validateStatementExists(statementId);

        return statementsTable.getState(statementId) == StatementsTable.State.REJECTED;
    }

    @Override
    public String getText(final String statementId)
    {
        validateStatementExists(statementId);

        return statementsTable.getText(statementId);
    }

    @Override
    public String getSubmitter(final String statementId)
    {
        validateStatementExists(statementId);

        return statementsTable.getSubmitter(statementId);
    }

    @Override
    public long getSubmissionTime(final String statementId)
    {
        validateStatementExists(statementId);

        return statementsTable.getSubmissionTime(statementId);
    }

    @Override
    public long getSubmissionTTL(final long now, final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementActive(statementId);

        return statementsTable.getExpirationTime(statementId) - now;
    }

    @Override
    public long getVoteBeginTime(final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementIsOrEverVoted(statementId);

        return statementsTable.getVoteBeginTime(statementId);
    }

    @Override
    public long getVoteTTL(final long now, final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementVoting(statementId);

        return statementsTable.getVoteEndTime(statementId) - now;
    }

    @Override
    public long getUserActiveTime(final String userId)
    {
        validateUserExists(userId);

        return usersTable.getActiveTime(userId);
    }

    @Override
    public long getUserTTL(final long now, final String userId)
    {
        validateUserExists(userId);

        return usersTable.getExpirationTime(userId) - now;
    }

    @Override
    public boolean canSupport(final String userId, final String statementId)
    {
        validateUserExists(userId);

        validateStatementExists(statementId);

        validateStatementActive(statementId);

        return getUserActiveTime(userId) < getSubmissionTime(statementId);
    }

    @Override
    public boolean canVote(final String userId, final String statementId)
    {
        validateUserExists(userId);

        validateStatementExists(statementId);

        validateStatementVoting(statementId);

        validateUserEligibleToVote(userId, statementId);

        return getUserActiveTime(userId) < getVoteBeginTime(statementId);
    }

    @Override
    public int getNumEligibleSupporters(final String statementId)
    {
        validateStatementExists(statementId);

        return statementsTable.getNumEligibleSupporters(statementId);
    }

    @Override
    public int getNumSupport(final String statementId)
    {
        validateStatementExists(statementId);

        return supportTable.getSupportCount(statementId);
    }

    @Override
    public int getNumSupportNeeded(final String statementId)
    {
        validateStatementExists(statementId);

        return statementsTable.getNumSupportNeeded(statementId);
    }

    @Override
    public int getNumEligibleVoters(final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementIsOrEverVoted(statementId);

        return statementsTable.getNumEligibleVoters(statementId);
    }

    @Override
    public int getNumVotes(final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementIsOrEverVoted(statementId);

        return votesTable.getVoteCount(statementId);
    }

    @Override
    public int getNumVotesNeeded(final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementIsOrEverVoted(statementId);

        return statementsTable.getNumVotesNeeded(statementId);
    }

    @Override
    public int getNumYeses(final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementIsOrEverVoted(statementId);

        return votesTable.getYesCount(statementId);
    }

    @Override
    public int getNumYesesNeeded(final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementIsOrEverVoted(statementId);

        return statementsTable.getNumYesesNeeded(statementId);
    }

    @Override
    public String getOldestActiveUserId()
    {
        return usersTable.getOldestActiveUserId();
    }

    @Override
    public String getOldestActiveStatementId()
    {
        return statementsTable.getOldestActiveStatement();
    }

    @Override
    public boolean isSupported(final String userId, final String statementId)
    {
        validateUserExists(userId);

        validateStatementExists(statementId);

        return supportTable.supports(userId, statementId);
    }

    @Override
    public Vote getVote(final String userId, final String statementId)
    {
        validateUserExists(userId);

        validateStatementExists(statementId);

        validateStatementIsOrEverVoted(statementId);

        validateUserEligibleToVote(userId, statementId);

        return votesTable.getVote(userId, statementId);
    }

    @Override
    public Iterator<String> getSubmittedStatementIds(final String userId)
    {
        validateUserExists(userId);

        return statementsTable.getStatementIds(userId);
    }

    @Override
    public Iterator<String> getSupportedStatementsIds(final String userId)
    {
        validateUserExists(userId);

        return supportTable.getSupported(userId);
    }

    @Override
    public Iterator<String> getVotedStatementIds(final String userId)
    {
        validateUserExists(userId);

        return votesTable.getVoted(userId);
    }

    private void validateUserExists(final String userId)
    {
        if (!usersTable.exists(userId))
        {
            throw new IllegalArgumentException("user:" + userId + " does not exist");
        }
    }

    private void validateTimeOrdering(final long from, final long until)
    {
        if (from > until)
        {
            throw new IllegalArgumentException("from=" + from + " > until=" + until);
        }
    }

    private void validateSupportCounts(final int numEligibleSupporters, final int numSupportNeeded)
    {
        if (numSupportNeeded > numEligibleSupporters)
        {
            throw new IllegalArgumentException("numSupportNeeded:" + numSupportNeeded + " > numEligibleSupporters:" + numEligibleSupporters);
        }
    }

    private void validateStatementExists(final String statementId)
    {
        if (!statementsTable.exists(statementId))
        {
            throw new IllegalArgumentException("statement:" + statementId + " does not exist");
        }
    }

    private void validateStatementActive(final String statementId)
    {
        if (statementsTable.getState(statementId) != StatementsTable.State.ACTIVE)
        {
            throw new IllegalStateException("statement:" + statementId + " is not active, state:" + statementsTable.getState(statementId));
        }
    }

    private void validateVoteCounts(final int numEligibleVoters, final int numVotesNeeded, final int numYesesNeeded)
    {
        if (numVotesNeeded > numEligibleVoters)
        {
            throw new IllegalArgumentException("numVotesNeeded:" + numVotesNeeded + " > numEligibleVoters:" + numEligibleVoters);
        }

        if (numYesesNeeded > numEligibleVoters)
        {
            throw new IllegalArgumentException("numYesesNeeded:" + numYesesNeeded + " > numEligibleVoters:" + numEligibleVoters);
        }

        if (numYesesNeeded > numVotesNeeded)
        {
            throw new IllegalArgumentException("numYesesNeeded:" + numYesesNeeded + " > numVotesNeeded:" + numVotesNeeded);
        }
    }

    private void validateStatementVoting(final String statementId)
    {
        if (statementsTable.getState(statementId) != StatementsTable.State.VOTING)
        {
            throw new IllegalStateException("statement:" + statementId + " is not voting, state:" + statementsTable.getState(statementId));
        }
    }

    private void validateStatementIsOrEverVoted(final String statementId)
    {
        final StatementsTable.State state = statementsTable.getState(statementId);
        if (state != StatementsTable.State.VOTING &&
                state != StatementsTable.State.ACCEPTED &&
                state != StatementsTable.State.REJECTED)
        {
            throw new IllegalStateException("statement:" + statementId + " has not entered voting state, state:" + state);
        }
    }

    private void validateUserEligibleToVote(final String userId, final String statementId)
    {
        if (!votesTable.eligible(userId, statementId))
        {
            throw new IllegalStateException("user:" + userId + " is/was not eligible to vote on statementId:" + statementId);
        }
    }
}
