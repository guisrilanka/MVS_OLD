package com.gui.mdt.thongsieknavclient.utils;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.util.Objects;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Created by nelin_000 on 09/25/2017.
 */

public class Log4jHelper {
//    private final static LogConfigurator _logConfigurator = new LogConfigurator();
//    private final static LogConfigurator _saveEditLogConfigurator = new LogConfigurator();
//    public static void Configure(String fileName, String filePattern,
//                                 int maxBackupSize, long maxFileSize) {
//
//        // set the name of the log file
//        _logConfigurator.setFileName(fileName);
//        // set output format of the log line
//        _logConfigurator.setFilePattern(filePattern);
//        // Maximum number of backed up log files
//        _logConfigurator.setMaxBackupSize(maxBackupSize);
//        // Maximum size of log file until rolling
//        _logConfigurator.setMaxFileSize(maxFileSize);
//
//        // configure
//        _logConfigurator.configure();
//
//    }
    public static void configureMainLog(String fileName, String filePattern, int maxBackupSize, long maxFileSize) {

        try {
            FileAppender mainAppender = new FileAppender(
                    new PatternLayout(filePattern),
                    fileName,
                    true
            );
            Logger mainLogger = Logger.getLogger("MainLogger");
            mainLogger.addAppender(mainAppender);
            mainLogger.setLevel(Level.INFO);
            mainLogger.setAdditivity(false); // 🔹 Prevents logs from propagating to other loggers
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void configureSaveEditLog(String filePath, String filePattern, int maxBackupSize, long maxFileSize) {

        try {
            FileAppender saveEditAppender = new FileAppender(
                    new PatternLayout(filePattern),
                    filePath, // 🔹 Setting the correct file path here
                    true
            );
            Logger saveEditLogger = Logger.getLogger("SaveEditLogger");
            saveEditLogger.addAppender(saveEditAppender);
            saveEditLogger.setLevel(Level.INFO);
            saveEditLogger.setAdditivity(false); // 🔹 Prevents logs from propagating to other loggers
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    public static void configureSaveEditLog(String fileName, String filePattern, int maxBackupSize, long maxFileSize) {
//        _saveEditLogConfigurator.setFileName(fileName);
//        _saveEditLogConfigurator.setFilePattern(filePattern);
//        _saveEditLogConfigurator.setMaxBackupSize(maxBackupSize);
//        _saveEditLogConfigurator.setMaxFileSize(maxFileSize);
//        _saveEditLogConfigurator.configure();
//    }

    public static Logger getLogger() {
        Logger logger = Logger.getLogger("MainLogger");
        return logger;
    }

    public static Logger getSaveEditLogger() {
        return Logger.getLogger("SaveEditLogger");
    }
}
