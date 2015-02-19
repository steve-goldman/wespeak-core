package com.wespeak.core.io.textdump;

import com.wespeak.core.Vote;
import com.wespeak.core.CommandHandler;

import java.io.*;
import java.util.regex.Pattern;

public class TextDumpCommandReader extends TextDumpReader
{
    private final CommandHandler commandHandler;

    public TextDumpCommandReader(final File file, final CommandHandler commandHandler) throws FileNotFoundException
    {
        super(file);
        this.commandHandler = commandHandler;
    }

    @Override
    protected boolean parseNext()
    {
        try
        {
            final String[] tokens = line.split(Pattern.quote(TextDumpConstants.Separator));

            // check for pulse
            if (TextDumpConstants.CommandTypes.Pulse.equals(tokens[1]))
            {
                handlePulse(tokens);
                return true;
            }

            // check for heartbeat
            if (TextDumpConstants.CommandTypes.Heartbeat.equals(tokens[1]))
            {
                handleHeartbeat(tokens);
                return true;
            }

            // check for leave
            if (TextDumpConstants.CommandTypes.Leave.equals(tokens[1]))
            {
                handleLeave(tokens);
                return true;
            }

            // check for submit
            if (TextDumpConstants.CommandTypes.Submit.equals(tokens[1]))
            {
                handleSubmit(tokens);
                return true;
            }

            // check for support
            if (TextDumpConstants.CommandTypes.Support.equals(tokens[1]))
            {
                handleSupport(tokens);
                return true;
            }

            // check for vote
            if (TextDumpConstants.CommandTypes.Vote.equals(tokens[1]))
            {
                handleVote(tokens);
                return true;
            }

            return false;
        }
        catch (final Exception e)
        {
            throw new Error(e);
        }
    }

    private void handlePulse(final String[] tokens) throws Exception
    {
        final long   now         = TextDumpConstants.stringToTime(tokens[0]);

        commandHandler.pulse(now);
    }

    private void handleHeartbeat(final String[] tokens) throws Exception
    {
        final long   now         = TextDumpConstants.stringToTime(tokens[0]);
        final String userId      = tokens[2];

        commandHandler.heartbeat(now, userId);
    }

    private void handleLeave(final String[] tokens) throws Exception
    {
        final long   now         = TextDumpConstants.stringToTime(tokens[0]);
        final String userId      = tokens[2];

        commandHandler.leave(now, userId);
    }

    private void handleSubmit(final String[] tokens) throws Exception
    {
        final long   now         = TextDumpConstants.stringToTime(tokens[0]);
        final String userId      = tokens[2];
        final int    textLength  = Integer.parseInt(tokens[3]);

        // read the text and the newline that follows it
        final char[] text = new char[textLength];
        reader.read(text);
        reader.skip(1);

        commandHandler.submit(now, userId, new String(text));
    }

    private void handleSupport(final String[] tokens) throws Exception
    {
        final long   now         = TextDumpConstants.stringToTime(tokens[0]);
        final String userId      = tokens[2];
        final String statementId = tokens[3];

        commandHandler.support(now, userId, statementId);
    }

    private void handleVote(final String[] tokens) throws Exception
    {
        final long   now         = TextDumpConstants.stringToTime(tokens[0]);
        final String userId      = tokens[2];
        final String statementId = tokens[3];
        final Vote   vote        = Vote.valueOf(tokens[4]);

        commandHandler.vote(now, userId, statementId, vote);
    }
}
