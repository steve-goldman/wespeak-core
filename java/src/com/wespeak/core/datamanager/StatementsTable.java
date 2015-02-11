package com.wespeak.core.datamanager;

import java.util.Iterator;

public interface StatementsTable
{
    public enum State
    {
        ACTIVE,
        INACTIVE,
        VOTING,
        ACCEPTED,
        REJECTED,
    }

    //
    // getters
    //
    boolean  exists                  (String statementId);
    String   getSubmitter            (String statementId);
    String   getText                 (String statementId);
    State    getState                (String statementId);
    long     getSubmissionTime       (String statementId);
    long     getExpirationTime       (String statementId);
    long     getVoteBeginTime        (String statementId);
    long     getVoteEndTime          (String statementId);
    int      getNumEligibleSupporters(String statementId);
    int      getNumSupportNeeded     (String statementId);
    int      getNumEligibleVoters    (String statementId);
    int      getNumVotesNeeded       (String statementId);
    int      getNumYesesNeeded       (String statementId);
    String   getOldestActiveStatement();

    Iterator<String> getStatementIds (String userId);

    //
    // setters
    //
    void     addStatement            (String statementId,
                                      String userId,
                                      String text,
                                      long   submissionTime,
                                      long   expirationTime,
                                      int    numEligibleSupporters,
                                      int    numSupportNeeded);

    void     setInactive             (String statementId);

    void     beginVote               (String statementId,
                                      long   voteBeginTime,
                                      long   voteEndTime,
                                      int    numEligibleVoters,
                                      int    numVotesNeeded,
                                      int    numYesesNeeded);

    void     endVote                 (String statementId);
}
