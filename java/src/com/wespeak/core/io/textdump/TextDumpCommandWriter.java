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
        writeLine(TextDumpConstants.CommandTypes.Pulse,
                TextDumpConstants.timeToString(now));
    }

    @Override
    public void heartbeat(final long now, final String userId)
    {
        writeLine(TextDumpConstants.CommandTypes.Heartbeat,
                TextDumpConstants.timeToString(now),
                userId);
    }

    @Override
    public void leave(final long now, final String userId)
    {
        writeLine(TextDumpConstants.CommandTypes.Leave,
                TextDumpConstants.timeToString(now),
                userId);
    }

    @Override
    public String submit(final long   now,
                         final String userId,
                         final String text)
    {
        writeLine(TextDumpConstants.CommandTypes.Submit,
                TextDumpConstants.timeToString(now),
                userId,
                "" + text.length());

        writeStatement(text);

        return null;
    }

    @Override
    public void support(final long   now,
                        final String userId,
                        final String statementId)
    {
        writeLine(TextDumpConstants.CommandTypes.Support,
                TextDumpConstants.timeToString(now),
                userId,
                statementId);
    }

    @Override
    public void vote(final long   now,
                     final String userId,
                     final String statementId,
                     final Vote   vote)
    {
        writeLine(TextDumpConstants.CommandTypes.Vote,
                TextDumpConstants.timeToString(now),
                userId,
                statementId,
                vote.name());
    }
}
