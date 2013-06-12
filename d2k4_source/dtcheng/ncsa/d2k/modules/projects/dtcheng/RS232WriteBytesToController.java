package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.core.modules.*;
import java.util.*;

public class RS232WriteBytesToController
    extends InputModule {

  private int NumDataBytes = 1;
  public void setNumDataBytes(int value) {
    this.NumDataBytes = value;
  }

  public int getNumDataBytes() {
    return this.NumDataBytes;
  }

  private int DataByte1 = 0;
  public void setDataByte1(int value) {
    this.DataByte1 = value;
  }

  public int getDataByte1() {
    return this.DataByte1;
  }

  private int DataByte2 = 0;
  public void setDataByte2(int value) {
    this.DataByte2 = value;
  }

  public int getDataByte2() {
    return this.DataByte2;
  }

  private int DataByte3 = 0;
  public void setDataByte3(int value) {
    this.DataByte3 = value;
  }

  public int getDataByte3() {
    return this.DataByte3;
  }

  private int DataByte4 = 0;
  public void setDataByte4(int value) {
    this.DataByte4 = value;
  }

  public int getDataByte4() {
    return this.DataByte4;
  }

  private int DataByte5 = 0;
  public void setDataByte5(int value) {
    this.DataByte5 = value;
  }

  public int getDataByte5() {
    return this.DataByte5;
  }

  private int DataByte6 = 0;
  public void setDataByte6(int value) {
    this.DataByte6 = value;
  }

  public int getDataByte6() {
    return this.DataByte6;
  }

  public String getModuleName() {
    return "RS232WriteBytesToController";
  }

  public String getModuleInfo() {
    return "Outputs a stream of bytes to a given rs232 controller.";
  }

  public String getInputName(int index) {
    switch (index) {
      case 0:
        return "controller";
      default:
        return "error";
    }
  }

  public String getInputInfo(int index) {
    switch (index) {
      case 0:
        return "controller";
      default:
        return "error";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.RS232Controller"};
    return types;
  }

  public String getOutputName(int index) {
    switch (index) {
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputInfo(int index) {
    switch (index) {
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {};
    return types;
  }

  public void KillTime(double HowLong) {

    //System.out.println("HowLong = " + HowLong);
    long StartTime = System.currentTimeMillis();
    long ElapsedTime = 0;
    while (ElapsedTime < (long) (HowLong * 1000)) {
      ElapsedTime = System.currentTimeMillis() - StartTime;
    }
  }

  RS232Controller controller;

  public void doit() throws Exception {

    controller = (RS232Controller)this.pullInput(0);


    byte [] data = new byte[8];
    data[0] = 100;
    data[1] = 100;
    data[2] = 100;
    data[3] = 100;
    data[4] = 100;
    data[5] = 100;
    data[6] = 100;
    data[7] = 100;

    int count = 0;
    while (true) {
      count++;
        controller.writeBytes(data);
      Thread.sleep(10L);
      //System.out.println("count = " + count);
    }

  /*
    byte [] data = new byte[6];
    data[0] = (byte) 0x78;
    data[1] = (byte) 0x01;
    data[2] = (byte) 0x02;
    data[3] = (byte) 0x05;
    data[4] = (byte) 0x72;
    data[5] = (byte) 0x00;
    for (int j = 120; j <= 120; j++) {
      data[0] = (byte) j;
      for (int i = 0; i < 7; i++) {
        data[1] = (byte) (i + 1);
        controller.writeBytes(data);
        KillTime(0.1);
      }
      //KillTime(2.0);
    }
  */
  }

}