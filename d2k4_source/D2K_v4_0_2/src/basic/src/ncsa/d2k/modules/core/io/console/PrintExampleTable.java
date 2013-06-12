package ncsa.d2k.modules.core.io.console;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;

public class PrintExampleTable
    extends OutputModule {

  int numProperties = 4;

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numProperties];

    pds[0] = new PropertyDescription(
        "label",
        "Label",
        "The label printed as a prefix to each report line");

    pds[1] = new PropertyDescription(
        "printFeatureNames",
        "Print Feature Names",
        "Report the name and number of each input and output feature");

    pds[2] = new PropertyDescription(
        "printNumExamples",
        "Print Num Examples",
        "Report the number of examples in the example table");

    pds[3] = new PropertyDescription(
        "printExampleValues",
        "Print Example Values",
        "Report the feature values for each example in the example table");

    return pds;
  }





  private String Label = "Print Example Table Label:  ";
  public void setLabel(String value) {
    this.Label = value;
  }

  public String getLabel() {
    return this.Label;
  }

  private boolean PrintFeatureNames = true;
  public void setPrintFeatureNames(boolean value) {
    this.PrintFeatureNames = value;
  }

  public boolean getPrintFeatureNames() {
    return this.PrintFeatureNames;
  }

  private boolean PrintNumExamples = true;
  public void setPrintNumExamples(boolean value) {
    this.PrintNumExamples = value;
  }

  public boolean getPrintNumExamples() {
    return this.PrintNumExamples;
  }

  private boolean PrintExampleValues = true;
  public void setPrintExampleValues(boolean value) {
    this.PrintExampleValues = value;
  }

  public boolean getPrintExampleValues() {
    return this.PrintExampleValues;
  }

  public String getModuleName() {
    return "Print Example Table";
  }

  public String getModuleInfo() {
    return "This module reports information the given example set.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Example Table";
    }
    return "";
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "The Example Table to report on";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return in;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Example Table";
    }
    return "";
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The Example Table that was input to the module without any modification";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return out;
  }

  public void doit() {

    ExampleTable exampleSet = (ExampleTable)this.pullInput(0);
    int numExamples = exampleSet.getNumRows();
    int numInputs  = exampleSet.getNumInputFeatures();
    int numOutputs = exampleSet.getNumOutputFeatures();

    if (PrintNumExamples)
      System.out.println(Label + " numExamples = " + numExamples);

    if (PrintFeatureNames) {
		 System.out.println(Label + " total num columns = " + 
		 	exampleSet.getNumColumns());
      System.out.println(Label + " numInputs = " + numInputs);
      for (int v = 0; v < numInputs; v++) {
        System.out.println(Label + "  " + (v + 1) + " : " +
                           exampleSet.getInputName(v));
      }
      System.out.println(Label + " numOutputs = " + numOutputs);
      for (int v = 0; v < numOutputs; v++) {
        System.out.println(Label + "  " + (v + 1) + " : " +
                           exampleSet.getOutputName(v));
      }
		if(exampleSet instanceof PredictionTable){
			PredictionTable pt = (PredictionTable)exampleSet;
			int numPreds = 0;
			if(pt.getPredictionSet() != null){
				numPreds = pt.getPredictionSet().length;
			}
      	System.out.println(Label + " numPredictions = " + numPreds);
	      for (int v = 0; v < numPreds; v++) {
   	     System.out.println(Label + "  " + (v + 1) + " : " +
                           pt.getColumnLabel(pt.getPredictionSet()[v]));
      	}
		}
			
    }

    if (PrintExampleValues) {
      for (int e = 0; e < numExamples; e++) {
        System.out.println(Label + "  e" + (e + 1));
        System.out.println(Label + "    input:");
        for (int v = 0; v < numInputs; v++) {
          System.out.println(Label + "    " + exampleSet.getInputName(v) +
                             " = " + exampleSet.getInputDouble(e, v));
        }
        System.out.println(Label + "    output:");
        for (int v = 0; v < numOutputs; v++) {
          System.out.println(Label + "    " + exampleSet.getOutputName(v) +
                             " = " + exampleSet.getOutputDouble(e, v));
        }
			if(exampleSet instanceof PredictionTable){
				PredictionTable pt = (PredictionTable)exampleSet;
				int numPreds = 0;
				if(pt.getPredictionSet() != null){
					numPreds = pt.getPredictionSet().length;
				}
		      for (int v = 0; v < numPreds; v++) {
					System.out.println(Label + "  " + 
						pt.getColumnLabel(pt.getPredictionSet()[v]) +
						" = " + exampleSet.getDouble(e, pt.getPredictionSet()[v]));
      		}
			}

      }
    }

    this.pushOutput(exampleSet, 0);
  }
}

