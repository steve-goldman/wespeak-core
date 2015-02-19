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
    public void pulse(final long now)
    {
        writeLine(TextDumpConstants.timeToString(now),
                TextDumpConstants.CommandTypes.Pulse);
    }

    @Override
    public void heartbeat(final long now, final String userId)
    {
        writeLine(TextDumpConstants.timeToString(now),
                TextDumpConstants.CommandTypes.Heartbeat,
                userId);
    }

    @Override
    public void leave(final long now, final String userId)
    {
        writeLine(TextDumpConstants.timeToString(now),
                TextDumpConstants.CommandTypes.Leave,
                userId);
    }

    @Override
    public void submit(final long   now,
                       final String userId,
                       final String text)
    {
        writeLine(TextDumpConstants.timeToString(now),
                TextDumpConstants.CommandTypes.Submit,
                userId,
                "" + text.length());

        writeStatement(text);
    }

    @Override
    public void support(final long   now,
                        final String userId,
                        final String statementId)
    {
        writeLine(TextDumpConstants.timeToString(now),
                TextDumpConstants.CommandTypes.Support,
                userId,
                statementId);
    }

    @Override
    public void vote(final long   now,
                     final String userId,
                     final String statementId,
                     final Vote   vote)
    {
        writeLine(TextDumpConstants.timeToString(now),
                TextDumpConstants.CommandTypes.Vote,
                userId,
                statementId,
                vote.name());
    }
}
