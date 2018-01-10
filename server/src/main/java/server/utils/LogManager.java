package server.utils;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogManager {

    private static LogManager instance = null;
    private Logger logger;

    /**
     * Creates the log file and the logger instance
     */
    protected LogManager() {
        logger = Logger.getLogger("ServerLog");
        FileHandler fh;

        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler("ServerLogFile.log");
            logger.addHandler(fh);
            logger.setUseParentHandlers(false);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Singleton method
     * @return LogManager instance
     */
    public static LogManager getInstance() {
        if(instance == null) {
            instance = new LogManager();
        }
        return instance;
    }

    /**
     * Adds a new info log entry
     * @param info the string to be added to the log
     */
    public void logInfo(String info) {
        logger.info(info);
    }

    /**
     * Adds a new error log entry
     * @param error the string to be added to the log
     */
    public void logError(String error) {
        logger.severe(error);
    }
}
