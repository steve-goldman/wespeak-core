package com.wespeak.core.datamanager.impl.memory;

import com.wespeak.core.Vote;
import com.wespeak.core.datamanager.VotesTable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryVotesTable implements VotesTable
{
    private final Map<String, Map<String, Vote>> votesByUserId = new HashMap<String, Map<String, Vote>>();
    private final Map<String, Integer>           votesCount    = new HashMap<String, Integer>();
    private final Map<String, Integer>           yesesCount    = new HashMap<String, Integer>();

    @Override
    public boolean eligible(final String userId, final String statementId)
    {
        final Map<String, Vote> votes = votesByUserId.get(userId);

        return votes != null && votes.containsKey(statementId);
    }

    @Override
    public Vote getVote(final String userId, final String statementId)
    {
        return votesByUserId.get(userId).get(statementId);
    }

    @Override
    public Iterator<String> getVoted(final String userId)
    {
        if (!votesByUserId.containsKey(userId))
        {
            votesByUserId.put(userId, new LinkedHashMap<String, Vote>());
        }

        return votesByUserId.get(userId).keySet().iterator();
    }

    @Override
    public void beginVote(final String statementId)
    {
        votesCount.put(statementId, 0);
        yesesCount.put(statementId, 0);
    }

    @Override
    public void setEligible(final String userId, final String statementId)
    {
        if (!votesByUserId.containsKey(userId))
        {
            votesByUserId.put(userId, new LinkedHashMap<String, Vote>());
        }

        votesByUserId.get(userId).put(statementId, Vote.ABSTAIN);
    }

    @Override
    public int getVoteCount(final String statementId)
    {
        return votesCount.get(statementId);
    }

    @Override
    public int getYesCount(final String statementId)
    {
        return yesesCount.get(statementId);
    }

    @Override
    public void vote(final String userId, final String statementId, final Vote vote)
    {
        votesByUserId.get(userId).put(statementId, vote);

        votesCount.put(statementId, votesCount.get(statementId) + 1);

        if (vote == Vote.YES)
        {
            yesesCount.put(statementId, yesesCount.get(statementId) + 1);
        }
    }
}
