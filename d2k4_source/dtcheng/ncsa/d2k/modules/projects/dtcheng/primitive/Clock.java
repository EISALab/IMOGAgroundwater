package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.modules.projects.dtcheng.*;

import java.util.*;
import java.io.*;

public class Clock {
  Date start_date = new Date();

  public void resetClock() {
    start_date = new Date();
  }

  public void reportClock(String title) {
    Date current_date = new Date();
    double seconds = (double) ((current_date.getTime() - start_date.getTime()) / 1000.0);
    System.out.println(title + ": time = " + seconds);
  }

  public double getTime() {
    Date current_date = new Date();
    return (double) ((current_date.getTime() - start_date.getTime()) / 1000.0);
  }

}
