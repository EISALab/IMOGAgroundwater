package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;


public class SelectBestAndWorst10 extends ComputeModule {
  private int OutputNumber = 1;
  public void setOutputNumber(int value) {this.OutputNumber = value;
  }


  public int getOutputNumber() {return this.OutputNumber;
  }


  public String getModuleInfo() {
    return "SelectBestAndWorst10";
  }


  public String getModuleName() {
    return "SelectBestAndWorst10";
  }


  public String[] getInputTypes() {
    String[] types = {"[S", "[[[D"};
    return types;
  }


  public String[] getOutputTypes() {
    String[] types = {"[S"};
    return types;
  }


  public String getInputInfo(int i) {
    switch (i) {
      case 0:
        return "ExampleLabels";
      case 1:
        return "ExampleSet";
      default:
        return "No such input";
    }
  }


  public String getInputName(int i) {
    switch (i) {
      case 0:
        return "ExampleLabels";
      case 1:
        return "ExampleSet";
      default:
        return "NO SUCH INPUT!";
    }
  }


  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "ExampleLabels";
      default:
        return "No such output";
    }
  }


  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "ExampleLabels";
      default:
        return "NO SUCH OUTPUT!";
    }
  }


  public void doit() {
    String[] exampleLabels = (String[])this.pullInput(0);
    double[][][] exampleSet = (double[][][])this.pullInput(1);

    int numExamples = exampleSet.length;
    int numInputs = exampleSet[0][0].length;
    int numOutputs = exampleSet[0][1].length;

    double[] exampleOutputs = new double[numExamples];
    int outputIndex = OutputNumber - 1;

    int[] bestIndices = new int[10];
    double[] bestScores = new double[10];
    for (int e = 0; e < numExamples; e++) {
      exampleOutputs[e] = exampleSet[e][1][outputIndex];
    }
    for (int i = 0; i < 10; i++) {
      bestScores[i] = Double.NEGATIVE_INFINITY;
      for (int e = 0; e < numExamples; e++) {
        if (exampleOutputs[e] > bestScores[i]) {
          bestScores[i] = exampleOutputs[e];
          bestIndices[i] = e;
        }
      }
      exampleOutputs[bestIndices[i]] = Double.NEGATIVE_INFINITY;
    }
    int[] worstIndices = new int[10];
    double[] worstScores = new double[10];
    for (int e = 0; e < numExamples; e++) {
      exampleOutputs[e] = exampleSet[e][1][outputIndex];
    }
    for (int i = 0; i < 10; i++) {
      worstScores[i] = Double.POSITIVE_INFINITY;
      for (int e = 0; e < numExamples; e++) {
        if (exampleOutputs[e] < worstScores[i]) {
          worstScores[i] = exampleOutputs[e];
          worstIndices[i] = e;
        }
      }
      exampleOutputs[worstIndices[i]] = Double.POSITIVE_INFINITY;
    }

    System.out.println("Best:");
    for (int i = 0; i < 10; i++) {
      System.out.println(exampleLabels[bestIndices[i]] + " " + bestScores[i]);
    }
    System.out.println("Worst:");
    for (int i = 0; i < 10; i++) {
      System.out.println(exampleLabels[worstIndices[i]] + " " + worstScores[i]);
    }

    String[] selectedLabels = new String[20];
    int index = 0;
    for (int i = 0; i < 10; i++) {
      selectedLabels[index++] = exampleLabels[bestIndices[i]];
    }
    for (int i = 0; i < 10; i++) {
      selectedLabels[index++] = exampleLabels[worstIndices[i]];
    }

    this.pushOutput(selectedLabels, 0);
  }
}
