package ncsa.d2k.modules.core.prediction.instancebased;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.core.modules.*;

public class InstanceBasedInducerOpt extends FunctionInducerOpt {

  int        NeighborhoodSize       = 1;
  double     DistanceWeightingPower = 0.0;
  double     ZeroDistanceWeight      = 1E-9;


  public String getModuleName() {
    return "Instance Based Inducer Optimizable";
  }
  public String getModuleInfo() {

    String s = "";
    s += "<p>";
    s += "Overview: ";
    s += "This module builds an instance based model from an example table. </p>";
    s += "<p>";
    s += "Detailed Description: ";
    s += "The module implements the instance based learning algorithm. ";
    s += "The instance based learning algorithm is also known as n-nearest neighbor or kernel density weighting.  ";
    s += "During the training phase, the instance based inducer simply memorizes (makes a copy of) the training example table.  ";
    s += "Given a target point in input space to classify, an instance based model first ";
    s += "finds the n (<i>Neighborhood Size</i>) nearest examples using Euclidean distance, and weights each example to make the final prediction.  ";
    s += "The formula used to weight each example is 1.0 / distance^<i>Distance Weighting Power</i>.  ";
    s += "Distance is Euclidean (square root of the sum of squared differences).  ";
    s += "To prevent division by zero, a constant weight (<i>Zero Distance Weight</i>) is assigned to any example with a zero distance.  ";
    s += "When <i>Distance Weighting Power</i> is 0.0, and <i>Zero Distance Value</i> is 1.0, every example in the neighborhood is given equal consideration.  ";
    s += "<p>";
    s += "Restrictions: ";
    s += "This module will only classify examples with numeric input and output attributes.";
    s += "<p>";
    s += "Data Handling: This module does not modify the input data. </p>";
    s += "<p>";
    s += "Scalability: This module can efficiently process a data set that can be stored in memory.  ";
    s += "The ultimate limit is how much virtual memory java can access. </p> ";
    s += "Model prediction speed can be increased by reducing <i>Neighborhood Size</i>.  ";

    return s;
  }


  public void setControlParameters(ParameterPoint parameterPoint) {

    NeighborhoodSize       = (int) parameterPoint.getValue(0);
    DistanceWeightingPower =       parameterPoint.getValue(1);
    ZeroDistanceWeight     =       parameterPoint.getValue(2);

  }

  public Model generateModel(ExampleTable examples, ErrorFunction errorFunction) {

    int numExamples = examples.getNumRows();
    int numInputs   = examples.getNumInputFeatures();
    double [] inputMins   = new double[numInputs];
    double [] inputMaxs   = new double[numInputs];
    double [] inputRanges = new double[numInputs];

    for (int v = 0; v < numInputs; v++) {
      inputMins[v] = Double.POSITIVE_INFINITY;
      inputMaxs[v] = Double.NEGATIVE_INFINITY;
    }


    for (int e = 0; e < numExamples; e++) {
      for (int v = 0; v < numInputs; v++) {
        double value = examples.getInputDouble(e, v);
        if (value < inputMins[v])
          inputMins[v] = value;
        if (value > inputMaxs[v])
          inputMaxs[v] = value;
      }
    }

    for (int v = 0; v < numInputs; v++) {
      inputRanges[v] = inputMaxs[v] - inputMins[v];
    }


    InstanceBasedModel model = new InstanceBasedModel(
        examples,
        inputRanges,
        NeighborhoodSize,
        DistanceWeightingPower,
        ZeroDistanceWeight,
        examples);

    return (Model) model;
  }

    //QA Anca added this:
    public PropertyDescription[] getPropertiesDescriptions() {
        // so that "ordered and _trace" property are invisible
        return new PropertyDescription[0];
    }

}
