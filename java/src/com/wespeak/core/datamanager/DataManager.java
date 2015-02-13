package com.wespeak.core.datamanager;

import com.wespeak.core.DataActions;
import com.wespeak.core.DataQueries;
import com.wespeak.core.StatementState;
import com.wespeak.core.Vote;

import java.util.Iterator;

public class DataManager implements DataActions, DataQueries
{
    private final UsersTable      usersTable;
    private final StatementsTable statementsTable;
    private final SupportTable    supportTable;
    private final VotesTable      votesTable;

    DataManager(final UsersTable      usersTable,
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
    public void heartbeat(final long now, final String userId, final long userActiveUntil)
    {
        validateTimeOrdering(now, userActiveUntil);

        // if the user is currently active, keep the original "from" time
        if (usersTable.exists(userId) && usersTable.isActive(userId))
        {
            usersTable.extendActive(userId, userActiveUntil);
        }
        // otherwise use "now" as the "from" time
        else
        {
            usersTable.setActive(userId, now, userActiveUntil);
        }
    }

    @Override
    public void submit(final long   now,
                       final String userId,
                       final String statementId,
                       final String text,
                       final long   statementActiveUntil,
                       final int    numEligibleSupporters,
                       final int    propSupportNeeded,
                       final long   userActiveUntil)
    {
        validateUserExists(userId);

        // statement must NOT already exist
        validateStatementExists(statementId, false);

        validateTimeOrdering(now, statementActiveUntil);

        validateSupportCounts(numEligibleSupporters, propSupportNeeded);

        if (usersTable.isActive(userId))
        {
            usersTable.extendActive(userId, userActiveUntil);
        }
        else
        {
            usersTable.setActive(userId, now, userActiveUntil);
        }

        statementsTable.addStatement(statementId, userId, text, now, statementActiveUntil, numEligibleSupporters, propSupportNeeded);
    }

    @Override
    public void support(final String userId, final String statementId, final long userActiveUntil)
    {
        // pre-conditions are validated in canSupport, which the client must call

        usersTable.extendActive(userId, userActiveUntil);

        supportTable.support(userId, statementId);
    }

    @Override
    public void vote(final long now, final String userId, final String statementId, final Vote vote, final long userActiveUntil)
    {
        // pre-conditions are validated in canVote, which the client must call

        if (usersTable.isActive(userId))
        {
            usersTable.extendActive(userId, userActiveUntil);
        }
        else
        {
            usersTable.setActive(userId, now, userActiveUntil);
        }

        votesTable.vote(userId, statementId, vote);
    }

    @Override
    public void timeoutUser(final String userId)
    {
        validateUserExists(userId);

        validateUserActive(userId);

        usersTable.setInactive(userId);
    }

    @Override
    public void timeoutStatement(final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementActive(statementId);

        statementsTable.setInactive(statementId);
    }

    @Override
    public void beginVote(final long   now,
                          final String statementId,
                          final long   until,
                          final int    numEligibleVoters,
                          final int    propVotesNeeded,
                          final int    propYesesNeeded)
    {
        validateStatementExists(statementId);

        validateStatementActive(statementId);

        validateTimeOrdering(now, until);

        validateVoteCounts(numEligibleVoters, propVotesNeeded, propYesesNeeded);

        final Iterator<String> iter = usersTable.getActiveUsers();
        while (iter.hasNext())
        {
            votesTable.setEligible(iter.next(), statementId);
        }

        votesTable.beginVote(statementId);

        statementsTable.beginVote(statementId, now, until, numEligibleVoters, propVotesNeeded, propYesesNeeded);
    }

    @Override
    public void endVoteAccepted(final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementVoting(statementId);

        statementsTable.endVoteAccepted(statementId);
    }

    @Override
    public void endVoteRejected(final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementVoting(statementId);

        statementsTable.endVoteRejected(statementId);
    }

    @Override
    public boolean isUserExists(final String userId)
    {
        return usersTable.exists(userId);
    }

    @Override
    public boolean isUserActive(final String userId)
    {
        validateUserExists(userId);

        return usersTable.isActive(userId);
    }

    @Override
    public boolean isStatementExists(final String statementId)
    {
        return statementsTable.exists(statementId);
    }

    @Override
    public StatementState getState(String statementId)
    {
        return statementsTable.getState(statementId);
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

        validateUserActive(userId);

        return usersTable.getActiveTime(userId);
    }

    @Override
    public long getUserTTL(final long now, final String userId)
    {
        validateUserExists(userId);

        validateUserActive(userId);

        return usersTable.getExpirationTime(userId) - now;
    }

    @Override
    public boolean canSupport(final String userId, final String statementId)
    {
        validateUserExists(userId);

        validateUserActive(userId);

        validateStatementExists(statementId);

        validateStatementActive(statementId);

        validateUserNotSupports(userId, statementId);

        return usersTable.getActiveTime(userId) <= statementsTable.getSubmissionTime(statementId);
    }

    @Override
    public boolean canVote(final String userId, final String statementId)
    {
        validateUserExists(userId);

        // the user does not need to be active (in case she was active before the vote began)

        validateStatementExists(statementId);

        validateStatementVoting(statementId);

        return votesTable.eligible(userId, statementId) && votesTable.getVote(userId, statementId) == Vote.ABSTAIN;
    }

    @Override
    public int getNumEligibleSupporters(final String statementId)
    {
        validateStatementExists(statementId);

        return statementsTable.getNumEligibleSupporters(statementId);
    }

    @Override
    public int getSupportCount(final String statementId)
    {
        validateStatementExists(statementId);

        return supportTable.getSupportCount(statementId);
    }

    @Override
    public int getPropSupportNeeded(final String statementId)
    {
        validateStatementExists(statementId);

        return statementsTable.getPropSupportNeeded(statementId);
    }

    @Override
    public int getNumEligibleVoters(final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementIsOrEverVoted(statementId);

        return statementsTable.getNumEligibleVoters(statementId);
    }

    @Override
    public int getVoteCount(final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementIsOrEverVoted(statementId);

        return votesTable.getVoteCount(statementId);
    }

    @Override
    public int getPropVotesNeeded(final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementIsOrEverVoted(statementId);

        return statementsTable.getPropVotesNeeded(statementId);
    }

    @Override
    public int getYesCount(final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementIsOrEverVoted(statementId);

        return votesTable.getYesCount(statementId);
    }

    @Override
    public boolean hasActiveUsers()
    {
        return usersTable.hasActiveUsers();
    }

    @Override
    public int getPropYesesNeeded(final String statementId)
    {
        validateStatementExists(statementId);

        validateStatementIsOrEverVoted(statementId);

        return statementsTable.getPropYesesNeeded(statementId);
    }

    @Override
    public String getOldestActiveUserId()
    {
        return usersTable.getOldestActiveUserId();
    }

    @Override
    public boolean hasActiveStatements()
    {
        return statementsTable.hasActiveStatements();
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

        // user does not need to be active for this

        validateStatementExists(statementId);

        return supportTable.supports(userId, statementId);
    }

    @Override
    public Vote getVote(final String userId, final String statementId)
    {
        validateUserExists(userId);

        // user does not need to be active for this

        validateStatementExists(statementId);

        validateStatementIsOrEverVoted(statementId);

        validateUserEligibleToVote(userId, statementId);

        return votesTable.getVote(userId, statementId);
    }

    @Override
    public Iterator<String> getSubmittedStatementIds(final String userId)
    {
        validateUserExists(userId);

        // user does not need to be active for this

        return statementsTable.getUserStatementIds(userId);
    }

    @Override
    public Iterator<String> getSupportedStatementsIds(final String userId)
    {
        validateUserExists(userId);

        // user does not need to be active for this

        return supportTable.getSupported(userId);
    }

    @Override
    public Iterator<String> getVotedStatementIds(final String userId)
    {
        validateUserExists(userId);

        // user does not need to be active for this

        return votesTable.getVoted(userId);
    }

    @Override
    public Iterator<String> getActiveStatementIds()
    {
        return statementsTable.getActiveStatements();
    }

    @Override
    public Iterator<String> getInactiveStatementIds()
    {
        return statementsTable.getInactiveStatements();
    }

    @Override
    public Iterator<String> getVotingStatementIds()
    {
        return statementsTable.getVotingStatements();
    }

    @Override
    public Iterator<String> getAcceptedStatementIds()
    {
        return statementsTable.getAcceptedStatements();
    }

    @Override
    public Iterator<String> getRejectedStatementIds()
    {
        return statementsTable.getRejectedStatements();
    }

    private void validateUserActive(final String userId)
    {
        if (!usersTable.isActive(userId))
        {
            throw new IllegalStateException("user:" + userId + " is not active");
        }
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

    private void validateSupportCounts(final int numEligibleSupporters, final int propSupportNeeded)
    {
        if (numEligibleSupporters < 1)
        {
            throw new IllegalArgumentException("invalid numEligibleSupporters:" + numEligibleSupporters);
        }

        if (propSupportNeeded < 0 || propSupportNeeded > 100)
        {
            throw new IllegalArgumentException("propSupportNeeded:" + propSupportNeeded + " out of range [0,100]");
        }
    }

    private void validateStatementExists(final String statementId)
    {
        validateStatementExists(statementId, true);
    }

    private void validateStatementExists(final String statementId, final boolean exists)
    {
        if (statementsTable.exists(statementId) != exists)
        {
            throw new IllegalStateException("statement:" + statementId + (exists ? " does not exist" : "exists"));
        }
    }

    private void validateStatementActive(final String statementId)
    {
        if (statementsTable.getState(statementId) != StatementState.ACTIVE)
        {
            throw new IllegalStateException("statement:" + statementId + " is not active, state:" + statementsTable.getState(statementId));
        }
    }

    private void validateVoteCounts(final int numEligibleVoters, final int propVotesNeeded, final int propYesesNeeded)
    {
        if (numEligibleVoters < 1)
        {
            throw new IllegalArgumentException("invalid numEligibleVoters:" + numEligibleVoters);
        }

        if (propVotesNeeded < 0 || propVotesNeeded > 100)
        {
            throw new IllegalArgumentException("propVotesNeeded:" + propVotesNeeded + " out of range [0,100]");
        }

        if (propYesesNeeded < 0 || propYesesNeeded > 100)
        {
            throw new IllegalArgumentException("propYesesNeeded:" + propYesesNeeded + " out of range [0,100]");
        }
    }

    private void validateStatementVoting(final String statementId)
    {
        if (statementsTable.getState(statementId) != StatementState.VOTING)
        {
            throw new IllegalStateException("statement:" + statementId + " is not voting, state:" + statementsTable.getState(statementId));
        }
    }

    private void validateStatementIsOrEverVoted(final String statementId)
    {
        final StatementState state = statementsTable.getState(statementId);
        if (state != StatementState.VOTING &&
                state != StatementState.ACCEPTED &&
                state != StatementState.REJECTED)
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

    private void validateUserNotSupports(final String userId, final String statementId)
    {
        if (supportTable.supports(userId, statementId))
        {
            throw new IllegalStateException("user:" + userId + "already supports statementId:" + statementId);
        }
    }
}
