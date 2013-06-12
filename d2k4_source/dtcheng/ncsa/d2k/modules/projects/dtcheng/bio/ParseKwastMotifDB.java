package ncsa.d2k.modules.projects.dtcheng.bio;

import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.io.file.*;

public class ParseKwastMotifDB
    extends ComputeModule {

  private String Label = "";
  public void setLabel(String value) {
    this.Label = value;
  }

  public String getLabel() {
    return this.Label;
  }

  public String getModuleInfo() {
    return "ParseKwastMotifDB";
  }

  public String getModuleName() {
    return "ParseKwastMotifDB";
  }

  public String[] getInputTypes() {
    String[] types = {
        "[Ljava.lang.Object;"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ObjectArray";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ObjectArray";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "[S",
        "[[[I",
    };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "MotifNames";
      case 1:
        return "MotifBaseFrequencies";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "MotifNames";
      case 1:
        return "MotifBaseFrequencies";
      default:
        return "NO SUCH OUTPUT!";
    }
  }

  public void beginExecution() {

  }

  public void doit() {

    Object object = this.pullInput(0);

    if (object == null) {
      return;
    }

    Object[] ObjectArray = (Object[]) object;

    int numConsensusMotifs = ObjectArray.length;

    System.out.println("numConsensusMotifs = " + numConsensusMotifs);

    String[] MotifNames = new String[numConsensusMotifs];
    int[][][] MotifBaseFrequencies = new int[numConsensusMotifs][][];

    for (int i = 0; i < numConsensusMotifs; i++) {

      MotifNames[i] = ( (String[]) ObjectArray[i])[0];
      String sequence = ( (String[]) ObjectArray[i])[1];

      byte[] MotifBytes = sequence.getBytes();
      int MotifLength = MotifBytes.length;

      MotifBaseFrequencies[i] = new int[MotifLength][4];

      for (int s = 0; s < MotifLength; s++) {

        int base = MotifBytes[s];

        switch (base) {
          case 'A':
          case 'a':
            MotifBaseFrequencies[i][s][0] = 1; // A
            MotifBaseFrequencies[i][s][1] = 0; // C
            MotifBaseFrequencies[i][s][2] = 0; // G
            MotifBaseFrequencies[i][s][3] = 0; // T
            break;

          case 'C':
          case 'c':
            MotifBaseFrequencies[i][s][0] = 0; // A
            MotifBaseFrequencies[i][s][1] = 1; // C
            MotifBaseFrequencies[i][s][2] = 0; // G
            MotifBaseFrequencies[i][s][3] = 0; // T
            break;

          case 'G':
          case 'g':
            MotifBaseFrequencies[i][s][0] = 0; // A
            MotifBaseFrequencies[i][s][1] = 0; // C
            MotifBaseFrequencies[i][s][2] = 1; // G
            MotifBaseFrequencies[i][s][3] = 0; // T
            break;

          case 'T':
          case 't':
            MotifBaseFrequencies[i][s][0] = 0; // A
            MotifBaseFrequencies[i][s][1] = 0; // C
            MotifBaseFrequencies[i][s][2] = 0; // G
            MotifBaseFrequencies[i][s][3] = 1; // T
            break;

          case 'R':
          case 'r':
            MotifBaseFrequencies[i][s][0] = 1; // A
            MotifBaseFrequencies[i][s][1] = 0; // C
            MotifBaseFrequencies[i][s][2] = 1; // G
            MotifBaseFrequencies[i][s][3] = 0; // T
            break;

          case 'Y':
          case 'y':
            MotifBaseFrequencies[i][s][0] = 0; // A
            MotifBaseFrequencies[i][s][1] = 1; // C
            MotifBaseFrequencies[i][s][2] = 0; // G
            MotifBaseFrequencies[i][s][3] = 1; // T
            break;

          case 'M':
          case 'm':
            MotifBaseFrequencies[i][s][0] = 1; // A
            MotifBaseFrequencies[i][s][1] = 1; // C
            MotifBaseFrequencies[i][s][2] = 0; // G
            MotifBaseFrequencies[i][s][3] = 0; // T
            break;

          case 'K':
          case 'k':
            MotifBaseFrequencies[i][s][0] = 0; // A
            MotifBaseFrequencies[i][s][1] = 0; // C
            MotifBaseFrequencies[i][s][2] = 1; // G
            MotifBaseFrequencies[i][s][3] = 1; // T
            break;

          case 'W':
          case 'w':
            MotifBaseFrequencies[i][s][0] = 1; // A
            MotifBaseFrequencies[i][s][1] = 0; // C
            MotifBaseFrequencies[i][s][2] = 0; // G
            MotifBaseFrequencies[i][s][3] = 1; // T
            break;

          case 'S':
          case 's':
            MotifBaseFrequencies[i][s][0] = 0; // A
            MotifBaseFrequencies[i][s][1] = 1; // C
            MotifBaseFrequencies[i][s][2] = 1; // G
            MotifBaseFrequencies[i][s][3] = 0; // T
            break;

          case 'B':
          case 'b':
            MotifBaseFrequencies[i][s][0] = 0; // A
            MotifBaseFrequencies[i][s][1] = 1; // C
            MotifBaseFrequencies[i][s][2] = 1; // G
            MotifBaseFrequencies[i][s][3] = 1; // T
            break;

          case 'D':
          case 'd':
            MotifBaseFrequencies[i][s][0] = 1; // A
            MotifBaseFrequencies[i][s][1] = 0; // C
            MotifBaseFrequencies[i][s][2] = 1; // G
            MotifBaseFrequencies[i][s][3] = 1; // T
            break;

          case 'H':
          case 'h':
            MotifBaseFrequencies[i][s][0] = 1; // A
            MotifBaseFrequencies[i][s][1] = 1; // C
            MotifBaseFrequencies[i][s][2] = 0; // G
            MotifBaseFrequencies[i][s][3] = 1; // T
            break;

          case 'V':
          case 'v':
            MotifBaseFrequencies[i][s][0] = 1; // A
            MotifBaseFrequencies[i][s][1] = 1; // C
            MotifBaseFrequencies[i][s][2] = 1; // G
            MotifBaseFrequencies[i][s][3] = 0; // T
            break;

          case 'N':
          case 'n':
            MotifBaseFrequencies[i][s][0] = 1; // A
            MotifBaseFrequencies[i][s][1] = 1; // C
            MotifBaseFrequencies[i][s][2] = 1; // G
            MotifBaseFrequencies[i][s][3] = 1; // T
            break;

          default:
            System.out.println("Error motif code(" + (char) base + ") not recognized");
            break;

        }

      }

    }

    this.pushOutput(MotifNames, 0);
    this.pushOutput(MotifBaseFrequencies, 1);
  }

}