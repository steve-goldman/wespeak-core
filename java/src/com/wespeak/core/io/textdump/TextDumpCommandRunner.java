package com.wespeak.core.io.textdump;

import com.wespeak.core.CommandResponse;
import com.wespeak.core.datamanager.DataManagerFactory;
import com.wespeak.core.engine.Engine;
import com.wespeak.core.engine.GroupParameters;

import java.io.File;
import java.io.IOException;

public class TextDumpCommandRunner
{
    private static void usage()
    {
        System.out.println("Usage: --input <input-file> --output <output-file>");
    }

    public static void main(final String[] args) throws IOException
    {
        String inputFile  = null;
        String outputFile = null;

        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-h"))
            {
                usage();
                System.exit(0);
            }

            if (args[i].equals("--input"))
            {
                inputFile = args[++i];
            }

            if (args[i].equals("--output"))
            {
                outputFile = args[++i];
            }
        }

        if (inputFile == null)
        {
            System.out.println("must specify input");
            usage();
            System.exit(1);
        }

        if (outputFile == null)
        {
            System.out.println("must specify output");
            usage();
            System.exit(1);
        }

        final TextDumpEventWriter   writer = new TextDumpEventWriter(new File(outputFile));
        final Engine                engine = new Engine(DataManagerFactory.getInstance(), new GroupParameters(), writer);
        final TextDumpCommandReader reader = new TextDumpCommandReader(new File(inputFile), engine);

        int line = 0;
        while (reader.hasNext())
        {
            reader.readNext();
            line++;

            final CommandResponse response = engine.getLastResponse();
            if (CommandResponse.Code.OK != response.getCode())
            {
                System.err.println("Unexpected response:" + response.getCode().name() + " at command:" + line + " message:" + response.getMessage());
            }
        }

        writer.flush();
    }
}
