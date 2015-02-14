package com.wespeak.core.io.textdump;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TextDumpConstants
{
    /*
     * ignore empty lines
     *
     * ignore lines that start with '#'
     *
     * pulse command looks like:
     *     pulse|<now>
     *
     * heartbeat command looks like:
     *     heartbeat|<now>|<user-id>
     *
     * leave command looks like:
     *     leave|<now>|<user-id>
     *
     * submit command looks like:
     *     submit|<now>|<user-id>|<text-length>
     *     <text>
     *
     * support command looks like:
     *     support|<now>|<user-id>|<statement-id>
     *
     * vote command looks like:
     *     vote|<now>|<user-id>|<statement-id>|<vote>
     *
     * heartbeat event looks like:
     *     heartbeat|<now>|<user-id>|<active-until>
     *
     * submit event looks like:
     *     submit|<now>|<user-id>|<statement-id>|<statement-active-until>|<num-eligible-supporters>|<num-support-needed>|<user-active-until>|<text-length>
     *     <text>
     *
     * support event looks like:
     *     support|<now>|<user-id>|<statement-id>|<user-active-until>
     *
     * vote event looks like:
     *     vote|<now>|<user-id>|<statement-id>|<vote>|<user-active-until>
     *
     * timeout user event looks like:
     *     timeout_user|<now|<user-id>
     *
     * timeout statement event looks like:
     *     timeout_statement|<now>|<statement-id>
     *
     * begin vote event looks like:
     *     begin_vote|<now>|<statement-id>|<until>|<num-eligible-voters>|<num-votes-needed>|<num-yeses-needed>
     *
     * end vote accepted event looks like:
     *     end_vote_accepted|<now>|<statement-id>
     *
     * end vote rejected event looks like:
     *     end_vote_rejected|<now>|<statement-id>
     */

    public static final String Comment   = "#";

    public static final String Separator = "|";

    public static final class CommandTypes
    {
        public static final String Pulse     = "pulse";
        public static final String Heartbeat = "heartbeat";
        public static final String Leave     = "leave";
        public static final String Submit    = "submit";
        public static final String Support   = "support";
        public static final String Vote      = "vote";
    }

    public static final class EventTypes
    {
        public static final String Heartbeat        = "heartbeat";
        public static final String Submit           = "submit";
        public static final String Support          = "support";
        public static final String Vote             = "vote";
        public static final String TimeoutUser      = "timeout_user";
        public static final String TimeoutStatement = "timeout_statement";
        public static final String BeginVote        = "begin_vote";
        public static final String EndVoteAccepted  = "end_vote_accepted";
        public static final String EndVoteRejected  = "end_vote_rejected";
    }

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");

    public static long stringToTime(final String str) throws ParseException
    {
        return formatter.parse(str).getTime();
    }

    public static String timeToString(final long time)
    {
        return formatter.format(new Date(time));
    }
}
