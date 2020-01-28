package com.adlitteram.jasmin.log;

import java.util.Date;
import java.util.logging.SimpleFormatter;
import java.util.logging.LogRecord;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.LogManager;
import org.apache.commons.lang3.time.FastDateFormat;

public class StreamFormatter extends SimpleFormatter {

    protected boolean brief;
    protected boolean trunc;
    protected static boolean withMethods = true;
    protected static boolean withClasses = false;
    private final Date dt = new Date();

    protected FastDateFormat fmt = FastDateFormat.getInstance("MM/dd/yyyy HH:mm:ss.SSS");
    protected FastDateFormat bfmt = FastDateFormat.getInstance("EEE HH:mm:ss.SSS #");
    protected static String eol = System.getProperty("line.separator");

    public final synchronized void setTruncateLoggerName(boolean isTrunc) {
        trunc = isTrunc;
    }

    public final synchronized void setBrief(boolean isBrief) {
        brief = isBrief;
    }

    public final synchronized void setWithMethods(boolean how) {
        withMethods = how;
    }

    public final synchronized void setWithClasses(boolean how) {
        withClasses = how;
    }

    public StreamFormatter() {
        checkProps();
    }

    public StreamFormatter(boolean brief) {
        setBrief(brief);
    }

    public StreamFormatter(boolean brief, boolean truncate) {
        setBrief(brief);
        setTruncateLoggerName(truncate);
    }

    private void checkProps() {
        LogManager lm = LogManager.getLogManager();

        String v = lm.getProperty("com.cytetech.log.StreamFormatter.withMethods");
        setWithMethods(v != null && (v.equals("true") || v.equals("yes") || v.equals("1")));

        v = lm.getProperty("com.cytetech.log.StreamFormatter.withClasses");
        setWithClasses(v != null && (v.equals("true") || v.equals("yes") || v.equals("1")));

        v = lm.getProperty("com.cytetech.log.StreamFormatter.brief");
        setBrief(v != null && (v.equals("true") || v.equals("yes") || v.equals("1")));

        v = lm.getProperty("com.cytetech.log.StreamFormatter.truncateLoggerName");
        setTruncateLoggerName(v != null && (v.equals("true") || v.equals("yes") || v.equals("1")));
    }

    @Override
    public String format(LogRecord rec) {
        dt.setTime(rec.getMillis());
        StringBuilder b = new StringBuilder();
        b.append((brief ? bfmt : fmt).format(dt));
        if (!brief) {
            b.append(" [");
            if (trunc) {
                String loggerName = rec.getLoggerName();
                if (loggerName != null) {
                    String[] s = rec.getLoggerName().split("\\.");
                    for (int i = 0; i < s.length - 1; ++i) {
                        b.append(s[i].charAt(0));
                        b.append(".");
                    }
                    b.append(s[s.length - 1]);
                } else {
                    b.append("Anonymous");
                }
            } else {
                b.append(rec.getLoggerName());
                b.append("#");
                b.append(rec.getSequenceNumber());
            }
            b.append("] ");
        } else {
            b.append(" ");
        }
        b.append(rec.getLevel());
        b.append(" # ");

        if (withClasses) {
            b.append(": from=");
            b.append(rec.getSourceClassName());
            b.append(".");
        }
        if (withMethods) {
            b.append(rec.getSourceMethodName());
            b.append("(");
            Object a[] = rec.getParameters();
            if (a != null) {
                b.append(" ");
                for (int i = 0; i < a.length; ++i) {
                    if (i > 0) {
                        b.append(", ");
                    }
                    b.append(a[i]).append("");
                }
            }
            b.append(" ) ");
        }
        b.append(formatMessage(rec));
        if (rec.getThrown() != null) {
            StringWriter wr = new StringWriter();
            rec.getThrown().printStackTrace(new PrintWriter(wr));
            b.append(eol);
            b.append(wr.toString());
        }
        b.append(eol);
        return b.toString();
    }
}
