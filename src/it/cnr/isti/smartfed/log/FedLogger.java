package it.cnr.isti.smartfed.log;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FedLogger extends Logger{
  
  protected FedLogger(String name, String resourceBundleName) {
		super(name, resourceBundleName);
		// TODO Auto-generated constructor stub
	}

  static private FileHandler fileTxt;
  static private Formatter formatterTxt;

  static private ConsoleHandler console;
  static private Formatter formatterConsole;

  static public void setup() throws IOException {

    // Get the global logger to configure it
    Logger logger = Logger.getLogger("");

    logger.setLevel(Level.INFO);
    fileTxt = new FileHandler("mylogging.txt");
    console = new ConsoleHandler();

    // Create txt Formatter
    formatterTxt = new FedSimpleFormatter();
    fileTxt.setFormatter(formatterTxt);
    logger.addHandler(fileTxt);

    // Create HTML Formatter
    /*
    formatterConsole = new FedSimpleFormatter();
    console.setFormatter(formatterConsole);
    logger.addHandler(console);
    */
  }
}
 
