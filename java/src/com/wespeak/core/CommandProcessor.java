package com.wespeak.core;

public interface CommandProcessor extends CommandHandler
{
    CommandResponse getLastResponse();

    String          getLastStatementId();
}
