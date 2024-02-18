package net.gavrix32.engine.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public static void info(String msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        System.out.println(dtf.format(LocalDateTime.now()) + " [INFO] " + msg);
    }

    public static void error(String msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        System.err.println(dtf.format(LocalDateTime.now()) + " [ERROR] " + msg);
    }
}