package ncsa.d2k.modules.projects.dtcheng.image;

import ncsa.d2k.core.modules.ComputeModule;
import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;

public class DownSampleImage
    extends ComputeModule {

  private int HistorySegmentNumber = 0;
  public void setHistorySegmentNumber(int value) {
    this.HistorySegmentNumber = value;
  }

  public int getHistorySegmentNumber() {
    return this.HistorySegmentNumber;
  }

  private int OldXSize = 320;
  public void setOldXSize(int value) {
    this.OldXSize = value;
  }

  public int getOldXSize() {
    return this.OldXSize;
  }

  private int OldYSize = 260;
  public void setOldYSize(int value) {
    this.OldYSize = value;
  }

  public int getOldYSize() {
    return this.OldYSize;
  }

  private int NewXSize = 2;
   public void setNewXSize(int value) {
     this.NewXSize = value;
   }

   public int getNewXSize() {
     return this.NewXSize;
   }

   private int NewYSize = 2;
   public void setNewYSize(int value) {
     this.NewYSize = value;
   }

   public int getNewYSize() {
     return this.NewYSize;
   }

  public String getModuleName() {
    return "DownSampleImage";
  }

  public String getModuleInfo() {
    return "DownSampleImage";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "UnsupervisedExample";
    }
    return "No such input";
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ncsa.d2k.modules.projects.dtcheng.UnsupervisedExample";
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.UnsupervisedExample"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "UnsupervisedExample";
      default:
        return "No such output";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "UnsupervisedExample";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.UnsupervisedExample",
    };
    return types;
  }

  public void doit() throws Exception {

    UnsupervisedExample example = (UnsupervisedExample)this.pullInput(0);

    int[] OldHistory = example.history;
    double[] OldValues = example.values;

    int HistorySegmentIndex = HistorySegmentNumber - 1;

    int HeadSize = 0;
    for (int i = 0; i < HistorySegmentIndex; i++) {
      HeadSize += OldHistory[i];
    }

    int OldBodySize = OldHistory[HistorySegmentIndex];

    int TailSize = 0;
    for (int i = HistorySegmentIndex + 1; i < OldHistory.length; i++) {
      TailSize += OldHistory[i];
    }

    int NumBands = 3;
    int OldFlatDataSize = OldXSize * OldYSize * NumBands;
    int NewFlatDataSize = NewXSize * NewYSize * NumBands;

    int NewBodySize = NewFlatDataSize;

    if (OldFlatDataSize != OldBodySize) {
      System.out.println("Error!  OldFlatDataSize != OldBodySize");
    }

    double [] NewValues = new double[HeadSize + NewBodySize + TailSize];
    int [] NewHistory = new int[OldHistory.length];
    for (int i = 0; i < OldHistory.length; i++) {
      NewHistory[i] = OldHistory[i];
    }
    NewHistory[HistorySegmentIndex] = NewFlatDataSize;

    for (int i = 0; i < HeadSize; i++) {
      NewValues[i] = OldValues[i];
    }

    int[] Counts = new int[NewFlatDataSize];
    for (int i = 0; i < OldXSize; i++) {
      for (int j = 0; j < OldYSize; j++) {
        for (int k = 0; k < NumBands; k++) {

          int OldIndex = i * OldYSize * NumBands + j * NumBands + k;
          double OldValue = OldValues[OldIndex + HeadSize];

          int NewI = i / (OldXSize / NewXSize);
          int NewJ = j / (OldYSize / NewYSize);

          int NewIndex = NewI * NewYSize * NumBands + NewJ * NumBands + k;
          try {
            NewValues[NewIndex + HeadSize] += OldValue;
          } catch (Exception e) {
            System.out.println("boom");
          }
          Counts[NewIndex]++;
        }
      }
    }

    for (int i = HeadSize; i < HeadSize + NewFlatDataSize; i++) {
      NewValues[i] = NewValues[i] / Counts[i - HeadSize];;
    }

    for (int i = 0; i <  TailSize; i++) {
      NewValues[HeadSize + NewBodySize + i] = OldValues[HeadSize + OldBodySize + i];
    }

    UnsupervisedExample NewUnsupervisedExample = new UnsupervisedExample(NewValues, NewHistory);

    this.pushOutput(NewUnsupervisedExample, 0);

  }
}
