package com.wespeak.core;

public interface EventHandler
{
    void heartbeat       (long now, String userId, long userActiveUntil);

    void submit          (long   now,
                          String userId,
                          String statementId,
                          String text,
                          long   statementActiveUntil,
                          int    numEligibleSupporters,
                          int    propSupportNeeded,
                          long   userActiveUntil);

    void support         (long now, String userId, String statementId, long userActiveUntil);

    void vote            (long now, String userId, String statementId, Vote vote, long userActiveUntil);

    void timeoutUser     (long now, String userId);

    void timeoutStatement(long now, String statementId);

    void beginVote       (long   now,
                          String statementId,
                          long   until,
                          int    numEligibleVoters,
                          int    propVotesNeeded,
                          int    propYesesNeeded);

    void endVoteAccepted (long now, String statementId);

    void endVoteRejected (long now, String statementId);
}
