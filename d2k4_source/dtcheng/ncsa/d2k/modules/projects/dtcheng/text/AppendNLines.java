package ncsa.d2k.modules.projects.dtcheng.text;

import java.lang.System.*;
import ncsa.d2k.core.modules.ComputeModule;

public class AppendNLines
    extends ComputeModule {

  private int NumLinesToAppend = 1;
  public void setNumLinesToAppend(int value) {
    this.NumLinesToAppend = value;
  }

  public int getNumLinesToAppend() {
    return this.NumLinesToAppend;
  }

  public String getModuleInfo() {
    return "AppendNLines";
  }

  public String getModuleName() {
    return "AppendNLines";
  }

  public String[] getInputTypes() {
    String[] types = {
        "[B"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[B"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "[B";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "[B";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "[B";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "[B";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  byte[][] Lines;
  int NumLines;

  public void beginExecution() {
    Lines = new byte[NumLinesToAppend][];
    NumLines = 0;
  }

  public void doit() {
    byte[] line = (byte[])this.pullInput(0);

    if (line == null) {
      this.pushOutput(line, 0);
      NumLines = 0;
      return;
    }

    Lines[NumLines] = line;

    NumLines++;

    if (NumLines == NumLinesToAppend) {
      int totalLength = 0;
      for (int i = 0; i < NumLines; i++) {
        totalLength += Lines[i].length;
      }
      byte[] newLine = new byte[totalLength];

      int index = 0;
      for (int i = 0; i < NumLines; i++) {
        int length = Lines[i].length;
        for (int j = 0; j < length; j++) {
          newLine[index++] = Lines[i][j];
        }
      }

      this.pushOutput(newLine, 0);
      NumLines = 0;
    }

  }
}