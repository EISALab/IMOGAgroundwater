package ncsa.d2k.modules.projects.dtcheng.bio;

import ncsa.d2k.modules.core.io.file.*;
import ncsa.d2k.modules.core.io.file.*;
import ncsa.d2k.core.modules.ComputeModule;

import java.util.*;
import java.io.*;

public class FindMotifsInSequences
    extends ComputeModule {

  private int UTROffset = 0;
  public void setUTROffset(int value) {
    this.UTROffset = value;
  }

  public int getUTROffset() {
    return this.UTROffset;
  }

  public String getModuleName() {
    return "FindMotifsInSequences";
  }

  public String getModuleInfo() {
    return "FindMotifsInSequences";
  }

  public String[] getInputTypes() {
    String[] types = {
        "java.io.PrintWriter",
        "[[B",
        "[S",
        "[[[I",
        "[S"
    };
    return types;
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Print Writer";
      case 1:
        return "UTRNames";
      case 2:
        return "UTRSequences";
      case 3:
        return "MotifBaseFrequencies";
      case 4:
        return "MotifNames";
    }
    return "";
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "A print writer used to route the results";
      case 1:
        return "UTRNames";
      case 2:
        return "UTRSequences";
      case 3:
        return "MotifBaseFrequencies";
      case 4:
        return "MotifNames";
    }
    return "";
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Clustering";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Object"
    };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Clustering";
      default:
        return "No such output";
    }
  }

  public void beginExecution() {
  }

  public void doit() throws Exception {

    int[] BaseToCompliment = new int[] {
        3, 2, 1, 0};

    PrintWriter out = (PrintWriter)this.pullInput(0);
    String[][] UTRNames = (String[][])this.pullInput(1);
    byte[][] UTRSequences = (byte[][])this.pullInput(2);
    String[] MotifNames = (String[])this.pullInput(3);
    int[][][] MotifBaseFrequencies = (int[][][])this.pullInput(4);

    int numSequences = UTRNames.length;
    int numMotifSequences = MotifNames.length;

    System.out.println("numSequences = " + numSequences);
    System.out.println("numMotifSequences = " + numMotifSequences);

    if (false) {
      for (int m = 0; m < numMotifSequences; m++) {

        //String sequence = new String(MotifSequences[m]);
        //System.out.println("motif = " + sequence);
        System.out.println("length = " + MotifBaseFrequencies[m].length);
      }

      for (int u = 0; u < numSequences; u++) {

        String sequence = new String(UTRSequences[u]);

        System.out.println("utr = " + sequence);
        System.out.println("length = " + UTRSequences[u].length);
      }
    }

    System.out.println();
    System.out.println();
    System.out.println();
    for (int u = 0; u < numSequences; u++) {

      System.out.println("UTRName(" + (u + 1) + ") = " + UTRNames[u][0]);

      int UTRSequenceNumBasePairs = UTRSequences[u].length;

      for (int m = 0; m < numMotifSequences; m++) {

        int[][] CurrentMotifBaseFrequencies = MotifBaseFrequencies[m];
        for (int d = 0; d < 2; d++) {

          int MotifSequenceNumBasePairs = MotifBaseFrequencies[m].length;
          //System.out.println("MotifSequenceNumBasePairs = " + MotifSequenceNumBasePairs);

          for (int UTRPositionIndex = 0;
               UTRPositionIndex < UTRSequenceNumBasePairs - MotifSequenceNumBasePairs + 1;
               UTRPositionIndex++) {

            boolean found = true;

            switch (d) {
              case 0:
                for (int motifPositionIndex = 0; motifPositionIndex < MotifSequenceNumBasePairs; motifPositionIndex++) {

                  if (CurrentMotifBaseFrequencies[motifPositionIndex][UTRSequences[u][UTRPositionIndex + motifPositionIndex]] == 0) {
                    found = false;
                    break;
                  }
                }

                if (found) {
                  System.out.println("D " +
                                     (UTRPositionIndex - UTROffset) +
                                     " to " +
                                     (UTRPositionIndex + MotifSequenceNumBasePairs - UTROffset - 1) +
                                     "  " + MotifNames[m]);
                }
                break;

              case 1:
                for (int motifPositionIndex = 0; motifPositionIndex < MotifSequenceNumBasePairs; motifPositionIndex++) {

                  if (CurrentMotifBaseFrequencies[MotifSequenceNumBasePairs - motifPositionIndex - 1]
                      [3 - UTRSequences[u][UTRPositionIndex + motifPositionIndex]] == 0) {
                    found = false;
                    break;
                  }
                }

                if (found) {
                  System.out.println("R " +
                                     (UTRPositionIndex - UTROffset) +
                                     " to " +
                                     (UTRPositionIndex + MotifSequenceNumBasePairs - UTROffset - 1) +
                                     "  " + MotifNames[m]);
                }
                break;
            }
          }
        }
      }
    }

    Object clustering = null;
    this.pushOutput(clustering, 0);
  }

}