package ncsa.d2k.modules.projects.dtcheng.generators;

import ncsa.d2k.core.modules.*;

public class GenerateString
    extends InputModule {

  private String StringData = "defaultStringValue";
  public void setStringData(String value) {
    this.StringData = value;
  }

  public String getStringData() {
    return this.StringData;
  }

  public String getModuleInfo() {
    return "This module outputs a single string which comes from the StringData     property.";
  }

  public String getModuleName() {
    return "GenerateString";
  }

  public String getInputName(int i) {
    switch (i) {
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      default:
        return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "OutputString";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.String"};
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "A string corresponding to the StringData property.  ";
      default:
        return "No such output";
    }
  }

  public void doit() {
    this.pushOutput(StringData, 0);
  }
}