package com.wespeak.core.events.io.textdump;

import com.wespeak.core.commands.io.textdump.TextDumpCommandReader;
import com.wespeak.core.events.EventHandler;
import com.wespeak.core.serialization.textdump.TextDumpConstants;

import java.io.File;
import java.io.FileNotFoundException;

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
            // check for timeout user
            if (line.startsWith(TextDumpConstants.EventTypes.TimeoutUser))
            {
                handleTimeoutUser(line);
                return true;
            }

            // check for timeout statement
            if (line.startsWith(TextDumpConstants.EventTypes.TimeoutStatement))
            {
                handleTimeoutStatement(line);
                return true;
            }

            // check for begin vote
            if (line.startsWith(TextDumpConstants.EventTypes.BeginVote))
            {
                handleBeginVote(line);
                return true;
            }

            // check for end vote accepted
            if (line.startsWith(TextDumpConstants.EventTypes.EndVoteAccepted))
            {
                handleEndVoteAccepted(line);
                return true;
            }

            // check for end vote rejected
            if (line.startsWith(TextDumpConstants.EventTypes.EndVoteRejected))
            {
                handleEndVoteRejected(line);
                return true;
            }

            return super.parseNext();
        }
        catch (final Exception e)
        {
            throw new Error(e);
        }
    }

    private void handleTimeoutUser(final String line) throws Exception
    {
        final String[] tokens = line.split(TextDumpConstants.Separator);

        final String userId = tokens[1];

        eventHandler.timeoutUser(0, userId);
    }

    private void handleTimeoutStatement(final String line) throws Exception
    {
        final String[] tokens = line.split(TextDumpConstants.Separator);

        final String statementId = tokens[1];

        eventHandler.timeoutStatement(0, statementId);
    }

    private void handleBeginVote(final String line) throws Exception
    {
        final String[] tokens = line.split(TextDumpConstants.Separator);

        final long   now               = TextDumpConstants.stringToTime(tokens[1]);
        final String statementId       = tokens[2];
        final long   until             = TextDumpConstants.stringToTime(tokens[3]);
        final int    numEligibleVoters = Integer.parseInt(tokens[4]);
        final int    propVotesNeeded   = Integer.parseInt(tokens[5]);
        final int    propYesesNeeded   = Integer.parseInt(tokens[6]);

        eventHandler.beginVote(now, statementId, until, numEligibleVoters, propVotesNeeded, propYesesNeeded);
    }

    private void handleEndVoteAccepted(final String line) throws Exception
    {
        final String[] tokens = line.split(TextDumpConstants.Separator);

        final String statementId = tokens[1];

        eventHandler.endVoteAccepted(0, statementId);
    }

    private void handleEndVoteRejected(final String line) throws Exception
    {
        final String[] tokens = line.split(TextDumpConstants.Separator);

        final String statementId = tokens[1];

        eventHandler.endVoteRejected(0, statementId);
    }


}
