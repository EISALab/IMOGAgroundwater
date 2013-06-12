package ncsa.d2k.modules.core.io.console;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;

public class PrintExample
    extends OutputModule {

  int numProperties = 1;

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numProperties];

    pds[0] = new PropertyDescription(
        "label",
        "Label",
        "The label printed as a prefix to each report line");

    return pds;
  }




  private String Label = "";
  public void setLabel(String value) {
    this.Label = value;
  }

  public String getLabel() {
    return this.Label;
  }

  public String getModuleName() {
    return "Print Example";
  }

  public String getModuleInfo() {
    return "This module reports the values of the features of the given example.  ";
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Example";
    }
    return "";
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ncsa.d2k.modules.core.datatype.table.Example";
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.table.Example"};
    return in;
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Example";
    }
    return "";
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The Example that was input to the module without any modification";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.table.Example"};
    return out;
  }

  public void doit() {

    Example example = (Example)this.pullInput(0);
    int numInputs = ((ExampleTable)example.getTable()).getNumInputFeatures();
    int numOutputs = ((ExampleTable)example.getTable()).getNumOutputFeatures();

    System.out.println(Label + "    input:");
    for (int v = 0; v < numInputs; v++) {
      System.out.println(Label + "    " + ((ExampleTable)example.getTable()).getInputName(v) + " = " +
                         example.getInputDouble(v));
    }
    System.out.println(Label + "    output:");
    for (int v = 0; v < numOutputs; v++) {
      System.out.println(Label + "    " + ((ExampleTable)example.getTable()).getOutputName(v) + " = " +
                         example.getOutputDouble(v));
    }

    this.pushOutput(example, 0);
  }
}
