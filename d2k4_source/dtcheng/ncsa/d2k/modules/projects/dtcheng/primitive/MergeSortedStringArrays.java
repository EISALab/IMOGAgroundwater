package ncsa.d2k.modules.projects.dtcheng.primitive;


import ncsa.d2k.core.modules.*;


public class MergeSortedStringArrays extends ComputeModule {

  public String getModuleName() {
    return "MergeSortedStringArrays";
  }


  public String getModuleInfo() {
    return "MergeSortedStringArrays";
  }


  public String[] getInputTypes() {
    String[] types = {
        "[S",
        "[S"};
    return types;
  }


  public String[] getOutputTypes() {
    String[] types = {
        "[S"};
    return types;
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Sorted String Array 1";
      case 1:
        return "Sorted String Array 2";
      default:
        return "No such input";
    }
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Sorted String Array 1";
      case 1:
        return "Sorted String Array 2";
      default:
        return "No such input";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "Merged Sorted String Array";
      default:
        return "No such output";
    }
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Merged Sorted String Array";
      default:
        return "No such output";
    }
  }


  public void doit() {

    String[] StringArray1 = (String[])this.pullInput(0);
    String[] StringArray2 = (String[])this.pullInput(1);

    int NumElements1 = StringArray1.length;
    int NumElements2 = StringArray2.length;

    String[] WorkMergedStringArray = new String[NumElements1 + NumElements2];

    int Index1 = 0;
    int Index2 = 0;
    int MergedIndex = 0;

    String String1 = null;
    String String2 = null;
    String CurrentString = null;
    String LastString = null;

    while (true) {

      if (Index1 < NumElements1) {
        String1 = StringArray1[Index1];
      }
      String2 = StringArray2[Index2];

      if ((Index1 < NumElements1) && (String2.compareTo(String1) > 0)) {
        CurrentString = String1;
        Index1++;
      }
      else {
        CurrentString = String2;
        Index2++;
      }

      if (!CurrentString.equals(LastString)) {
        WorkMergedStringArray[MergedIndex++] = CurrentString;
        LastString = CurrentString;
      }

      if ((Index1 == NumElements1) && (Index2 == NumElements2)) {
        break;
      }
    }

    int MergedNumElements = MergedIndex;

    String[] MergedStringArray = new String[MergedNumElements];

    System.arraycopy(WorkMergedStringArray, 0, MergedStringArray, 0, MergedNumElements);

    this.pushOutput(MergedStringArray, 0);
  }
}
