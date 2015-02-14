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

        cacheNextLine();
    }

    public boolean hasNext()
    {
        return line != null;
    }

    public void readNext()
    {
        if (!parseNext())
        {
            throw new Error("Unexpected line: " + line);
        }

        cacheNextLine();
    }

    protected boolean parseNext()
    {
        try
        {
            final String[] tokens = line.split(Pattern.quote(TextDumpConstants.Separator));

            // check for heartbeat
            if (TextDumpConstants.CommandTypes.Heartbeat.equals(tokens[0]))
            {
                handleHeartbeat(tokens);
                return true;
            }

            // check for submit
            if (TextDumpConstants.CommandTypes.Submit.equals(tokens[0]))
            {
                handleSubmit(tokens);
                return true;
            }

            // check for support
            if (TextDumpConstants.CommandTypes.Support.equals(tokens[0]))
            {
                handleSupport(tokens);
                return true;
            }

            // check for vote
            if (TextDumpConstants.CommandTypes.Vote.equals(tokens[0]))
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

    private void handleHeartbeat(final String[] tokens) throws Exception
    {
        final long   now             = TextDumpConstants.stringToTime(tokens[1]);
        final String userId          = tokens[2];
        final long   userActiveUntil = TextDumpConstants.stringToTime(tokens[3]);

        commandHandler.heartbeat(now, userId, userActiveUntil);
    }

    private void handleSubmit(final String[] tokens) throws Exception
    {
        final long   now                   = TextDumpConstants.stringToTime(tokens[1]);
        final String userId                = tokens[2];
        final String statementId           = tokens[3];
        final long   statementActiveUntil  = TextDumpConstants.stringToTime(tokens[4]);
        final int    numEligibleSupporters = Integer.parseInt(tokens[5]);
        final int    propSupportNeeded     = Integer.parseInt(tokens[6]);
        final long   userActiveUntil       = TextDumpConstants.stringToTime(tokens[7]);
        final int    textLength            = Integer.parseInt(tokens[8]);

        // read the text and the newline that follows it
        final char[] text = new char[textLength];
        reader.read(text);
        reader.skip(1);

        commandHandler.submit(now, userId, statementId, new String(text), statementActiveUntil, numEligibleSupporters, propSupportNeeded, userActiveUntil);
    }

    private void handleSupport(final String[] tokens) throws Exception
    {
        final long   now             = TextDumpConstants.stringToTime(tokens[1]);
        final String userId          = tokens[2];
        final String statementId     = tokens[3];
        final long   userActiveUntil = TextDumpConstants.stringToTime(tokens[4]);

        commandHandler.support(now, userId, statementId, userActiveUntil);
    }

    private void handleVote(final String[] tokens) throws Exception
    {
        final long   now             = TextDumpConstants.stringToTime(tokens[1]);
        final String userId          = tokens[2];
        final String statementId     = tokens[3];
        final Vote vote            = Vote.valueOf(tokens[4]);
        final long   userActiveUntil = TextDumpConstants.stringToTime(tokens[5]);

        commandHandler.vote(now, userId, statementId, vote, userActiveUntil);
    }
}
