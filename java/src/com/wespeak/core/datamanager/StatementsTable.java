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
    int      getPropSupportNeeded    (String statementId);
    int      getNumEligibleVoters    (String statementId);
    int      getPropVotesNeeded      (String statementId);
    int      getPropYesesNeeded      (String statementId);
    boolean  hasActiveStatements     ();
    String   getOldestActiveStatement();

    Iterator<String> getInactiveStatements();
    Iterator<String> getActiveStatements  ();
    Iterator<String> getVotingStatements  ();
    Iterator<String> getAcceptedStatements();
    Iterator<String> getRejectedStatements();

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
                                      int    propSupportNeeded);

    void     setInactive             (String statementId);

    void     beginVote               (String statementId,
                                      long   voteBeginTime,
                                      long   voteEndTime,
                                      int    numEligibleVoters,
                                      int    propVotesNeeded,
                                      int    propYesesNeeded);

    void     endVoteAccepted         (String statementId);
    void     endVoteRejected         (String statementId);
}
