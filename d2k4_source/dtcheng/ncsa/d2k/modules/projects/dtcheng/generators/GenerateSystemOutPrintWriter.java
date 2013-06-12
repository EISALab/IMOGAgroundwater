package ncsa.d2k.modules.projects.dtcheng.generators;
import java.io.*;
import ncsa.d2k.core.modules.*;

public class GenerateSystemOutPrintWriter
    extends InputModule {

  public String getModuleInfo() {
    return "GenerateSystemOutPrintWriter";
  }

  public String getModuleName() {
    return "GenerateSystemOutPrintWriter";
  }

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.io.PrintWriter"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "null";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "null";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void doit() {

    PrintWriter writer = new PrintWriter((OutputStream) System.out);

    this.pushOutput(writer, 0);
  }
}
