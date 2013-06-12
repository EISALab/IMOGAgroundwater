package ncsa.d2k.modules.core.prediction.mean.continuous;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.parameter.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;

public class MeanOutputInducerOpt extends FunctionInducerOpt {
  //int NumBiasParameters = 0;

  public String getModuleName() {
    return "Mean Output Inducer Optimizable";
  }

 public String getModuleInfo() {

    String s = "";
    s += "<p>";
    s += "Overview: ";
    s += "This module builds a simple model by computing the average for the output attribute.  </p>";
    s += "<p>";
    s += "Detailed Description: ";
    s += "The module implements the mean output learning algorithm.  ";
    s += "It produces a model that makes predictions that are independent of the input attribute values.  ";
    s += "During the training phase, the mean output inducer sums ";
    s += "up the output values for each output attribute and then divides by the number of examples.  ";
    s += "There are no control parameters to this learning algorithm and <i>Mean Output Inducer Optimizable</i> is added for uniformity.  ";
    s += "<p>";
    s += "Restrictions: ";
    s += "This module will only classify examples with numeric output attributes.";
    s += "<p>";
    s += "Data Handling: This module does not modify the input data. </p>";
    s += "<p>";
    s += "Scalability: This module can efficiently process a data set that can be stored in memory.  ";
    s += "The ultimate limit is how much virtual memory java can access. </p> ";

    return s;
  }

  public void setControlParameters(ParameterPoint point) throws Exception {
  }

  public void instantiateBiasFromProperties() {
    // Nothing to do in this case since properties are reference directly by the algorithm and no other control
    // parameters need be set.  This may not be the case in general so this stub is left open for future development.
  }


  public Model generateModel(ExampleTable examples, ErrorFunction errorFunction) throws Exception {

    int numExamples = examples.getNumRows();
    int numOutputs  = examples.getNumOutputFeatures();

    double [] outputSums = new double[numOutputs];

    for (int e = 0; e < numExamples; e++) {
      for (int f = 0; f < numOutputs; f++) {
        outputSums[f] += examples.getOutputDouble(e, f);
      }
    }

    for (int f = 0; f < numOutputs; f++) {
      outputSums[f] /= numExamples;
    }


    MeanOutputModel model = new MeanOutputModel(examples, outputSums);

    return (Model) model;
  }



    //QA Anca added this:
    public PropertyDescription[] getPropertiesDescriptions() {
        // so that "ordered and _trace" property are invisible
        return new PropertyDescription[0];
    }

}
