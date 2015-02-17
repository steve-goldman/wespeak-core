package com.wespeak.core.engine;

import com.wespeak.core.*;
import com.wespeak.core.datamanager.DataManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.regex.Matcher;

public class Engine extends EngineBase implements CommandProcessor
{
    private final EventPublisher  eventPublisher;

    private       CommandResponse lastResponse;

    private final MessageDigest   messageDigest;

    public Engine(final DataManager dataManager, final GroupParameters groupParameters, final EventPublisher eventPublisher)
    {
        super(dataManager, groupParameters);

        this.eventPublisher  = eventPublisher;

        try
        {
            this.messageDigest = MessageDigest.getInstance("MD5");
        }
        catch (final NoSuchAlgorithmException e)
        {
            throw new Error(e);
        }
    }

    @Override
    public CommandResponse getLastResponse()
    {
        return lastResponse;
    }

    @Override
    public void pulse(final long now)
    {
        // time out expired users
        while (dataManager.hasActiveUsers())
        {
            final String userId = dataManager.getOldestActiveUserId();
            if (dataManager.getUserTTL(now, userId) > 0)
            {
                break;
            }

            dataManager   .timeoutUser(now, userId);
            eventPublisher.timeoutUser(now, userId);
        }

        // time out expired statements
        while (dataManager.hasActiveStatements())
        {
            final String statementId = dataManager.getOldestActiveStatementId();
            if (dataManager.getSubmissionTTL(now, statementId) > 0)
            {
                break;
            }

            dataManager   .timeoutStatement(now, statementId);
            eventPublisher.timeoutStatement(now, statementId);
        }

        // end votes

        // TODO: this could be more efficient
        boolean voteEnded = true;
        while (voteEnded)
        {
            voteEnded = false;

            final Iterator<String> iter = dataManager.getVotingStatementIds();
            while (iter.hasNext())
            {
                final String statementId = iter.next();
                if (dataManager.getVoteTTL(now, statementId) <= 0)
                {
                    endVote(now, statementId);
                    voteEnded = true;
                    break;
                }
            }
        }
    }

    private void endVote(final long now, final String statementId)
    {
        final int numEligibleVoters = dataManager.getNumEligibleVoters(statementId);

        final int voteCount = dataManager.getVoteCount(statementId);

        if (100 * voteCount / numEligibleVoters >= groupParameters.getVoteThreshold())
        {
            if (100 * dataManager.getYesCount(statementId) / voteCount >= groupParameters.getYesThreshold())
            {
                dataManager   .endVoteAccepted(now, statementId);
                eventPublisher.endVoteAccepted(now, statementId);

                updateGroupParameters(dataManager.getText(statementId));
            }
            else
            {
                dataManager   .endVoteRejected(now, statementId);
                eventPublisher.endVoteRejected(now, statementId);
            }
        }
        else
        {
            dataManager   .endVoteRejected(now, statementId);
            eventPublisher.endVoteRejected(now, statementId);
        }
    }

    private boolean validParameterChange(final String text)
    {
        Matcher m;

        m = SpecialStatements.UserTTL.matcher(text);
        if (m.find())
        {
            // TODO: make the bounds a parameter
            final long userTTL = Long.parseLong(m.group(1));
            return userTTL > 1000L * 60 * 10 && userTTL < 1000L * 60 * 60 * 24 * 30;
        }

        m = SpecialStatements.SubmissionTTL.matcher(text);
        if (m.find())
        {
            // TODO: make the bounds a parameter
            final long submissionTTL = Long.parseLong(m.group(1));
            return submissionTTL > 1000L * 60 * 10 && submissionTTL < 1000L * 60 * 60 * 24 * 30;
        }

        m = SpecialStatements.VoteTTL.matcher(text);
        if (m.find())
        {
            // TODO: make the bounds a parameter
            final long voteTTL = Long.parseLong(m.group(1));
            return voteTTL > 1000L * 60 * 10 && voteTTL < 1000L * 60 * 60 * 24 * 30;
        }

        m = SpecialStatements.SupporthThreshold.matcher(text);
        if (m.find())
        {
            // TODO: make the bounds a parameter
            final int supportThreshold = Integer.parseInt(m.group(1));
            return supportThreshold > 0 && supportThreshold < 100;
        }

        m = SpecialStatements.VoteThreshold.matcher(text);
        if (m.find())
        {
            // TODO: make the bounds a parameter
            final int voteThreshold = Integer.parseInt(m.group(1));
            return voteThreshold > 0 && voteThreshold < 100;
        }

        m = SpecialStatements.YesThreshold.matcher(text);
        if (m.find())
        {
            // TODO: make the bounds a parameter
            final int yesThreshold = Integer.parseInt(m.group(1));
            return yesThreshold > 0 && yesThreshold < 100;
        }

        return true;
    }

    @Override
    public void heartbeat(final long now, final String userId)
    {
        dataManager   .heartbeat(now, userId, now + groupParameters.getUserTTL());
        eventPublisher.heartbeat(now, userId, now + groupParameters.getUserTTL());

        lastResponse = new CommandResponse(CommandResponse.Code.OK, null);
    }

    @Override
    public void leave(final long now, final String userId)
    {
        if (!dataManager.isUserExists(userId))
        {
            lastResponse = new CommandResponse(CommandResponse.Code.BAD_COMMAND, "user:" + userId + " does not exist");
            return;
        }

        if (!dataManager.isUserActive(userId))
        {
            lastResponse = new CommandResponse(CommandResponse.Code.COMMAND_REJECT, "user:" + userId + " is already inactive");
            return;
        }

        dataManager   .timeoutUser(now, userId);
        eventPublisher.timeoutUser(now, userId);

        lastResponse = new CommandResponse(CommandResponse.Code.OK, null);
    }

