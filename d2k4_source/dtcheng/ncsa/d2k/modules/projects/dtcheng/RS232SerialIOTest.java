package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.core.modules.*;
import java.util.*;
import Serialio.*;

public class RS232SerialIOTest
    extends InputModule {

  static final int MaxNumExamples = 999999999;
  static final double RangeOfMotion = 0.9;
  static final int Speed = 8;
  static final int NumAnalogChannels = 6;
  static final int NumTrials = 64;
  static final int TimeOutWaitPeriod = 100;
  static final long SleepTime = 1;
  static boolean ChangeBaudRate = false;

  //static int BaudRate = SerialConfig.BR_4800;
  static int BaudRate = SerialConfig.BR_9600;
  //static final int BaudRate = SerialConfig.BR_115200;
  static final int NumServos = 8;
  static final int IntialCenteringWait = 2 * 1000;
  static final long MovementDuration = (long) (3.0 * 1000L);
  static final double StepSize = 1.0;

  static final boolean ReportTimeOuts = true;
  static final boolean WaitForResponse = true;
  static final boolean PrintExamples = true;
  static final long CommandWaitLength = 0L; // 16 for direct connection; 80 for wirless through keysan
  static final String devName = "COM1"; //on Unix use device name e.g. "/dev/ttyS0"

  static SerialConfig serCfg;
  static SerialPort p;

  public String getModuleName() {
    return "RS232SerialIOTest";
  }

  public String getModuleInfo() {
    return "This modules test the RS232 port.";
  }

  public String getInputName(int index) {
    switch (index) {
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String getInputInfo(int index) {
    switch (index) {
      default:
        return "No such input";
    }
  }

  public String getOutputName(int index) {
    switch (index) {
      case 0:
        return "Controller";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.RS232Controller"};
    return types;
  }

  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        return "Controller";
      default:
        return "No such output";
    }
  }

  RS232Controller PortController;

  public void endExecution() {
    //PortController.close();
  }

  static public void PositionServos(double[] angles) throws Exception {

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

      p.putData(data);

      long last_time = System.currentTimeMillis();
      boolean timed_out = false;
      while (p.rxReadyCount() < 1) {
        long elapsed_time = System.currentTimeMillis() - last_time;
        if (elapsed_time > TimeOutWaitPeriod) {
          timed_out = true;
          break;
        }
        Thread.sleep(SleepTime);
      }
      if (timed_out) {
        i--;
        if (ReportTimeOuts) {
          System.out.println("Error!  Timed Out During Positioning, Retrying...");
        }
        //p.rxFlush();
        continue;
      }

      int returnValue = p.getByte();

      if (returnValue != 120) {
        System.out.println("error returnValue = " + returnValue);
      }
    }

  }

  static public byte WaitForByte() throws Exception {

    long last_time = System.currentTimeMillis();
    boolean timed_out = false;
    while (p.rxReadyCount() < 1) {
      long elapsed_time = System.currentTimeMillis() - last_time;
      if (elapsed_time > TimeOutWaitPeriod) {
        timed_out = true;
        break;
      }
      Thread.sleep(SleepTime);
    }
    if (timed_out) {
      throw new Exception();
    }
    else {

      int returnValue = p.getByte();

      return (byte) returnValue;
    }
  }

  static public void SetDigitalOutput(int port, int value) throws Exception {

    byte[] data4bytes = new byte[4];
    data4bytes[0] = (byte) 0x78;
    data4bytes[1] = (byte) (port);
    data4bytes[2] = (byte) 0x00;
    if (value != 0)
      data4bytes[3] = (byte) 0x01;
    else
      data4bytes[3] = (byte) 0x00;

    byte returnValue = 0;

    while (true) {
      p.putData(data4bytes);

      boolean TimedOut = false;
      try {
        returnValue = WaitForByte();
      }
      catch (Exception ex) {
        TimedOut = true;
      }

      if (TimedOut) {
        System.out.println("digital output timed out, retrying...");
        continue;
      }
      else if (returnValue != (byte) 120) {
        System.out.println("digital output wrong address, retrying...");
        continue;
      }
      else {
        break;
      }
    }
  }

  public void doit() throws Exception {

    serCfg = new SerialConfig(devName);
    serCfg.setBitRate(BaudRate);
    serCfg.setDataBits(SerialConfig.LN_8BITS);
    serCfg.setStopBits(SerialConfig.ST_1BITS);
    serCfg.setParity(SerialConfig.PY_NONE);
    serCfg.setHandshake(SerialConfig.HS_NONE);
    //serCfg.setHandshake(SerialConfig.HS_CTSDTR); // works
    //serCfg.setHandshake(SerialConfig.HS_CTSRTS);
    //serCfg.setHandshake(SerialConfig.HS_DSRDTR);
    //serCfg.setHandshake(SerialConfig.HS_HARD_IN);
    //serCfg.setHandshake(SerialConfig.HS_HARD_OUT);  //bad
    //serCfg.setHandshake(SerialConfig.HS_SOFT_IN);
    //serCfg.setHandshake(SerialConfig.HS_SOFT_OUT);
    //serCfg.setHandshake(SerialConfig.HS_SPLIT_MASK);
    //serCfg.setHandshake(SerialConfig.HS_XONXOFF);

    serCfg.setTxLen(4096);
    serCfg.setRxLen(4096);
    p = new SerialPortLocal(serCfg);
    p.setDTR(true); // some devices need DTR set (like the laptop)
    p.setRTS(true); // wireless rs232 needs this
    p.setTimeoutRx(0);
    p.txDrain();
    p.rxFlush();

    System.out.println("p.getConfig().getTxLen() = " + p.getConfig().getTxLen());
    System.out.println("p.getConfig().getRxLen() = " + p.getConfig().getRxLen());

    //Thread.sleep(BetweenCommandTime);

    if (ChangeBaudRate) { // change baud rate
      byte[] data = new byte[4];
      data[0] = (byte) 0x78;
      data[1] = (byte) 0x00;
      data[2] = (byte) 0x01; // 0x01 change baud rate
      data[3] = (byte) 0x08; // 0x01 = 2400, 0x06 = 57600, 0x08 = 115200

      p.putData(data);

    }

    else {

      double[] Angles = new double[NumServos];
      double[] LastAngles = new double[NumServos];
      PositionServos(LastAngles);
      Thread.sleep(IntialCenteringWait);

      long StartTime = Calendar.getInstance().getTimeInMillis();
      System.out.println("StartTime = " + StartTime);

      long CurrentTime = -1;
      long ElapsedTime = -1;
      long NextTxTime = 0;
      for (int bl = 0; bl < MaxNumExamples; bl++) {

        System.gc();

        if (p.rxReadyCount() != 0) {
          System.out.println("rxReadCount = " + p.rxReadyCount());
        }

        /////////////////////
        // position servos //
        /////////////////////

        if (PrintExamples) {
          //System.out.print("INPUT\t");
          System.out.print(bl);
          System.out.print("\t");
          //System.out.print(Calendar.getInstance().getTime());
          //System.out.print("\t");
          //System.out.print(System.currentTimeMillis());
          //System.out.print("\t");
        }

        for (int t = 0; t < NumServos; t++) {
          //Angles[t] = (Math.random()- 0.5) * 2.0 * RangeOfMotion * 90.0;
          Angles[t] = LastAngles[t] + (Math.random() - 0.5) * 2.0 * StepSize * 90.0;
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
        for (int t = 0; t < NumServos; t++) {

          if (PrintExamples) {
            if (Angles[t] >= 0.0)
              System.out.print(" ");
            System.out.print( ( (int) (Angles[t] * 1000)) / 1000.0);
            if (t != NumServos - 1)
              System.out.print("\t");
          }

        }
        if (PrintExamples) {
          //System.out.println();
        }

        Thread.sleep(MovementDuration);


        ///////////////////
        // read voltages //
        ///////////////////

        byte[] data7bytes = new byte[7];
        double[][] VoltageData = new double[NumTrials][NumAnalogChannels];
        for (int t = 0; t < NumTrials; t++) {
          for (int c = 0; c < NumAnalogChannels; c++) {

            ///////////////////////////////////////////
            // set digital outputs to select channel //
            ///////////////////////////////////////////

            int index = c;
            int bit0 = index % 2;
            index = index / 2;
            int bit1 = index % 2;
            index /= 2;
            int bit2 = index % 2;
            index /= 2;
            int bit3 = index % 2;

            SetDigitalOutput(17, bit0);
            SetDigitalOutput(18, bit1);
            SetDigitalOutput(19, bit2);
            SetDigitalOutput(20, bit3);

            ///////////////////////////
            // read analog channels  //
            ///////////////////////////

            data7bytes[0] = (byte) 0x78;
            data7bytes[1] = (byte) 21;
            data7bytes[2] = (byte) 0x04;
            data7bytes[3] = (byte) 0;
            data7bytes[4] = (byte) 0;
            data7bytes[5] = (byte) 0;
            data7bytes[6] = (byte) 0;

            p.putData(data7bytes);

            long last_time = System.currentTimeMillis();
            boolean timed_out = false;
            while (p.rxReadyCount() < 4) {
              long elapsed_time = System.currentTimeMillis() - last_time;
              if (elapsed_time > TimeOutWaitPeriod) {
                timed_out = true;
                break;
              }
              Thread.sleep(SleepTime);
            }
            if (timed_out) {
              c--;
              if (ReportTimeOuts) {
                System.out.println("Error!  Timed Out During Sensing, Retrying...");
              }
              //p.rxFlush();
              continue;
            }
            int address = p.getByte();
            int port = p.getByte();
            int value_msb = p.getByte();
            int value_lsb = p.getByte();
            double volts = (value_msb * 256 + value_lsb) / 1024.0 * 5.0;

            VoltageData[t][c] = volts;
          }
          if (false) {
            System.out.print("AnalogVolts: \t");
            for (int c = 0; c < NumAnalogChannels; c++) {
              System.out.print( (int) (VoltageData[t][c] * 1000.0) / 1000.0);
              if (c != NumAnalogChannels - 1)
                System.out.print("\t ");
            }
            System.out.println();

          }
        }
        //System.out.print("\tOUTPUT");
        for (int c = 0; c < NumAnalogChannels; c++) {
          double sum;
          sum = 0.0;
          for (int t = 0; t < NumTrials; t++) {

            sum += VoltageData[t][c];
          }

          double AverageVolts = sum / NumTrials;
          System.out.print("\t" + (int) (AverageVolts * 100000) / 100000.0);

          double VarianceSum = 0.0;
          for (int t = 0; t < NumTrials; t++) {
            double difference = VoltageData[t][c] - AverageVolts;
            VarianceSum += difference * difference;
          }
          double VoltsSTD = Math.sqrt(VarianceSum / NumTrials);
          System.out.print("\t" + (int) (VoltsSTD * 100000) / 100000.0);
        }
        System.out.println();

      }
    }
  }
}
