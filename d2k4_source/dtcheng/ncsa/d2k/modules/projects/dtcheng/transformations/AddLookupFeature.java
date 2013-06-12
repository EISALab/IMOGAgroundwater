package ncsa.d2k.modules.projects.dtcheng.transformations;


import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import java.util.Hashtable;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;


public class AddLookupFeature extends ComputeModule {

  private int InputFeatureNumber = 1;
  public void setInputFeatureNumber(int value) {
    this.InputFeatureNumber = value;
  }


  public int getInputFeatureNumber() {
    return this.InputFeatureNumber;
  }


  private double OtherwiseValue = 0.0;
  public void setOtherwiseValue(double value) {
    this.OtherwiseValue = value;
  }


  public double getOtherwiseValue() {
    return this.OtherwiseValue;
  }


  private String NewFeatureName = "NewLookupFeature";
  public void setNewFeatureName(String value) {
    this.NewFeatureName = value;
  }


  public String getNewFeatureName() {
    return this.NewFeatureName;
  }


  private boolean CacheTransformation = false;
  public void setCacheTransformation(boolean value) {
    this.CacheTransformation = value;
  }


  public boolean getCacheTransformation() {
    return this.CacheTransformation;
  }


  private boolean DeleteUnmatchedExamples = false;
  public void setDeleteUnmatchedExamples(boolean value) {
    this.DeleteUnmatchedExamples = value;
  }


  public boolean getDeleteUnmatchedExamples() {
    return this.DeleteUnmatchedExamples;
  }


  public String getModuleInfo() {
    return "The module adds a new input feature using a example table to lookup the new value.  " +
        "The example table has one input and one output which defines the transformation";
  }


  public String getModuleName() {
    return "AddLookupFeature";
  }


  public String[] getInputTypes() {
    String[] in = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable",
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"
    };
    return in;
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ExampleSet that contains the data.";
      case 1:
        return "ExampleSet that contains the transformation.";
    }
    return "";
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "DataExampleSet";
      case 1:
        return "TransformExampleSet";
    }
    return "";
  }


  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
    return out;
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ExampleSet";
    }
    return "";
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ExampleSet";
    }
    return "";
  }


  boolean InitialExecution;
  public void beginExecution() {
    InitialExecution = true;
  }


  public boolean isReady() {
    boolean value = false;

    if (InitialExecution) {
      value = (getFlags()[0] > 0) && (getFlags()[1] > 0);
    }
    else {
      if (!CacheTransformation)
        value = (getFlags()[0] > 0) || (getFlags()[1] > 0);
      else
        value = (getFlags()[0] > 0);
    }

    return value;
  }


  ExampleTable TransformationExampleSet = null;

  public void doit() throws Exception {

    ExampleTable originalExampleSet = (ExampleTable)this.pullInput(0);

    if (InitialExecution || !CacheTransformation) {
      TransformationExampleSet = (ExampleTable)this.pullInput(1);
    }

    int originalNumExamples = originalExampleSet.getNumRows();
    int originalNumInputs = originalExampleSet.getNumInputFeatures();
    int originalNumOutputs = originalExampleSet.getNumOutputFeatures();

    int numTransformationExamples = TransformationExampleSet.getNumRows();

    // create hash table to store lookup values

    Hashtable hashTable = new Hashtable();

    for (int e = 0; e < numTransformationExamples; e++) {
      Double originalInputValue = new Double(TransformationExampleSet.getInputDouble(e, 0));
      Double transformedInputValue = new Double(TransformationExampleSet.getOutputDouble(e, 0));
      if (hashTable.containsKey(originalInputValue)) {
        System.out.println("Warning!  duplicate entry (" + originalInputValue + ") in transformation table");
      }
      else {
        hashTable.put(originalInputValue, transformedInputValue);
      }

    }

    // count number of unmatched examples if necessary
    int numUnmatchedExamples = 0;
    if (DeleteUnmatchedExamples) {
      for (int e = 0; e < originalNumExamples; e++) {

        Double originalInputValue = new Double(originalExampleSet.getInputDouble(e, InputFeatureNumber - 1));
        //System.out.println("example originalInputValue = " + originalInputValue.doubleValue());

        if (!hashTable.containsKey(originalInputValue)) {
          numUnmatchedExamples++;
        }

      }
      //System.out.println("originalNumExamples = " + originalNumExamples);
      //System.out.println("numUnmatchedExamples = " + numUnmatchedExamples);
    }

    int transformedNumExamples;

    if (DeleteUnmatchedExamples) {
      transformedNumExamples = originalNumExamples - numUnmatchedExamples;
    }
    else {
      transformedNumExamples = originalNumExamples;
    }

    int transformedNumInputs = originalExampleSet.getNumInputFeatures() + 1;

    String[] originalInputNames = originalExampleSet.getInputNames();
    String[] transformedInputNames = new String[transformedNumInputs];

    for (int i = 0; i < originalNumInputs; i++) {
      transformedInputNames[i] = originalInputNames[i];
    }
    transformedInputNames[originalNumInputs] = NewFeatureName;

    String[] outputNames = originalExampleSet.getOutputNames();
    ContinuousDoubleExampleTable transformedExampleSet = new ContinuousDoubleExampleTable(transformedNumExamples,
        transformedNumInputs,
        originalExampleSet.getNumOutputFeatures(),
        transformedInputNames,
        outputNames);

    int transformedExampleIndex = 0;
    for (int e = 0; e < originalNumExamples; e++) {

      MutableExample transformedExample = (MutableExample) transformedExampleSet.getExample(transformedExampleIndex);

      Double originalInputValue = new Double(originalExampleSet.getInputDouble(e, InputFeatureNumber - 1));

      boolean found = false;
      if (hashTable.containsKey(originalInputValue)) {
        found = true;
      }
      if (found || !DeleteUnmatchedExamples) {
        for (int i = 0; i < originalNumInputs; i++) {
          transformedExample.setInputDouble(i, originalExampleSet.getInputDouble(e, i));
        }

        if (found) {
          Double transformedValue = (Double) hashTable.get(originalInputValue);
          transformedExample.setInputDouble(originalNumInputs, transformedValue.doubleValue());
        }
        else {
          transformedExample.setInputDouble(originalNumInputs, OtherwiseValue);
        }

        for (int o = 0; o < originalNumOutputs; o++) {
          transformedExample.setOutputDouble(o, originalExampleSet.getOutputDouble(e, o));
        }

        transformedExampleIndex++;
      }
    }

    this.pushOutput(transformedExampleSet, 0);

    InitialExecution = false;

  }

}
