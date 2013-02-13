package util;

/**
 *
 * @author sam
 */
public class Log {

    public Log() {
    }

    // normal log
    public static void p(String msg) {
        System.out.println(msg);
    }

    public static void p(int msg) {
        p(Integer.toString(msg));
    }

    public static void p(float msg) {
        p(Float.toString(msg));
    }

    public static void p(double msg) {
        p(Double.toString(msg));
    }

    public static void p(long msg) {
        p(Long.toString(msg));
    }

    public static void p(boolean msg) {
        p(Boolean.toString(msg));
    }

    // error log
    public static void err(String msg) {
        System.err.println("Error: " + msg);
    }

    public static void err(int msg) {
        err(Integer.toString(msg));
    }

    public static void err(float msg) {
        err(Float.toString(msg));
    }

    public static void err(double msg) {
        err(Double.toString(msg));
    }

    public static void err(long msg) {
        err(Long.toString(msg));
    }

    public static void err(boolean msg) {
        err(Boolean.toString(msg));
    }
}
