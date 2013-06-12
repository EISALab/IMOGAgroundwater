package ncsa.d2k.modules.projects.dtcheng;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import java.io.*;
import java.util.*;
import java.text.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;

public class Object2DArrayToFloatExampleSet extends ComputeModule {

  public boolean UseSingleOutputFeatureNumber = true;
  public void    setUseSingleOutputFeatureNumber (boolean value) {
    this.UseSingleOutputFeatureNumber =  value;
  }
  public boolean getUseSingleOutputFeatureNumber () {
    return this.UseSingleOutputFeatureNumber;
    }

  public boolean UseFeatureTypeString = false;
  public void    setUseFeatureTypeString (boolean value) {
    this.UseFeatureTypeString         =  value;
  }
  public boolean getUseFeatureTypeString () {
    return this.UseFeatureTypeString;
    }

  private String FeatureTypeString     = "IO";
  public  void    setFeatureTypeString (String value) {       this.FeatureTypeString       = value;}
  public  String getFeatureTypeString ()              {return this.FeatureTypeString;}


  private int        OutputFeatureNumber = 1;
  public  void    setOutputFeatureNumber (int value) {       this.OutputFeatureNumber = value;}
  public  int     getOutputFeatureNumber ()          {return this.OutputFeatureNumber;}


  public String getModuleInfo() {
    return "Object2DArrayToFloatExampleSet";
  }
  public String getModuleName() {
    return "Object2DArrayToFloatExampleSet";
  }

  public String getInputName(int index) {
    switch(index) {
      case 0:  return "Object2DArray";
      default: return "Error! No such input.  ";
    }
  }
  public String getInputInfo (int index) {
    switch (index) {
      case 0:  return "Object2DArray";
      default: return "Error! No such input.  ";
    }
  }
  public String[] getInputTypes() {
    String[] types = {"[[Ljava.lang.Object;"};
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:  return "ContinuousExampleSet";
      default: return "Error! No such output.  ";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case 0:  return "ExampleSet";
      default: return "Error! No such output.  ";
    }
  }
  public String[] getOutputTypes() {
    String [] out = {"ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return out;
  }

  public void doit() throws Exception {

    Object  object = (Object) this.pullInput(0);

    Object [] objectArray1 = (Object []) object;
    Object [] objectArray2 = (Object []) objectArray1[0];

    int numDataRows    = objectArray1.length;
    int numDataColumns = objectArray2.length;

    System.out.println("numDataRows    = " + numDataRows);
    System.out.println("numDataColumns = " + numDataColumns);


    int numExamples = numDataRows - 1;


    int numInputs  = 0;
    int numOutputs = 0;
    int     [] inputColumnIndices  = new int[numDataColumns];
    int     [] outputColumnIndices = new int[numDataColumns];

    if (UseFeatureTypeString) {
      byte [] featureTypeBytes = null;
      featureTypeBytes = FeatureTypeString.getBytes();
      int numFeatureSelectionColumns = featureTypeBytes.length;
      for (int i = 0; i < numFeatureSelectionColumns; i++) {
        switch (featureTypeBytes[i]) {
        case (byte) 'I':
        case (byte) 'i':
          inputColumnIndices[numInputs++] = i;
          break;
        case (byte) 'O':
        case (byte) 'o':
          outputColumnIndices[numOutputs++] = i;
          break;
      }
      }
    }

    if (UseSingleOutputFeatureNumber) {
      for (int i = 0; i < numDataColumns; i++) {
        if ((i + 1) == OutputFeatureNumber) {
          outputColumnIndices[numOutputs++] = i;
        }
        else {
          inputColumnIndices[numInputs++] = i;
        }
      }
    }

    int numFeatures = numInputs + numOutputs;

    System.out.println("numExamples........ " + numExamples);
    System.out.println("numFeatures... " + numFeatures);
    System.out.println("numInputs... " + numInputs);
    System.out.println("numOutputs.. " + numOutputs);



    double [] data = new double[numExamples * numFeatures];

    double value = 0.0;

    int example_index = 0;


    String [] columnNames = new String[numDataColumns];
    object = (Object []) objectArray1[0];
    for (int f = 0; f < numDataColumns; f++) {
      columnNames[f] = ((Object []) (object))[f].toString();
    }

    double [] columnValues = new double[numDataColumns];
    for (int e = 0; e < numExamples; e++) {

      object = (Object []) objectArray1[e + 1];

      for (int f = 0; f < numDataColumns; f++) {
        Float floatValue = new Float(((Object []) (object))[f].toString());
        columnValues[f] = floatValue.floatValue();
      }

      for (int i = 0; i < numInputs; i++)
        data[e * numFeatures + i] = (double) columnValues[inputColumnIndices[i]];
      for (int i = 0; i < numOutputs; i++)
        data[e * numFeatures + numInputs + i] = (double) columnValues[outputColumnIndices[i]];

    }


    String [] inputNames  = new String[numInputs];
    String [] outputNames = new String[numOutputs];

    for (int i = 0; i < numInputs; i++) {
      inputNames[i] = columnNames[inputColumnIndices[i]];
    }
    for (int i = 0; i < numOutputs; i++) {
      outputNames[i] = columnNames[outputColumnIndices[i]];
    }


    ContinuousDoubleExampleTable exampleSet = new ContinuousDoubleExampleTable(data, numExamples, numInputs, numOutputs, inputNames, outputNames);
    this.pushOutput((ExampleTable) exampleSet, 0);

  }



}