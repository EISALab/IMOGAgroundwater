package ncsa.d2k.modules.core.prediction;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.core.modules.ComputeModule;

public class GenerateFunctionInducer extends ComputeModule {

  public String getModuleName() {
    return "Generate Function Inducer";
  }
  public String getModuleInfo() {
    return "This module is used in the optimization of modules.  "  +
           "It takes as input the module (class) to be optimized and a point in control parameter space from the optimizer.  " +
           "Next it creates the module from the class, sets the module control parameters, and outputs the module for evaluation.  ";
  }

  public String getInputName(int i) {
    switch(i) {
      case  0: return "Function Inducer Opt Module";
      case  1: return "Control Parameter Point";
      default: return "No such input";
    }
  }
  public String getInputInfo(int i) {
    switch (i) {
      case  0: return "A class that defines a optimizable module";
      case  1: return "A point in control parameter space for defining the module behavior";
      default: return "No such input";
    }
  }
  public String[] getInputTypes() {
    String[] types = {
      "java.lang.Class",
      "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"
      };
    return types;
  }

  public String getOutputName(int i) {
    switch(i) {
      case  0: return "Function Inducer Module";
      default: return "No such output";
    }
  }
  public String getOutputInfo(int i) {
    switch (i) {
      case  0: return "A module that has its control parameters set and is ready to evaluate";
      default: return "No such output";
    }
  }
  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.prediction.FunctionInducerOpt"};
    return types;
  }

  boolean InitialExecution = true;
  public void beginExecution() {
    InitialExecution = true;
  }

  public boolean isReady() {
    boolean value = false;

    if (InitialExecution) {
      value = (this.getFlags()[0] > 0) && (this.getFlags()[1] > 0);
    }
    else {
      value = (this.getFlags()[1] > 0);
    }
    return value;
  }

  Class functionInducerClass = null;

  public void doit() throws Exception {

	//System.out.println("GENERATE FUNCTION INDUCER: ENTERED DOIT");
    if (InitialExecution) {
      functionInducerClass = (Class) this.pullInput(0);
      InitialExecution = false;
    }

    ParameterPoint parameterPoint = (ParameterPoint) this.pullInput(1);
	//System.out.println("GENERATE FUNCTION INDUCER: pulled parameter point " + parameterPoint);
    FunctionInducerOpt functionInducerOpt = null;

    try {
      functionInducerOpt = (FunctionInducerOpt) functionInducerClass.newInstance();
    }
    catch (Exception e) {
      System.out.println("could not create class");
      throw new Exception();
    }

    functionInducerOpt.setControlParameters(parameterPoint);

    this.pushOutput(functionInducerOpt, 0);
	//System.out.println("GENERATE FUNCTION INDUCER: pushed function inducer");
  }
}
