package ncsa.d2k.modules.core.io.console;

import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.PropertyDescription;
import java.beans.PropertyVetoException;


public class PrintModel
    extends OutputModule {

  int numProperties = 5;

  public PropertyDescription[] getPropertiesDescriptions() {

    PropertyDescription[] pds = new PropertyDescription[numProperties];

    pds[0] = new PropertyDescription(
        "enabled",
        "Enabled",
        "Turns the reporting on and off");

    pds[1] = new PropertyDescription(
        "asciiInputs",
        "Ascii Inputs",
        "Report the integer feature values using alpha numerics corresponding to the ascii code");

    pds[2] = new PropertyDescription(
        "enumerateSplitValues",
        "Enumerate Split Values",
        "When reporting decision tree splits, list the unique values of the split feature going down each branch ");

    pds[3] = new PropertyDescription(
        "maximumFractionDigits",
        "Maximum Fraction Digits",
        "The number of digits after the decimal point to report when printing numbers");

    pds[4] = new PropertyDescription(
        "outputLabel",
        "Output Label",
        "The label printed as a prefix to each report line");

    return pds;
  }

  public boolean Enabled = true;
  public void setEnabled(boolean value) {
    this.Enabled = value;
  }

  public boolean getEnabled() {
    return this.Enabled;
  }

  public boolean AsciiInputs = false;
  public void setAsciiInputs(boolean value) {
    this.AsciiInputs = value;
  }

  public boolean getAsciiInputs() {
    return this.AsciiInputs;
  }

  public boolean EnumerateSplitValues = false;
  public void setEnumerateSplitValues(boolean value) {
    this.EnumerateSplitValues = value;
  }

  public boolean getEnumerateSplitValues() {
    return this.EnumerateSplitValues;
  }

  public boolean PrintInnerNodeModels = false;
  public void setPrintInnerNodeModels(boolean value) {
    this.PrintInnerNodeModels = value;
  }

  public boolean getPrintInnerNodeModels() {
    return this.PrintInnerNodeModels;
  }

  public int MaximumFractionDigits = 3;
  public void setMaximumFractionDigits(int value) throws PropertyVetoException {
    if (value < 0) {
      throw new PropertyVetoException(" < 0", null);
    }
    this.MaximumFractionDigits = value;
  }

  public int getMaximumFractionDigits() {
    return this.MaximumFractionDigits;
  }

  private String OutputLabel = "PrintModel: ";
  public void setOutputLabel(String value) {
    this.OutputLabel = value;
  }

  public String getOutputLabel() {
    return this.OutputLabel;
  }

  public String getModuleName() {
    return "Print Model";
  }

  public String getModuleInfo() {
    return "This module report the contents of a model based on the printing options in the module properties";
  }

  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.model.Model"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.model.Model"};
    return out;
  }

  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "Model";
    }
    return "";
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "Model to be reported";
    }
    return "";
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "Model";
    }
    return "";
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "The Model that was input to the module without any modification";
    }
    return "";
  }

  public void doit() throws Exception {
    ModelPrintOptions printOptions = null;
    if (Enabled) {
      printOptions = new ModelPrintOptions();

      printOptions.AsciiInputs = AsciiInputs;
      printOptions.EnumerateSplitValues = EnumerateSplitValues;
      printOptions.PrintInnerNodeModels = PrintInnerNodeModels;
      printOptions.MaximumFractionDigits = MaximumFractionDigits;
    }

    Model model = (Model)this.pullInput(0);

    if (Enabled) {
      synchronized (System.out) {
        System.out.println();
        System.out.println(OutputLabel + "(start)");
        model.print(printOptions);
        System.out.println();
        System.out.println(OutputLabel + "(finish)");
        System.out.println();
      }
    }

    this.pushOutput(model, 0);

  }
}
