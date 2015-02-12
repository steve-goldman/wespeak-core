package com.wespeak.core.datamanager.impl.memory;

import com.wespeak.core.datamanager.StatementsTable;

import java.util.*;

public class MemoryStatementsTable implements StatementsTable
{
    private class StatementData
    {
        final String statementId;
        final String userId;
        final String text;
        final long   submissionTime;
        final long   expirationTime;
        final int    numEligibleSupporters;
        final int    numSupportNeeded;

        State state = State.ACTIVE;
        long  voteBeginTime;
        long  voteEndTime;
        int   numEligibleVoters;
        int   numVotesNeeded;
        int   numYesesNeeded;

        StatementData(final String statementId,
                      final String userId,
                      final String text,
                      final long   submissionTime,
                      final long   expirationTime,
                      final int    numEligibleSupporters,
                      final int    numSupportNeeded)
        {
            this.statementId           = statementId;
            this.userId                = userId;
            this.text                  = text;
            this.submissionTime        = submissionTime;
            this.expirationTime        = expirationTime;
            this.numEligibleSupporters = numEligibleSupporters;
            this.numSupportNeeded      = numSupportNeeded;
        }
    }

    private final Map<String, StatementData> statementsById   = new HashMap<String, StatementData>();
    private final Queue<StatementData>       activeStatements = new LinkedList<StatementData>();

    @Override
    public boolean exists(final String statementId)
    {
        return statementsById.containsKey(statementId);
    }

    @Override
    public String getSubmitter(final String statementId)
    {
        return statementsById.get(statementId).userId;
    }

    @Override
    public String getText(final String statementId)
    {
        return statementsById.get(statementId).text;
    }

    @Override
    public State getState(final String statementId)
    {
        return statementsById.get(statementId).state;
    }

    @Override
    public long getSubmissionTime(final String statementId)
    {
        return statementsById.get(statementId).submissionTime;
    }

    @Override
    public long getExpirationTime(final String statementId)
    {
        return statementsById.get(statementId).expirationTime;
    }

    @Override
    public long getVoteBeginTime(final String statementId)
    {
        return statementsById.get(statementId).voteBeginTime;
    }

    @Override
    public long getVoteEndTime(final String statementId)
    {
        return statementsById.get(statementId).voteEndTime;
    }

    @Override
    public int getNumEligibleSupporters(final String statementId)
    {
        return statementsById.get(statementId).numEligibleSupporters;
    }

    @Override
    public int getNumSupportNeeded(final String statementId)
    {
        return statementsById.get(statementId).numSupportNeeded;
    }

    @Override
    public int getNumEligibleVoters(final String statementId)
    {
        return statementsById.get(statementId).numEligibleVoters;
    }

    @Override
    public int getNumVotesNeeded(final String statementId)
    {
        return statementsById.get(statementId).numVotesNeeded;
    }

    @Override
    public int getNumYesesNeeded(final String statementId)
    {
        return statementsById.get(statementId).numYesesNeeded;
    }

    @Override
    public boolean hasActiveStatements()
    {
        return !activeStatements.isEmpty();
    }

    @Override
    public String getOldestActiveStatement()
    {
        return activeStatements.peek().statementId;
    }

    @Override
    public Iterator<String> getStatementIds(String userId)
    {
        final Iterator<StatementData> iter = activeStatements.iterator();

        return new Iterator<String>()
        {
            @Override
            public boolean hasNext()
            {
                return iter.hasNext();
            }

            @Override
            public String next()
            {
                return iter.next().statementId;
            }
        };
    }

    @Override
    public void addStatement(final String statementId,
                             final String userId,
                             final String text,
                             final long submissionTime,
                             final long expirationTime,
                             final int numEligibleSupporters,
                             final int numSupportNeeded)
    {
        final StatementData statementData = new StatementData(
                statementId,
                userId,
                text,
                submissionTime,
                expirationTime,
                numEligibleSupporters,
                numSupportNeeded);

        statementsById.put(statementId, statementData);
        activeStatements.add(statementData);
    }

    @Override
    public void setInactive(final String statementId)
    {
        final StatementData statementData = statementsById.get(statementId);
        statementData.state = State.INACTIVE;

        activeStatements.remove(statementData);
    }

    @Override
    public void beginVote(final String statementId,
                          final long voteBeginTime,
                          final long voteEndTime,
                          final int numEligibleVoters,
                          final int numVotesNeeded,
                          final int numYesesNeeded)
    {
        final StatementData statementData = statementsById.get(statementId);

        statementData.state             = State.VOTING;

        activeStatements.remove(statementData);

        statementData.voteBeginTime     = voteBeginTime;
        statementData.voteEndTime       = voteEndTime;
        statementData.numEligibleVoters = numEligibleVoters;
        statementData.numVotesNeeded    = numVotesNeeded;
        statementData.numYesesNeeded    = numYesesNeeded;
    }

    @Override
    public void endVoteAccepted(final String statementId)
    {
        statementsById.get(statementId).state = State.ACCEPTED;
    }

    @Override
    public void endVoteRejected(final String statementId)
    {
        statementsById.get(statementId).state = State.REJECTED;
    }
}
