package com.wespeak.core.datamanager;

import com.wespeak.core.Vote;

public interface VotesTable
{
    //
    // getters
    //
    boolean  eligible    (String userId, String statementId);
    Vote     getVote     (String userId, String statementId);
    String[] getVoted    (String userId);
    int      getVoteCount(String statementId);
    int      getYesCount (String statementId);

    //
    // setters
    //
    void     vote        (String userId, String statementId, Vote vote);
}
