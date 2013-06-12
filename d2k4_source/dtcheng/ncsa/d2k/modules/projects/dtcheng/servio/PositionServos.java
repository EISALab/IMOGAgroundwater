package ncsa.d2k.modules.projects.dtcheng.servio;


import Serialio.*;

import ncsa.d2k.core.modules.*;
import java.util.*;
import ncsa.d2k.modules.projects.dtcheng.*;


public class PositionServos extends OutputModule {

  private static double StepSize = 0.01;
  public void setStepSize(double value) {
    this.StepSize = value;
  }


  public double getStepSize() {
    return this.StepSize;
  }


  private static double RangeOfMotion = 1.0;
  public void setRangeOfMotion(double value) {
    this.RangeOfMotion = value;
  }


  public double getRangeOfMotion() {
    return this.RangeOfMotion;
  }


  private static int NumServos = 4;
  public void setNumServos(int value) {
    this.NumServos = value;
  }


  public int getNumServos() {
    return this.NumServos;
  }


  private static int ResponseRate = 1;
  public void setResponseRate(int value) {
    this.ResponseRate = value;
  }


  public int getResponseRate() {
    return this.ResponseRate;
  }


  private static int Speed = 16;
  public void setSpeed(int value) {
    this.Speed = value;
  }


  public int getSpeed() {
    return this.Speed;
  }


  public String getModuleName() {
    return "PositionServos";
  }


  public String getModuleInfo() {
    return "PositionServos";
  }


  public String getInputName(int index) {
    switch (index) {
      case 0:
        return "Trigger";
      default:
        return "No such input";
    }
  }


  public String getInputInfo(int index) {
    switch (index) {
      case 0:
        return "Trigger";
      default:
        return "No such input";
    }
  }


  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }


  public String getOutputName(int index) {
    switch (index) {
      case 0:
        return "Angles";
      default:
        return "No such input";
    }
  }


  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        return "Angles";
      default:
        return "No such input";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {"[D"};
    return types;
  }


  RS232Controller PortController;

  int Count = 0;
  public void beginExecution() {
    Count = 0;
  }


  static public void PositionServos(double[] angles) throws Exception {

    synchronized (Global.serCfg) {

      if (Global.serCfg == null) {
        Global.initialize();
      }
    }

    int NumPositions = angles.length;
    byte[] data = new byte[6];

    for (int i = 0; i < NumPositions; i++) {

      double ms = (angles[i] + 90) / 180.0 + 1.0;
      int msInt = (int) (ms * 1000);
      int PosMSB = (int) (msInt / 256);
      int PosLSB = (int) msInt % 256;
      data[0] = (byte) 0x78;
      data[1] = (byte) (i + 1);
      data[2] = (byte) 0x02;
      data[3] = (byte) PosMSB;
      data[4] = (byte) PosLSB;
      data[5] = (byte) Speed; // speed

      synchronized (Global.serCfg) {

        Global.p.putData(data);

        long last_time = System.currentTimeMillis();
        boolean timed_out = false;
        while (Global.p.rxReadyCount() < 1) {
          long elapsed_time = System.currentTimeMillis() - last_time;
          if (elapsed_time > Global.TimeOutWaitPeriod) {
            timed_out = true;
            break;
          }
          Thread.sleep(Global.ReadWaitSleepTime);
        }
        if (timed_out) {
          i--;
          if (Global.ReportTimeOuts) {
            System.out.println("Error!  Timed Out During Positioning, Retrying...");
          }
          //p.rxFlush();
          continue;
        }

        int returnValue = Global.p.getByte();

        if (returnValue != 120) {
          System.out.println("error returnValue = " + returnValue);
        }
      }
    }

  }


  double[] LastAngles = null;
  public void doit() throws Exception {

    // pull trigger
    Object Trigger = (Object)this.pullInput(0);

    if (Count % ResponseRate == 0) {

      if (LastAngles == null) {
        LastAngles = new double[NumServos];
        for (int t = 0; t < NumServos; t++) {
          LastAngles[t] = 0;
        }

      }

      // calcualte next set of angles
      double[] Angles = new double[NumServos];

      for (int t = 0; t < NumServos; t++) {
        Angles[t] = LastAngles[t] + (Math.random() - 0.5) * 2.0 * RangeOfMotion * 90.0 * StepSize;
        if (Angles[t] < -RangeOfMotion * 90.0) {
          t--;
          continue;
        }
        if (Angles[t] > RangeOfMotion * 90.0) {
          t--;
          continue;
        }
      }

      PositionServos(Angles);

      for (int t = 0; t < NumServos; t++) {
        LastAngles[t] = Angles[t];
      }
    }

    Count++;

    this.pushOutput(LastAngles, 0);
  }
}
