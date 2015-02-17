package com.wespeak.core.engine;

public class GroupParameters
{
    private long userTTL;
    private long submissionTTL;
    private long voteTTL;
    private int  supporthThreshold;
    private int  voteThreshold;
    private int  yesThreshold;

    public long getUserTTL()
    {
        return userTTL;
    }

    public void setUserTTL(final long userTTL)
    {
        this.userTTL = userTTL;
    }

    public long getSubmissionTTL()
    {
        return submissionTTL;
    }

    public void setSubmissionTTL(final long submissionTTL)
    {
        this.submissionTTL = submissionTTL;
    }

    public long getVoteTTL()
    {
        return voteTTL;
    }

    public void setVoteTTL(final long voteTTL)
    {
        this.voteTTL = voteTTL;
    }

    public int  getSupportThreshold()
    {
        return supporthThreshold;
    }

    public void setSupportThreshold(final int supportThreshold)
    {
        this.supporthThreshold = supportThreshold;
    }

    public int  getVoteThreshold()
    {
        return voteThreshold;
    }

    public void setVoteThreshold(final int voteThreshold)
    {
        this.voteThreshold = voteThreshold;
    }

    public int  getYesThreshold()
    {
        return yesThreshold;
    }

    public void setYesThreshold(final int yesThreshold)
    {
        this.yesThreshold = yesThreshold;
    }
}
