package ncsa.d2k.modules.projects.dtcheng;
import ncsa.d2k.core.modules.*;
import java.util.*;

public class RS232WriteByte extends InputModule {

  private String  CommPort       = "COM1";
  public  void    setCommPort    (String  value) {       this.CommPort    = value;}
  public  String  getCommPort    ()              {return this.CommPort           ;}

  private int     BaudRate       = 9600;
  public  void    setBaudRate    (int     value) {       this.BaudRate    = value;}
  public  int     getBaudRate    ()              {return this.BaudRate           ;}

  private byte    EOLByte       = 10;
  public  void    setEOLByte    (byte    value) {       this.EOLByte    = value;}
  public  byte    getEOLByte    ()              {return this.EOLByte           ;}

  public String getModuleName() {
    return "RS232WriteByte";
  }
  public String getModuleInfo () {
    return "Outputs a stream of bytes read to the specified com port.";
  }

  public String getInputName(int index) {
    switch(index) {
      case 0:
        return "BytesToSend";
      default: return "NO SUCH INPUT!";
    }
  }
  public String[] getInputTypes () {
        String[] types = {"[B"};
    return types;
  }
  public String getInputInfo(int index) {
    switch (index) {
        case 0: return "An array of bytes to send to the RS232 device.";
      default: return "No such input";
    }
  }

  public String getOutputName(int index) {
    switch(index) {
      default: return "NO SUCH OUTPUT!";
    }
  }
  public String[] getOutputTypes () {
    String[] types = {};
    return types;
  }
  public String getOutputInfo (int index) {
    switch (index) {
      default: return "No such output";
    }
  }


  RS232Controller reader;
  int numBytesRead;
  int count = 0;

  public void beginExecution() {
    reader   = new RS232Controller(CommPort, BaudRate, EOLByte);
    count = 0;
    numBytesRead = 0;
  }

  public void endExecution() {
    reader.close();
  }

  public boolean isReady() {
    return true;
  }

  public void doit () throws Exception {
    byte value = reader.readByte();
    count++;
    pushOutput(new Integer(value), 0);
  }


}
