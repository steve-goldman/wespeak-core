package com.wespeak.core.engine;

public class GroupParameters
{
    private long userTTL            = 1;
    private long statementTTL       = 1;
    private long voteTTL            = 1;
    private int  supporthThreshold  = 1;
    private int  voteThreshold      = 1;
    private int  yesThreshold       = 1;

    public long getUserTTL()
    {
        return userTTL;
    }

    public void setUserTTL(final long userTTL)
    {
        this.userTTL = userTTL;
    }

    public long getStatementTTL()
    {
        return statementTTL;
    }

    public void setStatementTTL(final long statementTTL)
    {
        this.statementTTL = statementTTL;
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
