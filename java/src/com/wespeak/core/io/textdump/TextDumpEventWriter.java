package com.wespeak.core.io.textdump;

import com.wespeak.core.EventPublisher;
import com.wespeak.core.Vote;

import java.io.File;
import java.io.IOException;

public class TextDumpEventWriter extends TextDumpWriter implements EventPublisher
{
    public TextDumpEventWriter(final File file) throws IOException
    {
        super(file);
    }

    @Override
    public void heartbeat(final long now, final String userId, final long userActiveUntil)
    {
        writeLine(TextDumpConstants.timeToString(now),
                TextDumpConstants.EventTypes.Heartbeat,
                userId,
                TextDumpConstants.timeToString(userActiveUntil));
    }

    @Override
    public void submit(final long   now,
                       final String userId,
                       final String statementId,
                       final String text,
                       final long   statementActiveUntil,
                       final int    numEligibleSupporters,
                       final int    propSupportNeeded,
                       final long   userActiveUntil)
    {
        writeLine(TextDumpConstants.timeToString(now),
                TextDumpConstants.EventTypes.Submit,
                userId,
                statementId,
                TextDumpConstants.timeToString(statementActiveUntil),
                "" + numEligibleSupporters,
                "" + propSupportNeeded,
                TextDumpConstants.timeToString(userActiveUntil),
                "" + text.length());

        writeStatement(text);

    }

    @Override
    public void support(final long   now,
                        final String userId,
                        final String statementId,
                        final long   userActiveUntil)
    {
        writeLine(TextDumpConstants.timeToString(now),
                TextDumpConstants.EventTypes.Support,
                userId,
                statementId,
                TextDumpConstants.timeToString(userActiveUntil));
    }

    @Override
    public void vote(final long   now,
                     final String userId,
                     final String statementId,
                     final Vote   vote,
                     final long   userActiveUntil)
    {
        writeLine(TextDumpConstants.timeToString(now),
                TextDumpConstants.EventTypes.Vote,
                userId,
                statementId,
                vote.name(),
                TextDumpConstants.timeToString(userActiveUntil));
    }

    @Override
    public void timeoutUser(final long now, final String userId)
    {
        writeLine(TextDumpConstants.timeToString(now),
                TextDumpConstants.EventTypes.TimeoutUser,
                userId);
    }

    @Override
    public void timeoutStatement(final long now, final String statementId)
    {
        writeLine(TextDumpConstants.timeToString(now),
                TextDumpConstants.EventTypes.TimeoutStatement,
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
        writeLine(TextDumpConstants.timeToString(now),
                TextDumpConstants.EventTypes.BeginVote,
                statementId,
                TextDumpConstants.timeToString(until),
                "" + numEligibleVoters,
                "" + propVotesNeeded,
                "" + propYesesNeeded);
    }

    @Override
    public void endVoteAccepted(final long now, final String statementId)
    {
        writeLine(TextDumpConstants.timeToString(now),
                TextDumpConstants.EventTypes.EndVoteAccepted,
                statementId);
    }

    @Override
    public void endVoteRejected(long now, String statementId)
    {
        writeLine(TextDumpConstants.timeToString(now),
                TextDumpConstants.EventTypes.EndVoteRejected,
                statementId);
    }
}
