package utils;

import core.Server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsolePrinter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd't'HH:mm:ss");

    public static enum PrintStatus { OK, ERROR }

    public static void print(PrintStatus status, boolean printDate, String message) {
        if(Server.ALLOW_PRINT) {
            StringBuilder b = new StringBuilder();
            b.append("[").append(status.toString());
            if(printDate) { b.append(", " + formatter.format(LocalDateTime.now())); }
            b.append("] ").append(message);
            System.out.println(b.toString());
        }
    }

}
