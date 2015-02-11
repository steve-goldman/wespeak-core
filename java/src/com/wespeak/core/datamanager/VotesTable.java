package com.wespeak.core.datamanager;

import com.wespeak.core.Vote;

import java.util.Iterator;

public interface VotesTable
{
    //
    // getters
    //
    boolean  eligible        (String userId, String statementId);
    Vote     getVote         (String userId, String statementId);
    int      getVoteCount    (String statementId);
    int      getYesCount     (String statementId);
    Iterator<String> getVoted(String userId);

    //
    // setters
    //
    void     setEligible     (String userId, String statementId);
    void     vote            (String userId, String statementId, Vote vote);
}
