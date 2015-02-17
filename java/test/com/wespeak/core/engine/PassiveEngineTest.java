package com.wespeak.core.engine;

import com.wespeak.core.SpecialStatements;
import com.wespeak.core.Vote;
import com.wespeak.core.datamanager.DataManager;
import com.wespeak.core.datamanager.DataManagerFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PassiveEngineTest
{
    private static final long    T0          = 398781000;
    private static final long    OneMinute   = 1000 * 60;
    private static final long    OneHour     = 60 * OneMinute;
    private static final long    OneDay      = 24 * OneHour;

    private static final String  Steve  = "steve";

    private DataManager          dataManager;
    private GroupParameters      groupParameters;

    private PassiveEngine        passiveEngine;

    @Before
    public void setup()
    {
        dataManager     = DataManagerFactory.getInstance();
        groupParameters = new GroupParameters();

        passiveEngine   = new PassiveEngine(dataManager, groupParameters);
    }

    @Test
    public void testParameterChanges()
    {
        passiveEngine.heartbeat(T0, Steve, T0 + groupParameters.getUserTTL());

        testParameterChange("userTTL",          SpecialStatements.makeUserTTL(OneDay));
        testParameterChange("submissionTTL",    SpecialStatements.makeSubmissionTTL(2 * OneDay));
        testParameterChange("voteTTL",          SpecialStatements.makeVoteTTL(5 * OneDay));
        testParameterChange("supportThreshold", SpecialStatements.makeSupporthThreshold(37));
        testParameterChange("voteThreshold",    SpecialStatements.makeVoteThreshold(53));
        testParameterChange("yesThreshold",     SpecialStatements.makeYesThreshold(71));

        Assert.assertEquals(OneDay,     groupParameters.getUserTTL());
        Assert.assertEquals(2 * OneDay, groupParameters.getSubmissionTTL());
        Assert.assertEquals(5 * OneDay, groupParameters.getVoteTTL());
        Assert.assertEquals(37,         groupParameters.getSupportThreshold());
        Assert.assertEquals(53,         groupParameters.getVoteThreshold());
        Assert.assertEquals(71,         groupParameters.getYesThreshold());
    }

    private void testParameterChange(final String statementId, final String text)
    {
        passiveEngine.submit(
                T0,
                Steve,
                statementId,
                text,
                T0 + groupParameters.getSubmissionTTL(),
                1,
                groupParameters.getSupportThreshold(),
                T0 + groupParameters.getUserTTL());

        passiveEngine.support(T0, Steve, statementId, T0 + groupParameters.getUserTTL());

        passiveEngine.beginVote(
                T0,
                statementId,
                T0 + groupParameters.getVoteTTL(),
                1,
                groupParameters.getVoteThreshold(),
                groupParameters.getYesThreshold());

        passiveEngine.vote(T0, Steve, statementId, Vote.YES, T0 + groupParameters.getUserTTL());

        passiveEngine.endVoteAccepted(T0, statementId);
    }
}