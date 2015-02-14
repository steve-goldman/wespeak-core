package com.wespeak.core.serialization.textdump;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TextDumpWriter
{
    private final BufferedWriter writer;

    protected TextDumpWriter(final File file) throws IOException
    {
        this.writer = new BufferedWriter(new FileWriter(file));
    }

    protected void writeLine(final String... tokens)
    {
        try
        {
            final StringBuilder sb = new StringBuilder();

            sb.append(tokens[0]);

            for (int i = 1; i < tokens.length; i++)
            {
                sb.append(TextDumpConstants.Separator).append(tokens[i]);
            }

            writer.write(sb.toString());

            writer.newLine();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    protected void writeStatement(final String statement)
    {
        try
        {
            writer.write(statement);

            writer.newLine();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    public void flush() throws IOException
    {
        writer.flush();
    }
}
