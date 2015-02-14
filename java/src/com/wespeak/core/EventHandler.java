package com.wespeak.core;

public interface EventHandler extends CommandHandler
{
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
