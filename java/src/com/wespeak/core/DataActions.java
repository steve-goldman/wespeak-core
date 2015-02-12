package com.wespeak.core;

public interface DataActions
{
    //
    // user actions
    //

    void heartbeat(long now, String userId, long userActiveUntil);

    void submit   (long   now,
                   String userId,
                   String statementId,
                   String text,
                   long   statementActiveUntil,
                   int    numEligibleSupporters,
                   int    propSupportNeeded,
                   long   userActiveUntil);

    void support  (String userId, String statementId, long userActiveUntil);

    void vote     (String userId, String statementId, Vote vote, long userActiveUntil);


    //
    // engine actions
    //

    void timeoutUser     (String userId);

    void timeoutStatement(String statementId);

    void beginVote       (long   now,
                          String statementId,
                          long   until,
                          int    numEligibleVoters,
                          int    propVotesNeeded,
                          int    propYesesNeeded);

    void endVoteAccepted (String statementId);

    void endVoteRejected (String statementId);

}
