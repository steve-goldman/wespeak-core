package com.wespeak.core;

import java.util.regex.Pattern;

public class SpecialStatements
{
    private static final String  UserTTLStem          = "Change INACTIVITY TIMEOUT to ";
    public  static final Pattern UserTTL              = Pattern.compile("^" + UserTTLStem + "(\\d+)$");

    private static final String  StatementTTLStem     = "Change STATEMENT LIFESPAN to ";
    public  static final Pattern StatementTTL         = Pattern.compile("^" + StatementTTLStem + "(\\d+)$");

    private static final String  VoteTTLStem          = "Change VOTE LIFESPAN to ";
    public  static final Pattern VoteTTL              = Pattern.compile("^" + VoteTTLStem + "(\\d+)$");

    private static final String  SupportThresholdStem = "Change SUPPORT THRESHOLD to ";
    public  static final Pattern SupporthThreshold    = Pattern.compile("^" + SupportThresholdStem + "(\\d+)$");

    private static final String  VoteThresholdStem    = "Change VOTE THRESHOLD to ";
    public  static final Pattern VoteThreshold        = Pattern.compile("^" + VoteThresholdStem + "(\\d+)$");

    private static final String  YesThresholdStem     = "Change YES THRESHOLD to ";
    public  static final Pattern YesThreshold         = Pattern.compile("^" + YesThresholdStem + "(\\d+)$");

    public static String makeUserTTL(final long userTTL)
    {
        return UserTTLStem + userTTL;
    }

    public static String makeStatementTTL(final long statementTTL)
    {
        return StatementTTLStem + statementTTL;
    }

    public static String makeVoteTTL(final long voteTTL)
    {
        return VoteTTLStem + voteTTL;
    }

    public static String makeSupporthThreshold(final int supportThreshold)
    {
        return SupportThresholdStem + supportThreshold;
    }

    public static String makeVoteThreshold(final int voteThreshold)
    {
        return VoteThresholdStem + voteThreshold;
    }

    public static String makeYesThreshold(final int yesThreshold)
    {
        return YesThresholdStem + yesThreshold;
    }
}
