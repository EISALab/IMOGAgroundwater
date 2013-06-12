package ncsa.d2k.modules.projects.dtcheng.servio;


import ncsa.d2k.core.modules.*;
import java.util.*;
import Serialio.*;
import ncsa.d2k.modules.projects.dtcheng.*;


public class SampleAnalogSignals extends OutputModule {

  private static int NumChannels = 8;
  public void setNumChannels(int value) {
    this.NumChannels = value;
  }


  public int getNumChannels() {
    return this.NumChannels;
  }


  private static int NumSamples = 32;
  public void setNumSamples(int value) {
    this.NumSamples = value;
  }


  public int getNumSamples() {
    return this.NumSamples;
  }


  public String getModuleName() {
    return "SampleAnalogSignals";
  }


  public String getModuleInfo() {
    return "SampleAnalogSignals";
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
    double[] VoltageData = new double[NumSamples * NumChannels];

    byte[] data7bytes = new byte[7];

    for (int t = 0; t < NumSamples; t++) {
      for (int c = 0; c < NumChannels; c++) {

        data7bytes[0] = (byte) 0x78;
        data7bytes[1] = (byte) (21 + c);
        data7bytes[2] = (byte) 0x04;
        data7bytes[3] = (byte) 0;
        data7bytes[4] = (byte) 0;
        data7bytes[5] = (byte) 0;
        data7bytes[6] = (byte) 0;

        synchronized (Global.serCfg) {

          Global.p.putData(data7bytes);

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
            c--;
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
          double volts = (value_msb * 256 + value_lsb) / 1024.0 * 5.0;

          VoltageData[t * NumChannels + c] = volts;
        }
      }
    }

    if (false) {
      for (int c = 0; c < NumChannels; c++) {
        double sum;
        sum = 0.0;
        for (int t = 0; t < NumSamples; t++) {

          sum += VoltageData[t * NumChannels + c];
        }

        double AverageVolts = sum / NumSamples;
        System.out.print("\t" + (int) (AverageVolts * 100000) / 100000.0);

        double VarianceSum = 0.0;
        for (int t = 0; t < NumSamples; t++) {
          double difference = VoltageData[t * NumChannels + c] - AverageVolts;
          VarianceSum += difference * difference;
        }
        double VoltsSTD = Math.sqrt(VarianceSum / NumSamples);
        System.out.print("\t" + (int) (VoltsSTD * 100000) / 100000.0);
      }
      System.out.println();
    }

    this.pushOutput(VoltageData, 0);
  }
}
