package com.wespeak.core.io.textdump;

import com.wespeak.core.EventHandler;
import com.wespeak.core.Vote;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;

public class TextDumpEventReader extends TextDumpReader
{
    private final EventHandler eventHandler;

    public TextDumpEventReader(final File file, final EventHandler eventHandler) throws FileNotFoundException
    {
        super(file);
        this.eventHandler = eventHandler;
    }

    @Override
    protected boolean parseNext()
    {
        try
        {
            final String[] tokens = line.split(Pattern.quote(TextDumpConstants.Separator));

            // check for heartbeat
            if (TextDumpConstants.EventTypes.Heartbeat.equals(tokens[0]))
            {
                handleHeartbeat(tokens);
                return true;
            }

            // check for submit
            if (TextDumpConstants.EventTypes.Submit.equals(tokens[0]))
            {
                handleSubmit(tokens);
                return true;
            }

            // check for support
            if (TextDumpConstants.EventTypes.Support.equals(tokens[0]))
            {
                handleSupport(tokens);
                return true;
            }

            // check for vote
            if (TextDumpConstants.EventTypes.Vote.equals(tokens[0]))
            {
                handleVote(tokens);
                return true;
            }

            // check for timeout user
            if (TextDumpConstants.EventTypes.TimeoutUser.equals(tokens[0]))
            {
                handleTimeoutUser(tokens);
                return true;
            }

            // check for timeout statement
            if (TextDumpConstants.EventTypes.TimeoutStatement.equals(tokens[0]))
            {
                handleTimeoutStatement(tokens);
                return true;
            }

            // check for begin vote
            if (TextDumpConstants.EventTypes.BeginVote.equals(tokens[0]))
            {
                handleBeginVote(tokens);
                return true;
            }

            // check for end vote accepted
            if (TextDumpConstants.EventTypes.EndVoteAccepted.equals(tokens[0]))
            {
                handleEndVoteAccepted(tokens);
                return true;
            }

            // check for end vote rejected
            if (TextDumpConstants.EventTypes.EndVoteRejected.equals(tokens[0]))
            {
                handleEndVoteRejected(tokens);
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

        eventHandler.heartbeat(now, userId, userActiveUntil);
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

        eventHandler.submit(now, userId, statementId, new String(text), statementActiveUntil, numEligibleSupporters, propSupportNeeded, userActiveUntil);
    }

    private void handleSupport(final String[] tokens) throws Exception
    {
        final long   now             = TextDumpConstants.stringToTime(tokens[1]);
        final String userId          = tokens[2];
        final String statementId     = tokens[3];
        final long   userActiveUntil = TextDumpConstants.stringToTime(tokens[4]);

        eventHandler.support(now, userId, statementId, userActiveUntil);
    }

    private void handleVote(final String[] tokens) throws Exception
    {
        final long   now             = TextDumpConstants.stringToTime(tokens[1]);
        final String userId          = tokens[2];
        final String statementId     = tokens[3];
        final Vote vote            = Vote.valueOf(tokens[4]);
        final long   userActiveUntil = TextDumpConstants.stringToTime(tokens[5]);

        eventHandler.vote(now, userId, statementId, vote, userActiveUntil);
    }

    private void handleTimeoutUser(final String[] tokens) throws Exception
    {
        final long   now    = TextDumpConstants.stringToTime(tokens[1]);
        final String userId = tokens[2];

        eventHandler.timeoutUser(now, userId);
    }

    private void handleTimeoutStatement(final String[] tokens) throws Exception
    {
        final long   now         = TextDumpConstants.stringToTime(tokens[1]);
        final String statementId = tokens[2];

        eventHandler.timeoutStatement(now, statementId);
    }

    private void handleBeginVote(final String[] tokens) throws Exception
    {
        final long   now               = TextDumpConstants.stringToTime(tokens[1]);
        final String statementId       = tokens[2];
        final long   until             = TextDumpConstants.stringToTime(tokens[3]);
        final int    numEligibleVoters = Integer.parseInt(tokens[4]);
        final int    propVotesNeeded   = Integer.parseInt(tokens[5]);
        final int    propYesesNeeded   = Integer.parseInt(tokens[6]);

        eventHandler.beginVote(now, statementId, until, numEligibleVoters, propVotesNeeded, propYesesNeeded);
    }

    private void handleEndVoteAccepted(final String[] tokens) throws Exception
    {
        final long   now         = TextDumpConstants.stringToTime(tokens[1]);
        final String statementId = tokens[2];

        eventHandler.endVoteAccepted(now, statementId);
    }

    private void handleEndVoteRejected(final String[] tokens) throws Exception
    {
        final long   now         = TextDumpConstants.stringToTime(tokens[1]);
        final String statementId = tokens[2];

        eventHandler.endVoteRejected(now, statementId);
    }


}
