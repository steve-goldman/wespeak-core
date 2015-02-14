package com.wespeak.core.io.textdump;

import java.io.*;

public class TextDumpReader
{
    protected final BufferedReader reader;
    protected String line;

    protected TextDumpReader(final File file) throws FileNotFoundException
    {
        this.reader = new BufferedReader(new FileReader(file));
    }

    protected void cacheNextLine()
    {
        try
        {
            line = reader.readLine();

            // check for comment
            while (line != null && (line.trim().isEmpty() || line.startsWith(TextDumpConstants.Comment)))
            {
                line = reader.readLine();
            }
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }
}
