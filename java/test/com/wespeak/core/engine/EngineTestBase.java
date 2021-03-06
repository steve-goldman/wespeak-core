package com.wespeak.core.engine;

import com.wespeak.core.CommandResponse;
import com.wespeak.core.SpecialStatements;
import com.wespeak.core.StatementState;
import com.wespeak.core.Vote;
import com.wespeak.core.datamanager.DataManager;
import com.wespeak.core.datamanager.DataManagerFactory;
import com.wespeak.core.io.textdump.TextDumpEventWriter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class EngineTestBase
{
    private static final   boolean DELETE_TMP_FILE_ON_EXIT = true;

    protected static final long    T0          = 398781000;
    protected static final long    OneMinute   = 1000 * 60;
    protected static final long    OneHour     = 60 * OneMinute;
    protected static final long    OneDay      = 24 * OneHour;

    protected static final String  Steve  = "steve";

    protected DataManager          dataManager;
    private TextDumpEventWriter    writer;
    protected GroupParameters      groupParameters;

    protected Engine               engine;

    @Before
    public void setup() throws IOException
    {
        dataManager     = DataManagerFactory.getInstance();

        final File outputFile = File.createTempFile("engineTest", null, new File("/tmp"));
        if (DELETE_TMP_FILE_ON_EXIT)
        {
            outputFile.deleteOnExit();
        }
        else
        {
            System.out.println("output file: " + outputFile.getPath());
        }

        writer          = new TextDumpEventWriter(outputFile);
        groupParameters = new GroupParameters();

        engine          = new Engine(dataManager, groupParameters, writer);
    }

    @After
    public void shutdown()
    {
        try
        {
            writer.flush();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testPrimeParameters()
    {
        primeParameters(
                OneDay,
                2 * OneDay,
                5 * OneDay,
                37,
                53,
                71);

        Assert.assertEquals(OneDay,     groupParameters.getUserTTL());
        Assert.assertEquals(2 * OneDay, groupParameters.getStatementTTL());
        Assert.assertEquals(5 * OneDay, groupParameters.getVoteTTL());
        Assert.assertEquals(37,         groupParameters.getSupportThreshold());
        Assert.assertEquals(53,         groupParameters.getVoteThreshold());
        Assert.assertEquals(71,         groupParameters.getYesThreshold());
    }

    protected void primeParameters(final long userTTL,
                                   final long statementTTL,
                                   final long voteTTL,
                                   final int  supportThreshold,
                                   final int  voteThreshold,
                                   final int  yesThreshold)
    {
        final String setupUser = "setup";

        engine.heartbeat(0, setupUser);

        for (final String text : new String[] {
                SpecialStatements.makeUserTTL(userTTL),
                SpecialStatements.makeStatementTTL(statementTTL),
                SpecialStatements.makeVoteTTL(voteTTL),
                SpecialStatements.makeSupporthThreshold(supportThreshold),
                SpecialStatements.makeVoteThreshold(voteThreshold),
                SpecialStatements.makeYesThreshold(yesThreshold) })
        {
            primeParameter(setupUser, text);
        }

        engine.pulse(1);

        Assert.assertFalse(dataManager.hasActiveUsers());
        Assert.assertFalse(dataManager.hasActiveStatements());
        Assert.assertFalse(dataManager.getVotingStatementIds().hasNext());
    }

    private void primeParameter(final String userId, final String text)
    {
        System.out.println("priming with: " + text);

        engine.submit(0, userId, text);
        Assert.assertEquals(CommandResponse.Code.OK, engine.getLastResponse().getCode());
        final String statementId = engine.getLastStatementId();
        Assert.assertTrue(dataManager.isStatementExists(statementId));
        Assert.assertEquals(StatementState.ACTIVE, dataManager.getState(statementId));

        engine.support(0, userId, statementId);
        Assert.assertTrue(dataManager.isSupported(userId, statementId));
        Assert.assertEquals(StatementState.VOTING, dataManager.getState(statementId));

        engine.vote(0, userId, statementId, Vote.YES);
        Assert.assertEquals(Vote.YES, dataManager.getVote(userId, statementId));
    }

    protected void heartbeat(final long now, final String userId)
    {
        // no pre-conditions

        engine.heartbeat(now, userId);
    }
}