package com.wespeak.core.engine;

public interface GroupParameters
{
    long getUserTTL();
    void setUserTTL(long userTTL);

    long getSubmissionTTL();
    void setSubmissionTTL(long submittionTTL);

    long getVoteTTL();
    void setVoteTTL(long voteTTL);

    int  getSupportThreshold();
    void setSupportThreshold(int supportThreshold);

    int  getVoteThreshold();
    void setVoteThreshold(int voteThreshold);

    int  getYesThreshold();
    void setYesThreshold(int yesThreshold);
}
