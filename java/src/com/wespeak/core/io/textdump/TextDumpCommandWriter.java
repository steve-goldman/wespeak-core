package com.wespeak.core.io.textdump;

import com.wespeak.core.Vote;
import com.wespeak.core.CommandHandler;

import java.io.File;
import java.io.IOException;

public class TextDumpCommandWriter extends TextDumpWriter implements CommandHandler
{
    public TextDumpCommandWriter(final File file) throws IOException
    {
        super(file);
    }

    @Override
    public void heartbeat(final long now, final String userId, final long userActiveUntil)
    {
        writeLine(TextDumpConstants.CommandTypes.Heartbeat,
                TextDumpConstants.timeToString(now),
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
        writeLine(TextDumpConstants.CommandTypes.Submit,
                TextDumpConstants.timeToString(now),
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
        writeLine(TextDumpConstants.CommandTypes.Support,
                TextDumpConstants.timeToString(now),
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
        writeLine(TextDumpConstants.CommandTypes.Vote,
                TextDumpConstants.timeToString(now),
                userId,
                statementId,
                vote.name(),
                TextDumpConstants.timeToString(userActiveUntil));
    }
}
