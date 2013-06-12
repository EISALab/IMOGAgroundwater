package ncsa.d2k.modules.core.prediction.decisiontree.continuous;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import java.text.*;

import ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTModel;
import ncsa.d2k.modules.core.prediction.decisiontree.ViewableDTNode;

public class DecisionTreeModel
    extends Model
    implements java.io.Serializable, ViewableDTModel {

  DecisionTreeNode decisionTree;

  public DecisionTreeModel(ExampleTable examples, DecisionTreeNode decisionTree) {
    super(examples);
    this.decisionTree = decisionTree;
  }

  public ViewableDTNode getViewableRoot() {
    return decisionTree;
  }

  public boolean scalarInput(int i) {
    return true;
  }

  public String[] getInputs() {
    return this.getInputFeatureNames();
  }

  public String[] getUniqueInputValues(int i) {
    return new String[] {
        "0", "1"};
  }

  public String[] getUniqueOutputValues() {
    return new String[] {
        "0", "1"};
  }

  public DecisionTreeNode getDecisionTreeRoot() {
    return decisionTree;
  }

  public double[] evaluate(ExampleTable testExampleSet, int e) throws Exception {

    DecisionTreeNode node = decisionTree;

    while (node.decomposition != null) {
      if (node.decomposition.evaluate(testExampleSet, e)) {
        node = node.childNode1;
      }
      else {
        node = node.childNode2;
      }
    }

    double[] outputs = null;
    try {
      outputs = node.model.evaluate(testExampleSet, e);
    }
    catch (Exception e2) {
      throw e2;
    }
    return outputs;
  }

  /*
    public void instantiate(int numInputs, int numOutputs, String [] inputNames, String [] outputNames,
                            DecisionTreeNode decisionTree) {
      this.numInputs = numInputs;
      this.numOutputs = numOutputs;
      this.inputNames = inputNames;
      this.outputNames = outputNames;
      this.decisionTree = decisionTree;
    }
   */

  void indent(int level) {
    for (int i = 0; i < level * 2; i++) {
      System.out.print(" ");
    }
  }

  public void printNodeModel(DecisionTreeNode node, int level, boolean leafNode,
                             int splitIndex) throws Exception {

    indent(level);

    if (leafNode)
      System.out.print("*");

    node.model.print(PrintOptions);

    // !!!
    if (node.examples != null) {

      ExampleTable examples = node.examples;
      int numExamples = examples.getNumRows();
      double outputValueSum = 0.0;
      double outputValueMin = Double.MAX_VALUE;
      double outputValueMax = Double.MIN_VALUE;
      for (int e = 0; e < numExamples; e++) {
        double outputValue = examples.getOutputDouble(e, 0);

        if (outputValue < outputValueMin)
          outputValueMin = outputValue;
        if (outputValue > outputValueMax)
          outputValueMax = outputValue;

        outputValueSum += outputValue;
      }
      double outputValueMean = outputValueSum / numExamples;
      double outputVarianceSum = 0.0;
      for (int e = 0; e < numExamples; e++) {
        double outputValue = examples.getOutputDouble(e, 0);
        double difference = outputValue - outputValueMean;
        double differenceSquared = difference * difference;

        outputVarianceSum += differenceSquared;
      }
      double outputSTD = Math.sqrt(outputVarianceSum / numExamples);

      System.out.print(" (min=" + Format.format(outputValueMin) +
                       " max=" + Format.format(outputValueMax) +
                       " std=" + Format.format(outputSTD) +
                       " d=" + node.depth +
                       // " index=" + node.index +
                       ")" +
                       " [P:" + numExamples +
                       " ERR:" + Format.format(node.error / numExamples));
      if (!leafNode) {
        System.out.print(" ERR_REDUCT:" +
                         Format.format(node.bestErrorReduction / numExamples) +
                         " FEATURE:" + this.getInputFeatureName(splitIndex));
      }
      System.out.println("]");
    }
    else {
      System.out.println("  [Pop = " + node.numExamples + "]");
    }
  }

  public void printTree(DecisionTreeNode node, int level) throws Exception {

    if (node.decomposition != null) {
      int splitIndex = node.decomposition.inputIndex;
      double splitValue = node.decomposition.value;

      if (PrintOptions.PrintInnerNodeModels) {
        printNodeModel(node, level, false, splitIndex);
      }

      String testString = null;
      if (PrintOptions.AsciiInputs) {
        if (PrintOptions.EnumerateSplitValues) {
          int[] byteCounts = new int[256];
          ExampleTable examples = node.examples;
          int numExamples = examples.getNumRows();
          for (int e = 0; e < numExamples; e++) {
            int byteValue = (int) examples.getInputDouble(e, splitIndex);
            if (byteValue < splitValue)
              byteCounts[byteValue]++;
          }
          testString = "(" + this.getInputFeatureName(splitIndex) + " = {";
          boolean firstTime = true;
          for (int i = 0; i < 256; i++) {
            if (byteCounts[i] > 0) {
              if (!firstTime) {
                testString += "|";
              }

              testString += (char) i;

              firstTime = false;
            }
          }
          testString += "})";
        }
        else {
          testString = "(" + this.getInputFeatureName(splitIndex) + " > " +
              ( (char) splitValue) + ")";
        }
      }
      else {
        testString = "(" + this.getInputFeatureName(splitIndex) + " > " +
            Format.format(splitValue) + ")";
      }

      indent(level);
      System.out.println("If " + testString);

      indent(level + 1);
      System.out.println("Then");
      printTree(node.childNode1, level + 2);
      indent(level + 1);
      System.out.println("Else" + "  // not" + testString);
      printTree(node.childNode2, level + 2);
    }
    else {
      printNodeModel(node, level, true, -1);
    }

  }

  ModelPrintOptions PrintOptions = null;
  DecimalFormat Format = new DecimalFormat();
  public void print(ModelPrintOptions printOptions) throws Exception {
    PrintOptions = printOptions;

    Format.setMaximumFractionDigits(PrintOptions.MaximumFractionDigits);

    System.out.println("Decision Tree:");
    printTree(this.decisionTree, 0);
  }

}