    @Override
    public String submit(final long now, final String userId, final String text)
    {
        if (!dataManager.isUserExists(userId))
        {
            lastResponse = new CommandResponse(CommandResponse.Code.BAD_COMMAND, "user:" + userId + " does not exist");
            return null;
        }

        final byte[] binStatementId = messageDigest.digest(("" + now + userId + text).getBytes());

        final StringBuilder sb = new StringBuilder();
        for (final byte b : binStatementId)
        {
            if ((0xff & b) < 0x10)
            {
                sb.append("0").append(Integer.toHexString(0xff & b));
            }
            else
            {
                sb.append(Integer.toHexString(0xff & b));
            }
        }

        final String statementId = sb.toString();

        if (dataManager.isStatementExists(statementId))
        {
            lastResponse = new CommandResponse(CommandResponse.Code.COMMAND_REJECT, "internal hashing issue");
            return null;
        }

        if (!validParameterChange(text))
        {
            lastResponse = new CommandResponse(CommandResponse.Code.BAD_COMMAND, "invalid parameter change");
            return null;
        }

        dataManager.submit(
                now,
                userId,
                statementId,
                text,
                now + groupParameters.getSubmissionTTL(),
                dataManager.getActiveUserCount(),
                groupParameters.getSupportThreshold(),
                now + groupParameters.getUserTTL());

        eventPublisher.submit(
                now,
                userId,
                statementId,
                text,
                now + groupParameters.getSubmissionTTL(),
                dataManager.getActiveUserCount(),
                groupParameters.getSupportThreshold(),
                now + groupParameters.getUserTTL());

        lastResponse = new CommandResponse(CommandResponse.Code.OK, "statementId:" + statementId);

        return statementId;
    }

    @Override
    public void support(final long now, final String userId, final String statementId)
    {
        if (!dataManager.isUserExists(userId))
        {
            lastResponse = new CommandResponse(CommandResponse.Code.BAD_COMMAND, "user:" + userId + " does not exist");
            return;
        }

        if (!dataManager.isStatementExists(statementId))
        {
            lastResponse = new CommandResponse(CommandResponse.Code.BAD_COMMAND, "statement:" + statementId + " does not exist");
            return;
        }

        if (!dataManager.isUserActive(userId))
        {
            lastResponse = new CommandResponse(CommandResponse.Code.COMMAND_REJECT, "user:" + userId + " is not active");
            return;
        }

        if (dataManager.getState(statementId) != StatementState.ACTIVE)
        {
            lastResponse = new CommandResponse(CommandResponse.Code.COMMAND_REJECT, "statement:" + statementId + " is not active");
            return;
        }

        if (dataManager.isSupported(userId, statementId))
        {
            lastResponse = new CommandResponse(CommandResponse.Code.BAD_COMMAND, "user:" + userId + " already supports statement:" + statementId);
            return;
        }

        if (!dataManager.isSupportEligible(userId, statementId))
        {
            lastResponse = new CommandResponse(CommandResponse.Code.BAD_COMMAND, "user:" + userId + " not eligible to support statement:" + statementId);
            return;
        }

        dataManager   .support(now, userId, statementId, now + groupParameters.getUserTTL());
        eventPublisher.support(now, userId, statementId, now + groupParameters.getUserTTL());

        // begin vote
        if (100 * dataManager.getSupportCount(statementId) / dataManager.getNumEligibleSupporters(statementId) >=
                groupParameters.getSupportThreshold())
        {
            dataManager.beginVote(
                    now,
                    statementId,
                    now + groupParameters.getVoteTTL(),
                    dataManager.getActiveUserCount(),
                    groupParameters.getVoteThreshold(),
                    groupParameters.getYesThreshold());

            eventPublisher.beginVote(
                    now,
                    statementId,
                    now + groupParameters.getVoteTTL(),
                    dataManager.getActiveUserCount(),
                    groupParameters.getVoteThreshold(),
                    groupParameters.getYesThreshold());
        }

        lastResponse = new CommandResponse(CommandResponse.Code.OK, null);
    }

    @Override
    public void vote(final long now, final String userId, final String statementId, final Vote vote)
    {
        if (!dataManager.isUserExists(userId))
        {
            lastResponse = new CommandResponse(CommandResponse.Code.BAD_COMMAND, "user:" + userId + " does not exist");
            return;
        }

        if (!dataManager.isStatementExists(statementId))
        {
            lastResponse = new CommandResponse(CommandResponse.Code.BAD_COMMAND, "statement:" + statementId + " does not exist");
            return;
        }

        if (dataManager.getState(statementId) != StatementState.VOTING)
        {
            lastResponse = new CommandResponse(CommandResponse.Code.COMMAND_REJECT, "statement:" + statementId + " is not voting");
            return;
        }

        if (!dataManager.isVoteEligible(userId, statementId))
        {
            lastResponse = new CommandResponse(CommandResponse.Code.BAD_COMMAND, "user:" + userId + " not eligible to vote on statement:" + statementId);
            return;
        }

        dataManager   .vote(now, userId, statementId, vote, now + groupParameters.getUserTTL());
        eventPublisher.vote(now, userId, statementId, vote, now + groupParameters.getUserTTL());

        lastResponse = new CommandResponse(CommandResponse.Code.OK, null);
    }
}
