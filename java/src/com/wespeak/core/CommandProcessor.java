package com.wespeak.core;

public interface CommandProcessor extends CommandHandler
{
    CommandResponse getLastResponse();

    void pulse(long now);
}
