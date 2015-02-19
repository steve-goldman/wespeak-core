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
     *     <now>|pulse
     *
     * heartbeat command looks like:
     *     <now>|heartbeat|<user-id>
     *
     * leave command looks like:
     *     <now>|leave|<user-id>
     *
     * submit command looks like:
     *     <now>|submit|<user-id>|<text-length>
     *     <text>
     *
     * support command looks like:
     *     <now>|support|<user-id>|<statement-id>
     *
     * vote command looks like:
     *     <now>|vote|<user-id>|<statement-id>|<vote>
     *
     * heartbeat event looks like:
     *     <now>|heartbeat|<user-id>|<active-until>
     *
     * submit event looks like:
     *     <now>|submit|<user-id>|<statement-id>|<statement-active-until>|<num-eligible-supporters>|<num-support-needed>|<user-active-until>|<text-length>
     *     <text>
     *
     * support event looks like:
     *     <now>|support|<user-id>|<statement-id>|<user-active-until>
     *
     * vote event looks like:
     *     <now>|vote|<user-id>|<statement-id>|<vote>|<user-active-until>
     *
     * timeout user event looks like:
     *     <now>|timeout_user|<user-id>
     *
     * timeout statement event looks like:
     *     <now>|timeout_statement|<statement-id>
     *
     * begin vote event looks like:
     *     <now>|begin_vote|<statement-id>|<until>|<num-eligible-voters>|<num-votes-needed>|<num-yeses-needed>
     *
     * end vote accepted event looks like:
     *     <now>|end_vote_accepted|<statement-id>
     *
     * end vote rejected event looks like:
     *     <now>|end_vote_rejected|<statement-id>
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
