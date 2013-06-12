package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.core.modules.*;
import java.util.*;

public class RS232CreatePortController
    extends InputModule {

  private String CommPort = "COM1";
  public void setCommPort(String value) {
    this.CommPort = value;
  }

  public String getCommPort() {
    return this.CommPort;
  }

  private int BaudRate = 9600;
  public void setBaudRate(int value) {
    this.BaudRate = value;
  }

  public int getBaudRate() {
    return this.BaudRate;
  }

  private byte EOLByte = 10;
  public void setEOLByte(byte value) {
    this.EOLByte = value;
  }

  public byte getEOLByte() {
    return this.EOLByte;
  }

  public String getModuleName() {
    return "RS232CreatePortController";
  }

  public String getModuleInfo() {
    return "This modules creates an RS232 port controller for the specified com port and com settings.";
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
    PortController.close();
  }

  public void doit() throws Exception {
    PortController = new RS232Controller(CommPort, BaudRate, EOLByte);
    pushOutput(PortController, 0);
  }

}