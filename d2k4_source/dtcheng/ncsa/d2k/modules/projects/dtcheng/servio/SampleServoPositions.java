package ncsa.d2k.modules.projects.dtcheng.servio;


import ncsa.d2k.core.modules.*;
import java.util.*;
import Serialio.*;
import ncsa.d2k.modules.projects.dtcheng.*;


public class SampleServoPositions extends OutputModule {

  private static int NumServos = 8;
  public void setNumServos(int value) {
    this.NumServos = value;
  }


  public int getNumServos() {
    return this.NumServos;
  }


  private static int NumSamples = 32;
  public void setNumSamples(int value) {
    this.NumSamples = value;
  }


  public int getNumSamples() {
    return this.NumSamples;
  }


  static final boolean WaitForResponse = true;
  static final boolean PrintExamples = true;

  public String getModuleName() {
    return "SampleServoPositions";
  }


  public String getModuleInfo() {
    return "SampleServoPositions";
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
        return "Samples";
      default:
        return "NO SUCH OUTPUT!";
    }
  }


  public String getOutputInfo(int index) {
    switch (index) {
      default:
        return "Samples";
    }
  }


  public String[] getOutputTypes() {
    String[] types = {
        "[D"};
    return types;
  }


  RS232Controller PortController;

  public void endExecution() {
    //PortController.close();
  }


  public void doit() throws Exception {

    this.pullInput(0);

    synchronized (Global.serCfg) {

      if (Global.serCfg == null) {
        Global.initialize();
      }
    }

    ///////////////////////////
    // read analog channels  //
    ///////////////////////////
    double[] Values = new double[NumSamples * NumServos];

    byte[] data4bytes = new byte[4];

    for (int t = 0; t < NumSamples; t++) {
      for (int ServoIndex = 0; ServoIndex < NumServos; ServoIndex++) {

        data4bytes[0] = (byte) 0x78;
        data4bytes[1] = (byte) 0x00;
        data4bytes[2] = (byte) 0x0E;
        data4bytes[3] = (byte) (ServoIndex + 1);

        synchronized (Global.serCfg) {
          Global.p.putData(data4bytes);

          // check for time out //

          long last_time = System.currentTimeMillis();
          boolean timed_out = false;
          while (Global.p.rxReadyCount() < 4) {
            long elapsed_time = System.currentTimeMillis() - last_time;
            if (elapsed_time > Global.TimeOutWaitPeriod) {
              timed_out = true;
              break;
            }
            Thread.sleep(Global.ReadWaitSleepTime);
          }
          if (timed_out) {
            ServoIndex--;
            if (Global.ReportTimeOuts) {
              System.out.println("Error!  Timed Out During Sensing, Retrying...");
            }
            //p.rxFlush();
            continue;
          }

          // read data //

          int address = Global.p.getByte();
          int port = Global.p.getByte();
          int value_msb = Global.p.getByte();
          int value_lsb = Global.p.getByte();
          double Value = value_msb * 256 + value_lsb;

          Values[t * NumServos + ServoIndex] = Value;
        }
      }
    }

    if (false) {
      for (int c = 0; c < NumServos; c++) {
        double sum;
        sum = 0.0;
        for (int t = 0; t < NumSamples; t++) {

          sum += Values[t * NumServos + c];
        }

        double AverageValue = sum / NumSamples;
        System.out.print("\t" + (int) (AverageValue * 100000) / 100000.0);

        double VarianceSum = 0.0;
        for (int t = 0; t < NumSamples; t++) {
          double difference = Values[t * NumServos + c] - AverageValue;
          VarianceSum += difference * difference;
        }
        double VoltsSTD = Math.sqrt(VarianceSum / NumSamples);
        System.out.print("\t" + (int) (VoltsSTD * 100000) / 100000.0);
      }
      System.out.println();
    }

    this.pushOutput(Values, 0);
  }
}
