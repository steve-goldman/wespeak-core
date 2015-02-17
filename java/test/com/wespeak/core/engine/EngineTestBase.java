package com.wespeak.core.engine;

import com.wespeak.core.EventPublisher;
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
    private static final boolean   DELETE_TMP_FILE_ON_EXIT = false;

    protected static final long    T0          = 398781000;
    protected static final long    OneMinute   = 1000 * 60;
    protected static final long    OneHour     = 60 * OneMinute;
    protected static final long    OneDay      = 24 * OneHour;

    protected static final String  Steve  = "steve";

    protected DataManager          dataManager;
    protected File                 outputFile;
    protected TextDumpEventWriter  writer;
    protected GroupParameters      groupParameters;

    protected Engine               engine;

    @Before
    public void setup() throws IOException
    {
        dataManager     = DataManagerFactory.getInstance();

        outputFile      = File.createTempFile("engineTest", null, new File("/tmp"));
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
        Assert.assertEquals(2 * OneDay, groupParameters.getSubmissionTTL());
        Assert.assertEquals(5 * OneDay, groupParameters.getVoteTTL());
        Assert.assertEquals(37,         groupParameters.getSupportThreshold());
        Assert.assertEquals(53,         groupParameters.getVoteThreshold());
        Assert.assertEquals(71,         groupParameters.getYesThreshold());
    }

    protected void primeParameters(final long userTTL,
                                   final long submissionTTL,
                                   final long voteTTL,
                                   final int  supportThreshold,
                                   final int  voteThreshold,
                                   final int  yesThreshold)
    {
        final String setup = "setup";

        engine.heartbeat(0, setup);

        for (final String text : new String[] {
                SpecialStatements.makeUserTTL(userTTL),
                SpecialStatements.makeSubmissionTTL(submissionTTL),
                SpecialStatements.makeVoteTTL(voteTTL),
                SpecialStatements.makeSupporthThreshold(supportThreshold),
                SpecialStatements.makeVoteThreshold(voteThreshold),
                SpecialStatements.makeYesThreshold(yesThreshold) })
        {
            primeParameter(setup, text);
        }

        engine.pulse(1);

        Assert.assertFalse(dataManager.hasActiveUsers());
        Assert.assertFalse(dataManager.hasActiveStatements());
        Assert.assertFalse(dataManager.getVotingStatementIds().hasNext());
    }

    private void primeParameter(final String userId, final String text)
    {
        System.out.println("priming with: " + text);

        final String statementId = engine.submit(0, userId, text);
        Assert.assertTrue(dataManager.isStatementExists(statementId));
        Assert.assertEquals(StatementState.ACTIVE, dataManager.getState(statementId));

        engine.support(0, userId, statementId);
        Assert.assertTrue(dataManager.isSupported(userId, statementId));
        Assert.assertEquals(StatementState.VOTING, dataManager.getState(statementId));

        engine.vote(0, userId, statementId, Vote.YES);
        Assert.assertEquals(Vote.YES, dataManager.getVote(userId, statementId));
    }

    protected class DebugEventPublisher implements EventPublisher
    {
        private final EventPublisher eventPublisher;

        private boolean didHeartbeat;
        private boolean didSubmit;
        private boolean didSupport;
        private boolean didVote;
        private boolean didTimeoutUser;
        private boolean didTimeoutStatement;
        private boolean didBeginVote;
        private boolean didEndVoteAccepted;
        private boolean didEndVoteRejected;

        DebugEventPublisher(final EventPublisher eventPublisher)
        {
            this.eventPublisher = eventPublisher;
        }



        @Override
        public void heartbeat(final long now, final String userId, final long userActiveUntil)
        {

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

        }

        @Override
        public void support(final long   now,
                            final String userId,
                            final String statementId,
                            final long   userActiveUntil)
        {

        }

        @Override
        public void vote(final long   now,
                         final String userId,
                         final String statementId,
                         final Vote   vote,
                         final long   userActiveUntil)
        {

        }

        @Override
        public void timeoutUser(final long now, final String userId)
        {

        }

        @Override
        public void timeoutStatement(final long now, final String statementId)
        {

        }

        @Override
        public void beginVote(final long   now,
                              final String statementId,
                              final long   until,
                              final int    numEligibleVoters,
                              final int    propVotesNeeded,
                              final int    propYesesNeeded)
        {

        }

        @Override
        public void endVoteAccepted(final long now, final String statementId)
        {

        }

        @Override
        public void endVoteRejected(final long now, final String statementId)
        {

        }
    }
}