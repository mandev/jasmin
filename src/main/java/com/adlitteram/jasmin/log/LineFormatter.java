package com.adlitteram.jasmin.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import org.apache.commons.io.IOUtils;

public class LineFormatter extends Formatter {

    private final static String format = "{0,date} {0,time}";
    private Date dat = new Date();
    private MessageFormat formatter;
    private final Object args[] = new Object[1];
    // Line separator string.  This is the value of the line.separator
    // property at the moment that the SimpleFormatter was created.
    private final String lineSeparator = "\n";

    /**
     * Format the given LogRecord.
     *
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();

        // Minimize memory allocations here.
        dat.setTime(record.getMillis());
        args[0] = dat;
        StringBuffer text = new StringBuffer();
        if (formatter == null) {
            formatter = new MessageFormat(format);
        }
        formatter.format(args, text, null);
        sb.append(text);
        sb.append(" ");

        if (record.getSourceClassName() != null) {
            String str = record.getSourceClassName();
            if (str.startsWith("com.adlitteram.edoc.")) {
                sb.append(str.substring(20));
            } else {
                sb.append(str);
            }
        } else {
            sb.append(record.getLoggerName());
        }

        if (record.getSourceMethodName() != null) {
            sb.append(" ");
            sb.append(record.getSourceMethodName());
        }

//    sb.append(lineSeparator);
        sb.append(" ");

        String message = formatMessage(record);
        sb.append(record.getLevel().getLocalizedName());
        sb.append(": ");
        sb.append(message);
        sb.append(lineSeparator);

        if (record.getThrown() != null) {
            PrintWriter pw = null;
            try {
                StringWriter sw = new StringWriter();
                pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
                IOUtils.closeQuietly(pw);
            }
        }
        return sb.toString();
    }
}
