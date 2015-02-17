package com.wespeak.core.engine;

import com.wespeak.core.SpecialStatements;
import com.wespeak.core.datamanager.DataManager;

import java.util.regex.Matcher;

public class EngineBase
{
    protected final DataManager dataManager;
    protected final GroupParameters groupParameters;

    protected EngineBase(final DataManager dataManager, final GroupParameters groupParameters)
    {
        this.dataManager     = dataManager;
        this.groupParameters = groupParameters;
    }

    protected void updateGroupParameters(final String text)
    {
        Matcher m;

        m = SpecialStatements.UserTTL.matcher(text);
        if (m.find())
        {
            groupParameters.setUserTTL(Long.parseLong(m.group(1)));
            return;
        }

        m = SpecialStatements.SubmissionTTL.matcher(text);
        if (m.find())
        {
            groupParameters.setSubmissionTTL(Long.parseLong(m.group(1)));
            return;
        }

        m = SpecialStatements.VoteTTL.matcher(text);
        if (m.find())
        {
            groupParameters.setVoteTTL(Long.parseLong(m.group(1)));
            return;
        }

        m = SpecialStatements.SupporthThreshold.matcher(text);
        if (m.find())
        {
            groupParameters.setSupportThreshold(Integer.parseInt(m.group(1)));
            return;
        }

        m = SpecialStatements.VoteThreshold.matcher(text);
        if (m.find())
        {
            groupParameters.setVoteThreshold(Integer.parseInt(m.group(1)));
            return;
        }

        m = SpecialStatements.YesThreshold.matcher(text);
        if (m.find())
        {
            groupParameters.setYesThreshold(Integer.parseInt(m.group(1)));
            //return;
        }
    }

}
