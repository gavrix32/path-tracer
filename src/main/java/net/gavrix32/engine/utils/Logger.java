package net.gavrix32.engine.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Logger {
    private static final ArrayList<String> logList = new ArrayList<>();
    public static boolean printToOutput = true;
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.S");

    public static void info(String msg) {
        msg = timeFormatter.format(LocalDateTime.now()) + " [INFO] " + msg;
        logList.add(msg);
        if (printToOutput) System.out.println(msg);
    }

    public static void warning(String msg) {
        msg = timeFormatter.format(LocalDateTime.now()) + " [WARNING] " + msg;
        logList.add(msg);
        if (printToOutput) System.out.println("\u001B[33m" + msg + "\u001B[0m)");
    }

    public static void error(String msg) {
        msg = timeFormatter.format(LocalDateTime.now()) + " [ERROR] " + msg;
        logList.add(msg);
        if (printToOutput) System.err.println(msg);
    }

    public static ArrayList<String> getLogList() {
        return logList;
    }

    /*public static void clearLogList() {
        logList.clear();
    }*/
}