package com.wespeak.core.engine;

import com.wespeak.core.EventHandler;
import com.wespeak.core.Vote;
import com.wespeak.core.datamanager.DataManager;

public class PassiveEngine extends EngineBase implements EventHandler
{
    public PassiveEngine(final DataManager dataManager, final GroupParameters groupParameters)
    {
        super(dataManager, groupParameters);
    }

    @Override
    public void heartbeat(final long now, final String userId, final long userActiveUntil)
    {
        dataManager.heartbeat(now, userId, now + groupParameters.getUserTTL());
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
        dataManager.submit(now, userId, statementId, text, statementActiveUntil, numEligibleSupporters, propSupportNeeded, userActiveUntil);
    }

    @Override
    public void support(final long   now,
                        final String userId,
                        final String statementId,
                        final long   userActiveUntil)
    {
        dataManager.support(now, userId, statementId, userActiveUntil);
    }

    @Override
    public void vote(final long   now,
                     final String userId,
                     final String statementId,
                     final Vote   vote,
                     final long   userActiveUntil)
    {
        dataManager.vote(now, userId, statementId, vote, userActiveUntil);
    }

    @Override
    public void timeoutUser(final long now, final String userId)
    {
        dataManager.timeoutUser(now, userId);
    }

    @Override
    public void timeoutStatement(final long now, final String statementId)
    {
        dataManager.timeoutStatement(now, statementId);
    }

    @Override
    public void beginVote(final long   now,
                          final String statementId,
                          final long   until,
                          final int    numEligibleVoters,
                          final int    propVotesNeeded,
                          final int    propYesesNeeded)
    {
        dataManager.beginVote(now, statementId, until, numEligibleVoters, propVotesNeeded, propYesesNeeded);
    }

    @Override
    public void endVoteAccepted(final long now, final String statementId)
    {
        dataManager.endVoteAccepted(now, statementId);
        updateGroupParameters(dataManager.getText(statementId));
    }

    @Override
    public void endVoteRejected(final long now, final String statementId)
    {
        dataManager.endVoteRejected(now, statementId);
    }
 }
