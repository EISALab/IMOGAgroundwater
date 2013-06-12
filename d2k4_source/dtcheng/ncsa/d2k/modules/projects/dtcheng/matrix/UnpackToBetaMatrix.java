package ncsa.d2k.modules.projects.dtcheng.matrix;

import java.lang.Math.*;
import ncsa.d2k.core.modules.*;
import Jama.Matrix;

public class UnpackToBetaMatrix
    extends ComputeModule {

  public String getModuleName() {
    return "UnpackToBetaMatrix";
  }

  public String getModuleInfo() {
    return
        "% This program unpacks a column vector of 'fundamental parameters' into " +
        "%   a matrix of betas (column = option # from 1 to n_options-1) according" +
        "%   to the desires specified in the kill_list and the scaled_list." +
        "<p>" +
        "% For use with full_logit_LL.m ." +
        "<p>" +
        "% WARNING!!! Not idiot-proofed at all." +
        "<p>" +
        "% USAGE: unpack_fundamental_to_betamatrix(fundamental_parameters,kill_list,scaled_list,n_options,n_Xs);" +
        "<p>" +
        "% fundamental_parameters    a column vector of parameters that are 'fundamental'; that is, not" +
        "%                           determined by the kill_list or scaled from other coefficients by" +
        "%                           the scaled_list<br>" +
        "% kill_list                 a matrix that is used to set certain coefficients to arbitrary values." +
        "%                           see full_logit_LL.m for an explanation<br>" +
        "% scaled_list               a matrix that is used to set certain coefficients to scalar multiples" +
        "%                           of other coefficients. see full_logit_LL.m for an explanation<br>" +
        "% n_options                 the number of different options that are being chosen between" +
        "% n_Xs                      the number of explanatory variables being used in the model _*EXCLUDING*_" +
        "%                           the constant term<br>" +
        "<p>" +
        "% written by Ricky Robertson <rdrobert@uiuc.edu> <rickyr@andrews.edu><br>" +
        "% started coding: 25 September ad MMIII<br>" +
        "% original name: <laptop> c:\\rdrobert\\matlab_adventures\\multiB\\unpack_fundamental_to_betamatrix.m<br>" +
        "";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "FundamentalParameters";
      case 1:
        return "KillList";
      case 2:
        return "ScaledList";
      case 3:
        return "NumberOfOptions";
      case 4:
        return "NumberOfXs";
      case 5:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "FundamentalParameters";
      case 1:
        return "KillList";
      case 2:
        return "ScaledList";
      case 3:
        return "NumberOfOptions";
      case 4:
        return "NumberOfXs";
      case 5:
        return "FormatIndex";
      default:
        return "Error!  No such input.  ";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
        "java.lang.Integer",
        "java.lang.Integer",
        "java.lang.Integer",
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "BetaMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "BetaMatrix";
      default:
        return "Error!  No such output.  ";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.projects.dtcheng.matrix.MultiFormatMatrix",
    };
    return types;
  }

  public void doit() throws Exception {

    MultiFormatMatrix FundamentalParameters = (MultiFormatMatrix)this.pullInput(0);
    MultiFormatMatrix KillList = (MultiFormatMatrix)this.pullInput(1);
    MultiFormatMatrix ScaledList = (MultiFormatMatrix)this.pullInput(2);
    int NumberOfOptions = ( (Integer)this.pullInput(3)).intValue();
    int NumberOfXs = ( (Integer)this.pullInput(4)).intValue();
    int FormatIndex = ( (Integer)this.pullInput(5)).intValue();

    int LastOptionIndex = NumberOfOptions - 1;

    int NumRowsInKillList = KillList.getDimensions()[0];
    int NumRowsInScaledList = ScaledList.getDimensions()[0];

    MultiFormatMatrix BetaMatrix = new MultiFormatMatrix(FormatIndex, new int[] {NumberOfXs + 1, NumberOfOptions - 1});

    int GrabbingIndex = 0;

    int done = 0;

    for (int OptionIndex = 0; OptionIndex < NumberOfOptions - 1; OptionIndex++) {
      for (int VariableIndex = 0; VariableIndex < NumberOfXs + 1; VariableIndex++) {

        done = 0;

        for (int KillIndex = 0; KillIndex < NumRowsInKillList; KillIndex++) {
          if ( (KillList.getValue(KillIndex, 0) == OptionIndex) &
              (KillList.getValue(KillIndex, 1) == VariableIndex)) {
            BetaMatrix.setValue(VariableIndex, OptionIndex, KillList.getValue(KillIndex, 2));
            done = 1;
          }

        }

        if (done == 0) {
          for (int ScaledRowIndex = 0; ScaledRowIndex < NumRowsInScaledList; ScaledRowIndex++) {
            if ( (ScaledList.getValue(ScaledRowIndex, 2) == OptionIndex) &&
                (ScaledList.getValue(ScaledRowIndex, 3) == VariableIndex)) {

             BetaMatrix.setValue(VariableIndex,
                                 OptionIndex,
                                 ScaledList.getValue(ScaledRowIndex, 4) *
                                 BetaMatrix.getValue(
                 (int) ScaledList.getValue(ScaledRowIndex, 1),
                 (int) ScaledList.getValue(ScaledRowIndex, 0)));
             done = 1;
           }
          }
        }

        if (done == 0) {
          BetaMatrix.setValue(VariableIndex, OptionIndex, FundamentalParameters.getValue(GrabbingIndex, 0));
          GrabbingIndex++;
        }

      }
    }

    for (int ScaledRowIndex = 0; ScaledRowIndex < NumRowsInScaledList; ScaledRowIndex++) {
      if (ScaledList.getValue(ScaledRowIndex, 2) == LastOptionIndex) {
        int RowToPull = (int) ScaledList.getValue(ScaledRowIndex, 1);
        int RowToReplace = (int) ScaledList.getValue(ScaledRowIndex, 3);
        int ColToPull = (int) ScaledList.getValue(ScaledRowIndex, 0);
        double SourceValue = BetaMatrix.getValue(RowToPull, ColToPull);
        for (int BetaColIndex = 0; BetaColIndex < LastOptionIndex; BetaColIndex++) {
          BetaMatrix.setValue(RowToReplace, BetaColIndex,
                              BetaMatrix.getValue(RowToReplace, BetaColIndex) - SourceValue);

        }
      }
    }

    this.pushOutput(BetaMatrix, 0);

  }
}