package net.gavrix32.engine.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Logger {
    private static final ArrayList<String> logList = new ArrayList<>();
    public static boolean printToOutput = true;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("y-M-d HH:mm:ss");

    public static void info(String msg) {
        msg = "[" + formatter.format(LocalDateTime.now()) + "] [INFO] " + msg;
        logList.add(msg);
        if (printToOutput) System.out.println(msg);
    }

    public static void info(Object obj) {
        String msg = formatter.format(LocalDateTime.now()) + " [INFO] " + obj.toString();
        logList.add(msg);
        if (printToOutput) System.out.println(msg);
    }

    public static void warning(String msg) {
        msg = formatter.format(LocalDateTime.now()) + " [WARNING] " + msg;
        logList.add(msg);
        if (printToOutput) System.out.println("\u001B[33m" + msg + "\u001B[0m)");
    }

    public static void warning(Object obj) {
        String msg = formatter.format(LocalDateTime.now()) + " [WARNING] " + obj.toString();
        logList.add(msg);
        if (printToOutput) System.out.println("\u001B[33m" + msg + "\u001B[0m)");
    }

    public static void error(String msg) {
        msg = formatter.format(LocalDateTime.now()) + " [ERROR] " + msg;
        logList.add(msg);
        if (printToOutput) System.err.println(msg);
    }

    public static void error(Object obj) {
        String msg = formatter.format(LocalDateTime.now()) + " [ERROR] " + obj.toString();
        logList.add(msg);
        if (printToOutput) System.err.println(msg);
    }

    public static ArrayList<String> getLogList() {
        return logList;
    }
}