package com.wespeak.core.datamanager.impl.memory;

import com.wespeak.core.datamanager.StatementsTable;

import java.util.Iterator;

public class MemoryStatementsTable implements StatementsTable
{
    @Override
    public boolean exists(String statementId)
    {
        return false;
    }

    @Override
    public String getSubmitter(String statementId)
    {
        return null;
    }

    @Override
    public String getText(String statementId)
    {
        return null;
    }

    @Override
    public State getState(String statementId)
    {
        return null;
    }

    @Override
    public long getSubmissionTime(String statementId)
    {
        return 0;
    }

    @Override
    public long getExpirationTime(String statementId)
    {
        return 0;
    }

    @Override
    public long getVoteBeginTime(String statementId)
    {
        return 0;
    }

    @Override
    public long getVoteEndTime(String statementId)
    {
        return 0;
    }

    @Override
    public int getNumEligibleSupporters(String statementId)
    {
        return 0;
    }

    @Override
    public int getNumSupportNeeded(String statementId)
    {
        return 0;
    }

    @Override
    public int getNumEligibleVoters(String statementId)
    {
        return 0;
    }

    @Override
    public int getNumVotesNeeded(String statementId)
    {
        return 0;
    }

    @Override
    public int getNumYesesNeeded(String statementId)
    {
        return 0;
    }

    @Override
    public String getOldestActiveStatement()
    {
        return null;
    }

    @Override
    public Iterator<String> getStatementIds(String userId)
    {
        return null;
    }

    @Override
    public void addStatement(String statementId, String userId, String text, long submissionTime, long expirationTime, int numEligibleSupporters, int numSupportNeeded)
    {

    }

    @Override
    public void setInactive(String statementId)
    {

    }

    @Override
    public void beginVote(String statementId, long voteBeginTime, long voteEndTime, int numEligibleVoters, int numVotesNeeded, int numYesesNeeded)
    {

    }

    @Override
    public void endVote(String statementId)
    {

    }
}
