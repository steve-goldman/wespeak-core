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
     * heartbeats look like:
     *     heartbeat|<now>|<user-id>|<active-until>
     *
     * submit looks like:
     *     submit|<now>|<user-id>|<statement-id>|<statement-active-until>|<num-eligible-supporters>|<num-support-needed>|<user-active-until>|<text-length>
     *     <text>
     *
     * support looks like:
     *     support|<now>|<user-id>|<statement-id>|<user-active-until>
     *
     * vote looks like:
     *     vote|<now>|<user-id>|<statement-id>|<vote>|<user-active-until>
     *
     * timeout user looks like:
     *     timeout_user|<now|<user-id>
     *
     * timeout statement looks like:
     *     timeout_statement|<now>|<statement-id>
     *
     * begin vote looks like:
     *     begin_vote|<now>|<statement-id>|<until>|<num-eligible-voters>|<num-votes-needed>|<num-yeses-needed>
     *
     * end vote accepted looks like:
     *     end_vote_accepted|<now>|<statement-id>
     *
     * end vote rejected looks like:
     *     end_vote_rejected|<now>|<statement-id>
     */

    public static final String Comment   = "#";

    public static final String Separator = "|";

    public static final class CommandTypes
    {
        public static final String Heartbeat = "heartbeat";
        public static final String Submit = "submit";
        public static final String Support = "support";
        public static final String Vote = "vote";
    }

    public static final class EventTypes
    {
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
