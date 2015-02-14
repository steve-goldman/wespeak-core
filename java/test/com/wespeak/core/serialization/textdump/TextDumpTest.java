package com.wespeak.core.serialization.textdump;

import com.wespeak.core.events.io.textdump.TextDumpEventReader;
import com.wespeak.core.events.io.textdump.TextDumpEventWriter;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class TextDumpTest
{
    private boolean cleanDiff(final InputStream streamA, final InputStream streamB) throws IOException
    {
        int a = streamA.read();
        int b = streamB.read();

        int count = 0;
        while (a != -1 && b != -1 && a == b)
        {
            count++;
            a = streamA.read();
            b = streamB.read();
        }

        if (a == -1 && b == -1)
        {
            return true;
        }

        System.out.println("problem at byte " + count);

        return false;
    }

    private void runFile(final File inputFile) throws IOException
    {
        final File outputFile = File.createTempFile("textDumpTest", null, new File("/tmp"));

        System.out.println("input  file: " + inputFile.getPath());
        System.out.println("output file: " + outputFile.getPath());

        final TextDumpEventWriter writer = new TextDumpEventWriter(outputFile);
        final TextDumpEventReader reader = new TextDumpEventReader(inputFile, writer);

        while (reader.hasNext())
        {
            reader.readNext();
        }
        writer.flush();

        Assert.assertTrue(cleanDiff(new FileInputStream(inputFile), new FileInputStream(outputFile)));
    }

    private void runTest(final String test) throws IOException
    {
        runFile(new File(getClass().getResource(test + ".txt").getFile()));
    }

    @Test
    public void testHeartbeat() throws IOException
    {
        runTest("heartbeat");
    }

    @Test
    public void testSubmit() throws IOException
    {
        runTest("submit");
    }

    @Test
    public void testSupport() throws IOException
    {
        runTest("support");
    }

    @Test
    public void testVote() throws IOException
    {
        runTest("vote");
    }

    @Test
    public void testTimeoutUser() throws IOException
    {
        runTest("timeout_user");
    }

    @Test
    public void testTimeoutStatement() throws IOException
    {
        runTest("timeout_statement");
    }

    @Test
    public void testBeginVote() throws IOException
    {
        runTest("begin_vote");
    }

    @Test
    public void testEndVoteAccepted() throws IOException
    {
        runTest("end_vote_accepted");
    }

    @Test
    public void testEndVoteRejected() throws IOException
    {
        runTest("end_vote_rejected");
    }
}
