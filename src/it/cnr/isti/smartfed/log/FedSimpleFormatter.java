package it.cnr.isti.smartfed.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

//This custom formatter formats parts of a log record to a single line
class FedSimpleFormatter extends Formatter {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    public String format(LogRecord record) {
    	return record.getLevel() + ":" + record.getMessage() + LINE_SEPARATOR;
    	
        // StringBuilder sb = new StringBuilder();

        /*
        sb.append(new Date(record.getMillis()))
            .append(" ")
            .append(record.getLevel().getLocalizedName())
            .append(": ")
            .append(formatMessage(record))
            .append(LINE_SEPARATOR);
         
        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
                // ignore
            }
        }

        return sb.toString();
        */
    }
	/*
  // This method is called for every log records
  public String format(LogRecord rec) {
    StringBuffer buf = new StringBuffer(1000);
    // Bold any levels >= WARNING
    
    if (rec.getLevel().intValue() >= Level.WARNING.intValue()) {
      
      buf.append(rec.getLevel());
      
    } else {
      buf.append(rec.getLevel());
    }
    // buf.append(calcDate(rec.getMillis()));
    buf.append(formatMessage(rec));
    return buf.toString();
  }

  private String calcDate(long millisecs) {
    SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
    Date resultdate = new Date(millisecs);
    return date_format.format(resultdate);
  }

  // This method is called just after the handler using this
  // formatter is created
  public String getHead(Handler h) {
    return "PUPPA\n";
  }

  // This method is called just after the handler using this
  // formatter is closed
  public String getTail(Handler h) {
    return "SUCAn";
  }
  */
} 