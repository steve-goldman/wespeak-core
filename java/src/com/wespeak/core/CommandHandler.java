package com.wespeak.core;

public interface CommandHandler
{
    void pulse    (long now);

    void heartbeat(long now, String userId);

    void leave    (long now, String userId);

    void submit   (long now, String userId, String text);

    void support  (long now, String userId, String statementId);

    void vote     (long now, String userId, String statementId, Vote vote);
}
