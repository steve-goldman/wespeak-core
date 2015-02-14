package com.wespeak.core.io.textdump;

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

    private void runCommandFile(final File inputFile) throws IOException
    {
        final File outputFile = File.createTempFile("textDumpCommandTest", null, new File("/tmp"));

        System.out.println("input  file: " + inputFile.getPath());
        System.out.println("output file: " + outputFile.getPath());

        final TextDumpCommandWriter writer = new TextDumpCommandWriter(outputFile);
        final TextDumpCommandReader reader = new TextDumpCommandReader(inputFile, writer);

        while (reader.hasNext())
        {
            reader.readNext();
        }
        writer.flush();

        Assert.assertTrue(cleanDiff(new FileInputStream(inputFile), new FileInputStream(outputFile)));
    }

    private void runEventFile(final File inputFile) throws IOException
    {
        final File outputFile = File.createTempFile("textDumpEventTest", null, new File("/tmp"));

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

    private void runCommandTest(final String test) throws IOException
    {
        runCommandFile(new File(getClass().getResource(test + "_command.txt").getFile()));
    }

    private void runEventTest(final String test) throws IOException
    {
        runEventFile(new File(getClass().getResource(test + "_event.txt").getFile()));
    }

    @Test
    public void testPulseCommand() throws IOException
    {
        runCommandTest("pulse");
    }

    @Test
    public void testHeartbeatCommand() throws IOException
    {
        runCommandTest("heartbeat");
    }

    @Test
    public void testLeaveCommand() throws IOException
    {
        runCommandTest("leave");
    }

    @Test
    public void testSubmitCommand() throws IOException
    {
        runCommandTest("submit");
    }

    @Test
    public void testSupportCommand() throws IOException
    {
        runCommandTest("support");
    }

    @Test
    public void testVoteCommand() throws IOException
    {
        runCommandTest("vote");
    }

    @Test
    public void testHeartbeatEvent() throws IOException
    {
        runEventTest("heartbeat");
    }

    @Test
    public void testSubmitEvent() throws IOException
    {
        runEventTest("submit");
    }

    @Test
    public void testSupportEvent() throws IOException
    {
        runEventTest("support");
    }

    @Test
    public void testVoteEvent() throws IOException
    {
        runEventTest("vote");
    }

    @Test
    public void testTimeoutUserEvent() throws IOException
    {
        runEventTest("timeout_user");
    }

    @Test
    public void testTimeoutStatementEvent() throws IOException
    {
        runEventTest("timeout_statement");
    }

    @Test
    public void testBeginVoteEvent() throws IOException
    {
        runEventTest("begin_vote");
    }

    @Test
    public void testEndVoteAcceptedEvent() throws IOException
    {
        runEventTest("end_vote_accepted");
    }

    @Test
    public void testEndVoteRejectedEvent() throws IOException
    {
        runEventTest("end_vote_rejected");
    }
}
