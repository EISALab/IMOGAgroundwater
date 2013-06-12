package ncsa.d2k.modules.core.prediction;

import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.table.*;


import ncsa.d2k.core.modules.*;

public class ApplyFunctionInducer
    extends OrderedReentrantModule {

  public String getModuleName() {
    return "Apply Function Inducer";
  }

  public String getModuleInfo() {
    return "This module applies a function inducer module to the given example table using the given error function to produce a model";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Function Inducer";
      case 1:
        return "Error Function";
      case 2:
        return "Examples";
      default:
        return "No such input";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The function inducer module used to generate the model";
      case 1:
        return "The error function used to guide the function inducer";
      case 2:
        return "The example table used for generating the model";
      default:
        return "No such input";
    }
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.prediction.FunctionInducerOpt",
        "ncsa.d2k.modules.core.prediction.ErrorFunction",
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"
    };
    return types;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Model";
      default:
        return "No such output";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The model generated from the example table and the error function";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.datatype.model.Model"};
    return types;
  }

  public void doit() throws Exception {

    ExampleTable exampleSet = null;
    //System.out.println("APPLYFUNCTION INDUCER: Entered doit");

    FunctionInducerOpt functionInducer = (FunctionInducerOpt)this.pullInput(0);
    ErrorFunction errorFunction = (ErrorFunction)this.pullInput(1);

    exampleSet = (ExampleTable)this.pullInput(2);
	
    //!!! do i need this?
    //exampleSet = (ExampleTable) exampleSet.copy();

    Model model = functionInducer.generateModel(exampleSet, errorFunction);

	//System.out.println("APPLYFUNCTION INDUCER: generated model");
    this.pushOutput(model, 0);
  }
}