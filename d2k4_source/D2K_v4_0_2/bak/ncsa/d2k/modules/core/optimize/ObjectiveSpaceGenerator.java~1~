package ncsa.d2k.modules.core.optimize;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.parameter.basic.*;
import ncsa.d2k.core.modules.ComputeModule;

public class ObjectiveSpaceGenerator extends ComputeModule {

  int     numObjectiveDimensions = 1;

  private double  AccuracyMin = 0.0;
  public  void    setAccuracyMin (double value) {       this.AccuracyMin = value;}
  public  double  getAccuracyMin ()          {return this.AccuracyMin;}

  private double  AccuracyMax = 1.0;
  public  void    setAccuracyMax (double value) {       this.AccuracyMax = value;}
  public  double  getAccuracyMax ()          {return this.AccuracyMax;}

  public String getModuleName() {
    return "ObjectiveSpaceGenerator";
    }
  public String getModuleInfo() {
    return "ObjectiveSpaceGenerator";
    }

  public String getInputName(int i) {
    return "";
    }
  public String[] getInputTypes() {
    String [] in = {};
    return in;
    }

  public String getInputInfo(int i) {
    return "";
    }


  public String getOutputName(int i) {
    switch (i) {
      case 0: return "Objective Paramter Space";
      }
    return "";
    }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Control Paramter Space";
      }
    return "";
    }
  public String[] getOutputTypes() {
    String [] out = {"ncsa.d2k.modules.core.datatype.parameter.ParameterSpace"};
    return out;
    }



  public void doit() throws Exception {

    double [][] biasSpaceBounds = new double[2][numObjectiveDimensions];
    double []   defaults        = new double[numObjectiveDimensions];
    int    []   resolutions     = new int[numObjectiveDimensions];
    int    []   types           = new int[numObjectiveDimensions];
    String []   biasNames       = new String[numObjectiveDimensions];

    int errorFunctionIndex;
    String errorFunctionObjectFileName;

    int biasIndex = 0;

    biasNames         [biasIndex] = "Accuracy";
    biasSpaceBounds[0][biasIndex] = AccuracyMin;
    biasSpaceBounds[1][biasIndex] = AccuracyMax;
    defaults[biasIndex] = Double.NaN;
    resolutions[biasIndex] = Integer.MAX_VALUE;
    types[biasIndex] = ColumnTypes.DOUBLE;
    biasIndex++;


    ParameterSpaceImpl parameterSpace = new ParameterSpaceImpl();
    parameterSpace.createFromData(biasNames, biasSpaceBounds[0],  biasSpaceBounds[1], defaults, resolutions, types);

    this.pushOutput(parameterSpace,       0);
    }
  }
