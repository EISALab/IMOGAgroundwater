package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class MultiNRelevant
    extends ComputeModule {

  public String getModuleName() {
    return "MultiNRelevant";
  }

  public String getModuleInfo() {
    return "% This function determines the number of fundamentally identifiable" +
"%   parameters for a multinomial linearly-based model defined by the" +
"%   variables in X along with constants that is restricted via" +
"%   the kill_list and the scaled_list. NOTE: the X should NOT contain" +
"%   a column of ones for the constants. If a constant is not desired," +
"%   they should be set to zero via the kill_list." +
"%   The formula is:" +
"<p>" +
"% #_fundamental = (n_Xs + 1)*(n_options - 1) [the maximum number of unique parameters] <br>" +
"%                   - n_rows_kill_list [those set to constants] <br>" +
"%                   - n_rows_scaled_list (that dont involve the last option) [those which are merely repeats] <br>" +
"<p>" +
"% This is used to prepare for full_logit_LL.m." +
"<p>" +
"% USAGE:    n_relevant(kill_list,scaled_list,X,deez);" +
"<p>" +
"% kill_list                 a matrix that is used to set certain coefficients to arbitrary values. <br>" +
"%                           see full_logit_LL.m for an explanation" +
"% scaled_list               a matrix that is used to set certain coefficients to scalar multiples <br>" +
"%                           of other coefficients. see full_logit_LL.m for an explanation <br>" +
"% X                         the matrix containing the data but NOT containing a column of ones for the constant term" +
"% deez                      the matrix containing the outcome data. that is, if example #(row) chose option #(col) <br>" +
"%                           then deez(row,col) = 1, otherwise it is zero <br>" +
"<p>" +
"% written by Ricky Robertson <rdrobert@uiuc.edu> <rickyr@andrews.edu> <br>" +
"% started coding: 21 September ad MMIII <br>" +
"% original name: <laptop> c:\\rdrobert\\matlab_adventures\\multiB\\n_relevant.m <br>" +
"";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "KillList";
      case 1:
        return "ScaledList";
      case 2:
        return "NumberOfOptions";
      case 3:
        return "NumberOfXs";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "KillList";
      case 1:
        return "ScaledList";
      case 2:
        return "NumberOfOptions";
      case 3:
        return "NumberOfXs";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Integer",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "NumberOfRelevantParameters";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "NumberOfRelevantParameters";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.lang.Integer"};
    return types;
  }

  public void doit() throws Exception {

    MultiFormatMatrix KillList = (MultiFormatMatrix)this.pullInput(0);
    MultiFormatMatrix ScaledList = (MultiFormatMatrix)this.pullInput(1);
    int NumberOfOptions = ((Integer) this.pullInput(2)).intValue();
    int NumberOfXs = ((Integer) this.pullInput(3)).intValue();

    int LastOptionIndex = NumberOfOptions - 1;

    int NumRowsInKillList   = KillList.getDimensions()[0];
    int NumRowsInScaledList = ScaledList.getDimensions()[0];

    int NumEligibleRowsInScaledList = 0;

    if (NumRowsInScaledList > 0) {
      for (int RowIndex = 0; RowIndex < NumRowsInScaledList; RowIndex++) {
        if ( (ScaledList.getValue(RowIndex, 0) != LastOptionIndex) && (ScaledList.getValue(RowIndex, 2) != LastOptionIndex)) {
          NumEligibleRowsInScaledList++;
        }
      }
    }
    int result = (NumberOfXs + 1)*(NumberOfOptions - 1) - NumRowsInKillList - NumEligibleRowsInScaledList;

    this.pushOutput(new Integer(result), 0);
  }

}
