package com.wespeak.core;

public interface CommandHandler
{
    void heartbeat(long now, String userId, long userActiveUntil);

    void submit   (long   now,
                   String userId,
                   String statementId,
                   String text,
                   long   statementActiveUntil,
                   int    numEligibleSupporters,
                   int    propSupportNeeded,
                   long   userActiveUntil);

    void support  (long now, String userId, String statementId, long userActiveUntil);

    void vote     (long now, String userId, String statementId, Vote vote, long userActiveUntil);
}
