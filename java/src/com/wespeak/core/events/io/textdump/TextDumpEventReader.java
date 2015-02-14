package com.wespeak.core.events.io.textdump;

import com.wespeak.core.commands.io.textdump.TextDumpCommandReader;
import com.wespeak.core.events.EventHandler;
import com.wespeak.core.serialization.textdump.TextDumpConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;

public class TextDumpEventReader extends TextDumpCommandReader
{
    private final EventHandler eventHandler;

    public TextDumpEventReader(final File file, final EventHandler eventHandler) throws FileNotFoundException
    {
        super(file, eventHandler);
        this.eventHandler = eventHandler;
    }

    @Override
    protected boolean parseNext()
    {
        try
        {
            final String[] tokens = line.split(Pattern.quote(TextDumpConstants.Separator));

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

            return super.parseNext();
        }
        catch (final Exception e)
        {
            throw new Error(e);
        }
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
