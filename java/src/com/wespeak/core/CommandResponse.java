package com.wespeak.core;

public class CommandResponse
{
    public enum Code
    {
        OK,
        COMMAND_REJECT,
        BAD_COMMAND,
    }

    private final Code   code;
    private final String message;

    public CommandResponse(final Code code, final String message)
    {
        this.code    = code;
        this.message = message;
    }

    public Code getCode()
    {
        return code;
    }

    public String getMessage()
    {
        return message;
    }
}
