package com.wespeak.core;

import java.util.Iterator;

public interface DataQueries
{
    //
    // queries
    //
    boolean  isUserExists              (String userId);
    boolean  isUserActive              (String userId);

    boolean  isStatementExists         (String statementId);
    StatementState getState            (String statementId);

    String   getText                   (String statementId);
    String   getSubmitter              (String statementId);
    long     getSubmissionTime         (String statementId);
    long     getSubmissionTTL          (long now, String statementId);
    long     getVoteBeginTime          (String statementId);
    long     getVoteTTL                (long now, String statementId);

    long     getUserActiveTime         (String userId);
    long     getUserTTL                (long now, String userId);

    boolean  isSupportEligible         (String userId, String statementId);
    boolean  isVoteEligible            (String userId, String statementId);

    int      getNumEligibleSupporters  (String statementId);
    int      getPropSupportNeeded      (String statementId);
    int      getSupportCount           (String statementId);
    int      getNumEligibleVoters      (String statementId);
    int      getPropVotesNeeded        (String statementId);
    int      getVoteCount              (String statementId);
    int      getPropYesesNeeded        (String statementId);
    int      getYesCount               (String statementId);

    boolean  hasActiveUsers            ();
    String   getOldestActiveUserId     ();
    int      getActiveUserCount        ();
    boolean  hasActiveStatements       ();
    String   getOldestActiveStatementId();

    boolean  isSupported               (String userId, String statementId);
    Vote     getVote                   (String userId, String statementId);

    Iterator<String> getSubmittedStatementIds (String userId);
    Iterator<String> getSupportedStatementsIds(String userId);
    Iterator<String> getVotedStatementIds     (String userId);

    Iterator<String> getActiveStatementIds    ();
    Iterator<String> getInactiveStatementIds  ();
    Iterator<String> getVotingStatementIds    ();
    Iterator<String> getAcceptedStatementIds  ();
    Iterator<String> getRejectedStatementIds  ();

}
