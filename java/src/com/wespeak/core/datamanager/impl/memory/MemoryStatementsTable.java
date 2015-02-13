package com.wespeak.core.datamanager.impl.memory;

import com.wespeak.core.StatementState;
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
        final int    propSupportNeeded;

        StatementState state = StatementState.ACTIVE;
        long  voteBeginTime;
        long  voteEndTime;
        int   numEligibleVoters;
        int   propVotesNeeded;
        int   propYesesNeeded;

        StatementData(final String statementId,
                      final String userId,
                      final String text,
                      final long   submissionTime,
                      final long   expirationTime,
                      final int    numEligibleSupporters,
                      final int    propSupportNeeded)
        {
            this.statementId           = statementId;
            this.userId                = userId;
            this.text                  = text;
            this.submissionTime        = submissionTime;
            this.expirationTime        = expirationTime;
            this.numEligibleSupporters = numEligibleSupporters;
            this.propSupportNeeded     = propSupportNeeded;
        }
    }

    private final Map<String, StatementData> statementsById     = new LinkedHashMap<String, StatementData>();
    private final Deque<StatementData>       activeStatements   = new LinkedList<StatementData>();
    private final Deque<StatementData>       inactiveStatements = new LinkedList<StatementData>();
    private final Deque<StatementData>       votingStatements   = new LinkedList<StatementData>();
    private final Deque<StatementData>       acceptedStatements = new LinkedList<StatementData>();
    private final Deque<StatementData>       rejectedStatements = new LinkedList<StatementData>();

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
    public StatementState getState(final String statementId)
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
    public int getPropSupportNeeded(final String statementId)
    {
        return statementsById.get(statementId).propSupportNeeded;
    }

    @Override
    public int getNumEligibleVoters(final String statementId)
    {
        return statementsById.get(statementId).numEligibleVoters;
    }

    @Override
    public int getPropVotesNeeded(final String statementId)
    {
        return statementsById.get(statementId).propVotesNeeded;
    }

    @Override
    public int getPropYesesNeeded(final String statementId)
    {
        return statementsById.get(statementId).propYesesNeeded;
    }

    @Override
    public boolean hasActiveStatements()
    {
        return !activeStatements.isEmpty();
    }

    @Override
    public String getOldestActiveStatement()
    {
        return activeStatements.peekLast().statementId;
    }

    private Iterator<String> makeIterator(final Queue<StatementData> queue)
    {
        final Iterator<StatementData> iter = queue.iterator();
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
    public Iterator<String> getActiveStatements()
    {
        return makeIterator(activeStatements);
    }

    @Override
    public Iterator<String> getInactiveStatements()
    {
        return makeIterator(inactiveStatements);
    }

    @Override
    public Iterator<String> getVotingStatements()
    {
        return makeIterator(votingStatements);
    }

    @Override
    public Iterator<String> getAcceptedStatements()
    {
        return makeIterator(acceptedStatements);
    }

    @Override
    public Iterator<String> getRejectedStatements()
    {
        return makeIterator(rejectedStatements);
    }

    @Override
    public Iterator<String> getUserStatementIds(final String userId)
    {
        // nextIter stays one ahead of iter and always points at a record submitted by userId
        final Iterator<String> nextIter = statementsById.keySet().iterator();
        final Iterator<String> iter     = statementsById.keySet().iterator();

        while (nextIter.hasNext())
        {
            final StatementData statementData = statementsById.get(nextIter.next());
            if (userId.equals(statementData.userId))
            {
                break;
            }
            iter.next();
        }

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
                final String statementId = iter.next();

                // now nextIter and iter are at the same place
                while (nextIter.hasNext())
                {
                    final StatementData statementData = statementsById.get(nextIter.next());
                    if (userId.equals(statementData.userId))
                    {
                        break;
                    }
                    iter.next();
                }

                return statementId;
            }
        };
    }

    @Override
    public void addStatement(final String statementId,
                             final String userId,
                             final String text,
                             final long   submissionTime,
                             final long   expirationTime,
                             final int    numEligibleSupporters,
                             final int    propSupportNeeded)
    {
        final StatementData statementData = new StatementData(
                statementId,
                userId,
                text,
                submissionTime,
                expirationTime,
                numEligibleSupporters,
                propSupportNeeded);

        statementsById.put(statementId, statementData);
        activeStatements.addFirst(statementData);
    }

    @Override
    public void setInactive(final String statementId)
    {
        final StatementData statementData = statementsById.get(statementId);
        statementData.state = StatementState.INACTIVE;

        activeStatements.remove(statementData);
        inactiveStatements.addFirst(statementData);
    }

    @Override
    public void beginVote(final String statementId,
                          final long   voteBeginTime,
                          final long   voteEndTime,
                          final int    numEligibleVoters,
                          final int    propVotesNeeded,
                          final int    propYesesNeeded)
    {
        final StatementData statementData = statementsById.get(statementId);
        statementData.state = StatementState.VOTING;

        activeStatements.remove(statementData);
        votingStatements.addFirst(statementData);

        statementData.voteBeginTime     = voteBeginTime;
        statementData.voteEndTime       = voteEndTime;
        statementData.numEligibleVoters = numEligibleVoters;
        statementData.propVotesNeeded   = propVotesNeeded;
        statementData.propYesesNeeded   = propYesesNeeded;
    }

    @Override
    public void endVoteAccepted(final String statementId)
    {
        final StatementData statementData = statementsById.get(statementId);
        statementData.state = StatementState.ACCEPTED;

        votingStatements.remove(statementData);
        acceptedStatements.addFirst(statementData);
    }

    @Override
    public void endVoteRejected(final String statementId)
    {
        final StatementData statementData = statementsById.get(statementId);
        statementData.state = StatementState.REJECTED;

        votingStatements.remove(statementData);
        rejectedStatements.addFirst(statementData);
    }
}
