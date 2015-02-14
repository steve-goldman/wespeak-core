package com.wespeak.core.serialization.textdump;

import com.wespeak.core.EventHandler;

import java.io.File;
import java.io.IOException;

public class TextDumpEventWriter extends TextDumpCommandWriter implements EventHandler
{
    public TextDumpEventWriter(final File file) throws IOException
    {
        super(file);
    }

    @Override
    public void timeoutUser(final long now, final String userId)
    {
        writeLine(TextDumpConstants.EventTypes.TimeoutUser,
                TextDumpConstants.timeToString(now),
                userId);
    }

    @Override
    public void timeoutStatement(final long now, final String statementId)
    {
        writeLine(TextDumpConstants.EventTypes.TimeoutStatement,
                TextDumpConstants.timeToString(now),
                statementId);
    }

    @Override
    public void beginVote(final long   now,
                          final String statementId,
                          final long   until,
                          final int    numEligibleVoters,
                          final int    propVotesNeeded,
                          final int    propYesesNeeded)
    {
        writeLine(TextDumpConstants.EventTypes.BeginVote,
                TextDumpConstants.timeToString(now),
                statementId,
                TextDumpConstants.timeToString(until),
                "" + numEligibleVoters,
                "" + propVotesNeeded,
                "" + propYesesNeeded);
    }

    @Override
    public void endVoteAccepted(final long now, final String statementId)
    {
        writeLine(TextDumpConstants.EventTypes.EndVoteAccepted,
                TextDumpConstants.timeToString(now),
                statementId);
    }

    @Override
    public void endVoteRejected(long now, String statementId)
    {
        writeLine(TextDumpConstants.EventTypes.EndVoteRejected,
                TextDumpConstants.timeToString(now),
                statementId);
    }
}
