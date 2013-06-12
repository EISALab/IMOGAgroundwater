package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.*;
import java.util.Random;

public class DelayObjectStream extends ComputeModule {

  private int  DelayInterval = 1;
  public  void setDelayInterval (int value) {       this.DelayInterval = value;}
  public  int  getDelayInterval ()          {return this.DelayInterval;}


  public String getModuleInfo() {
    return "DelayObjectStream";
  }
  public String getModuleName() {
    return "DelayObjectStream";
  }

  public String[] getInputTypes() {
    String[] types = {"java.lang.Object"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {"java.lang.Object"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Object";
      default: return "No such input";
    }
  }

  public String getInputName(int i) {
    switch(i) {
      case 0:
        return "Object";
      default: return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Object";
      default: return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch(i) {
      case 0:
        return "Object";
      default: return "NO SUCH OUTPUT!";
    }
  }

  Object [] objectBuffer = new Object[1];
  int objectBufferFillIndex = 0;
  int objectBufferReadIndex = 0;
  int numObjectsInBuffer = 0;
  int count;
  public void beginExecution() {
    objectBufferFillIndex = 0;
    objectBufferReadIndex = 0;
    numObjectsInBuffer = 0;
    count = 0;
  }

  public void doit() {

    int objectBufferSize = DelayInterval;
    if (objectBuffer.length != objectBufferSize) {
      objectBuffer = new Object[objectBufferSize];
    }

    Object object = (Object) this.pullInput(0);

    if (object == null) {
      this.pushOutput(null, 0);
      beginExecution();
      return;
    }

    if (DelayInterval == 0) {
      this.pushOutput(object, 0);
    }
    else {
      while (numObjectsInBuffer >= DelayInterval) {
        this.pushOutput(objectBuffer[objectBufferReadIndex % objectBufferSize], 0);
        objectBufferReadIndex++;
        numObjectsInBuffer--;
      }
      objectBuffer[objectBufferFillIndex % objectBufferSize] = object;
      objectBufferFillIndex++;
      numObjectsInBuffer++;

      System.out.println("objectBufferFillIndex = " + objectBufferFillIndex);
      System.out.println("objectBufferReadIndex = " + objectBufferReadIndex);
      System.out.println("numObjectsInBuffer    = " + numObjectsInBuffer);
    }

  }
}