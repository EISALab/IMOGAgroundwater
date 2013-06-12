package ncsa.d2k.modules.projects.dtcheng.transformations;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.ComputeModule;
import java.util.Random;

public class RecodeExampleOutput
    extends ComputeModule {

  private int OutputFeatureNumber = 1;
  public void setOutputFeatureNumber(int value) {
    this.OutputFeatureNumber = value;
  }

  public int getOutputFeatureNumber() {
    return this.OutputFeatureNumber;
  }

  private double OriginalValue = 255.0;
  public void setOriginalValue(double value) {
    this.OriginalValue = value;
  }

  public double getOriginalValue() {
    return this.OriginalValue;
  }

  private double TranslatedValue = 1.0;
  public void setTranslatedValue(double value) {
    this.TranslatedValue = value;
  }

  public double getTranslatedValue() {
    return this.TranslatedValue;
  }

  public String getModuleInfo() {
    return "RecodeExampleOutput";
  }

  public String getModuleName() {
    return "RecodeExampleOutput";
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
      default:
        return "No such input";
    }
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
    }
    return "";
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ncsa.d2k.modules.core.datatype.table.ExampleTable";
    }
    return "";
  }

  ////////////////
  ///   WORK   ///
  ////////////////
  public void doit() {
    int outputFeatureIndex = OutputFeatureNumber - 1;

    ///////////////////////
    ///   PULL INPUTS   ///
    ///////////////////////

    ExampleTable examples = (ExampleTable)this.pullInput(0);

    int numExamples = examples.getNumRows();

    for (int e = 0; e < numExamples; e++) {
      double value = examples.getOutputDouble(e, outputFeatureIndex);
      if (value == OriginalValue) {
        //!!!examples.setOutput(e, outputFeatureIndex, TranslatedValue);
      }
    }

    ////////////////////////
    ///   PUSH OUTPUTS   ///
    ////////////////////////

    this.pushOutput(examples, 0);
  }

}