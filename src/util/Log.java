package util;

/**
 *
 * @author sam
 */
public class Log {

    public Log() {
    }

    public static void p(String message) {
        System.out.println(message);
    }

    public static void err(String message) {
        System.err.println("Error: " + message);
    }
}
