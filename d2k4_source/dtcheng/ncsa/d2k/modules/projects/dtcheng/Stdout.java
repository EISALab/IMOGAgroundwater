package ncsa.d2k.modules.projects.dtcheng;

/******************************************************
 * File: Stdout.java.java
 * created 24.07.2001 21:44:46 by David Fischer, fischer@d-fischer.com
 * Description: utility class for standard output
 */


public class Stdout {

  public static void log(String msg) {
    System.out.println(msg);
  }


  public static void logAndAbortException(Exception e) {
    log("" + e);
    flush();
    System.exit(0);
  }


  public static void logAndAbortError(Error e) {
    log("" + e);
    flush();
    System.exit(0);
  }


  public static void flush() {
    System.out.flush();
  }

}
