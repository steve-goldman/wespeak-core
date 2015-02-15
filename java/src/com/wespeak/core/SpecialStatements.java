package com.wespeak.core;

import java.util.regex.Pattern;

public class SpecialStatements
{
    public static final Pattern UserTTL           = Pattern.compile("^Change INACTIVITY TIMEOUT to (\\d+)$");
    public static final Pattern SubmissionTTL     = Pattern.compile("^Change STATEMENT LIFESPAN to (\\d+)$");
    public static final Pattern VoteTTL           = Pattern.compile("^Change VOTE LIFESPAN to (\\d+)$");
    public static final Pattern SupporthThreshold = Pattern.compile("^Change SUPPORT THRESHOLD to (\\d+)$");
    public static final Pattern VoteThreshold     = Pattern.compile("^Change VOTE THRESHOLD to (\\d+)$");
    public static final Pattern YesThreshold      = Pattern.compile("^Change YES THRESHOLD to (\\d+)$");
}
