package com.wespeak.core.datamanager.impl.memory;

import com.wespeak.core.Vote;
import com.wespeak.core.datamanager.VotesTable;

import java.util.Iterator;

public class MemoryVotesTable implements VotesTable
{
    @Override
    public boolean eligible(String userId, String statementId)
    {
        return false;
    }

    @Override
    public Vote getVote(String userId, String statementId)
    {
        return null;
    }

    @Override
    public Iterator<String> getVoted(String userId)
    {
        return null;
    }

    @Override
    public void setEligible(String userId, String statementId)
    {

    }

    @Override
    public int getVoteCount(String statementId)
    {
        return 0;
    }

    @Override
    public int getYesCount(String statementId)
    {
        return 0;
    }

    @Override
    public void vote(String userId, String statementId, Vote vote)
    {

    }
}
