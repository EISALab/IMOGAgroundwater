package ncsa.d2k.modules.projects.dtcheng.primitive;

import ncsa.d2k.core.modules.*;

/**
 * User: redman
 * Date: Aug 28, 2003
 * Time: 11:14:33 AM
 * This module will push the first input it receives on input 0, but will not push subsequent inputs on 0
 * until it receives an input on one.
 */
public class WaitPushInc
    extends ComputeModule {

  protected boolean firstTime = true;
  protected boolean haveTrigger = false;

  /**
   * We are ready to run if we have received our first input on port zero or we have inputs on
   * zero and one.
   * @return true if we are ready to fire.
   */
  public boolean isReady() {
    if (firstTime && this.getFlags()[0] > 0) {
      return true;
    }
    else {
      if ((this.getFlags()[0] > 0) && haveTrigger)
        return true;
      if (this.getFlags()[1] > 0)
        return true;
    }
    return false;
  }

  /**
   * set the first execution flag.
   */
  public void beginExecution() {
    firstTime = true;
    haveTrigger = false;
  }

  /**
   * If first time, just pull input 0 and push it, subsequent runs, pull input 1, push it
   * and pull an input off the second input port, discard it.
   * @throws Exception
   */
  Object obj = null;

  public void doit() throws Exception {

    if (firstTime) {
      Object obj = this.pullInput(0);
      this.pushOutput(obj, 0);
      firstTime = false;
    }
    else {
      if (haveTrigger) {
        Object obj = this.pullInput(0);
        this.pushOutput(obj, 0);
        haveTrigger = false;
      }
      else  {
        this.pullInput(1);
        haveTrigger = true;
      }
    }

  }

  ////////////////////////
  /// D2K Info Methods
  /////////////////////

  public String getModuleInfo() {
    return "<p>      Overview: This module will collect the input received on it's first       input, save" +
        " it and push it one the first invocation. Subsequent inputs       received on the second input" +
        " will cause the input saved from the first       input to be pushed again.    </p>";
  }

  public String getModuleName() {
    return "Trigger Push";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.lang.Object", "java.lang.Object"};
    return types;
  }

  public String getInputInfo(int index) {
    switch (index) {
      case 0:
        return "The object to push.";
      case 1:
        return "The triggering object.";
      default:
        return "No such input";
    }
  }

  public String getInputName(int index) {
    switch (index) {
      case 0:
        return "Object";
      case 1:
        return "Trigger";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Object"};
    return types;
  }

  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        return "The object received on the first input is pushed after it is received the     first time, and after any input on the second input.";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int index) {
    switch (index) {
      case 0:
        return "Object";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

}