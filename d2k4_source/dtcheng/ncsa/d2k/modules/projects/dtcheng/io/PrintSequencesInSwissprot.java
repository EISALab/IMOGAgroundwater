package ncsa.d2k.modules.projects.dtcheng.io;

import java.util.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.io.file.*;

public class PrintSequencesInSwissprot
    extends OutputModule {

  public String getModuleInfo() {
    return "PrintSequencesInSwissprot";
  }

  public String getModuleName() {
    return "PrintSequencesInSwissprot";
  }

  public String[] getInputTypes() {
    String[] types = {
        "[[S",
        "[[B"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
    };
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ByteArray";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "SequenceNames";
      case 1:
        return "SequenceElements";
      default:
        return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "SequenceNames";
      case 1:
        return "SequenceElements";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      default:
        return "NO SUCH OUTPUT!";
    }
  }


  public void doit() {

    String[][] SequenceNames = (String[][])this.pullInput(0);
    byte[][] SequenceElements = (byte[][])this.pullInput(1);

    int NumSequences = SequenceNames.length;

    for (int s = 0; s < NumSequences; s++) {

      System.out.print("AC   ");
      for (int n = 0; n < SequenceNames[s].length; n++) {
        System.out.print(SequenceNames[s][n] + ";");
      }
      System.out.println();

      int NumElements = SequenceElements[s].length;

      System.out.println("SQ   SEQUENCE   " + NumElements + " AA;");
      int BlockIndex = 0;
      for (int i = 0; i < NumElements; i++) {
        if (i % 60 == 0) {
          System.out.print("     ");
        }
        System.out.print( (char) SequenceElements[s][i]);
        if ( (i + 1) % 10 == 0) {
          if (BlockIndex < 5) {
            System.out.print(" ");
          }
          if ( (BlockIndex + 1) % 6 == 0) {
            System.out.println();
            BlockIndex = -1;
          }
          BlockIndex++;
        }
      }
      if (NumElements % 60 != 0) {
        System.out.println("");
      }
      System.out.println("//");

    }
  }


}