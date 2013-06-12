package ncsa.d2k.modules.core.prediction.neuralnet;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.parameter.*;

/*
 @author pgroves
 */

public class BackPropModelGeneratorReentrant
    extends OrderedReentrantModule
    implements java.io.Serializable {

  //////////////////////
  //d2k Props
  ////////////////////
  /** if true, the last model made will be saved (and will show up
    in the models pop-up pane), if false, the model will not be saved
    here*/
  boolean makeModelAvailable = true;

  boolean debug = false;
  /////////////////////////
  /// other fields
  ////////////////////////

  /*the model that will be saved at the end of execution
   */
  public BackPropModel model;

  //////////////////////////
  ///d2k control methods
  ///////////////////////

  public boolean isReady() {
    return super.isReady();
  }

  public void endExecution() {
    wipeFields();
    super.endExecution();
  }

  public void beginExecution() {
    wipeFields();
    super.beginExecution();
  }

  public void wipeFields() {
  }

  public ModelModule getModel() {
    return model;
  }

  /////////////////////
  //work methods
  ////////////////////
  /*
   does it
   */
  public void doit() throws Exception {
    if (debug) {
      System.out.println(getAlias() + ":Firing");
    }
    ExampleTable trainData = (ExampleTable) pullInput(0);
    int[] outputs = trainData.getOutputFeatures();
    if (outputs == null || outputs.length == 0)
      throw new Exception(getAlias() + "Please select an output attribute.");
    int[] inputs = trainData.getInputFeatures();
    if (inputs == null || inputs.length == 0)
      throw new Exception(getAlias() + "Please select an intput attribute.");
    //if(trainData.isColumnScalar(outputs[0]))
    //   throw new Exception("Output feature must be nominal.");

    ParameterPoint params = (ParameterPoint) pullInput(1);
    model = new BackPropModel(trainData, params);
    if (model.trainingSuccess) {
      pushOutput(model, 0);
    }
    else {
      model = null;
      throw new Exception(this.getAlias() + ": A valid model was not able" +
                          " to be produced. No model pushed out.");
    }
    if (!makeModelAvailable)
      model = null;
  }

  ////////////////////////
  /// D2K Info Methods
  /////////////////////
  public String getModuleInfo() {
    return
        "<p><b>Overview</b>: This trains and outputs a feed-forward, multi-" +
        "layered" +
        " perceptron neural network. The training method is some flavor of" +
        " back-propagation (conjugate-gradient descent). There are many" +
        " user specified parameters that allow for a good deal of customizat" +
        "ion of the architecture and behavior of the neural network." +
        "</p><p><b>Detailed Description</b>:" +
        "A perceptron can be thought of as a gadget that has multiple input" +
        " pipes and produces one output." +
        " Every input pipe has an associated weight, and the " +
        "output is a function of the sum of the values in the inputs pipes"
        + " multiplied by the " +
        "weight of the pipe. The actual output is this" +
        " sum put through what is called an activation function." +
        "The idea behind a Multi-Layered Perceptron Neural Network such as" +
        " this one is to hook up the input features of the data set to some" +
        " perceptrons, and then have the outputs go to more perceptrons, and" +
        " so on, until finally all the perceptrons in the last layer feed " +
        "into a single perceptron per output feature. In this way, the output" +
        " of the output feature's perceptron is a very complicated function" +
        " of the input features. If the right weights for each perceptron are" +
        " chosen, then the values produced by the output perceptron should be" +
        " equivalent to the observed outputs in the training data. The learn" +
        "ing process of a neural net is trying to find the best weights by" +
        " testing data points and changing the weights based on how closely" +
        " the value from the output perceptron came to the value from the " +
        "training example. This is a <i>very</i> simplified explanation of" +
        " only the most basic principles of neural nets. To get a better " +
        "understanding of the underlying mathematics, see the references " +
        "below." +
        //BackPropParamSpaceGenerator.PARAM_DESCRIPTION+

        "<p><b>Data Type Restrictions</b>: This model can only use" +
        " numeric or boolean inputs and can make only DOUBLE predictions. " +
        " Any input or output features that do not meet these" +
        " criteria will be removed before training. If their removal" +
        " causes there to be either zero input features or zero output" +
        " features, the model building process will fail and no model" +
        " will be generated." +

        "</p><p><b>Data Handling</b>:This module will transform the " +
        "output features <i>in place</i>. Therefore, it is necessary to" +
        " pass this module a copy (such as made by the module CopyTable)" +
        " if the training data will be used anywhere else in the itinerary or" +
        " in situations such as cross-validation where the same data is" +
        " passed into this module multiple times." +

        "</p><p><b>Scalability</b>: Neural networks are notoriously slow, and" +
        " this one is no exception. Of particular importance to the training" +
        " time is the number of nodes, or perceptrons used. This means that" +
        " larger values for the parameters <u>Number Hidden Layers</u> and" +
        " <u>Number Nodes Per Layer XX</u> will slow down training" +
        " tremendously. As for data set size, large numbers of attributes" +
        " have a similar effect as a large number of nodes (as attributes are" +
        " treated as input/output nodes in the neural net). Each example is" +
        " used exactly once per <i>epoch</i>, so both of these quantities" +
        " have a linear influence on training time." +

        "</p><p><b>References</b>:<br>" +
        "Basic Introduction to Neural Nets:<br>" +
        "NN FAQ, Maintained by Warren Sarle of the SAS institute:" +
        "<a ref=ftp://ftp.sas.com/pub/neural/FAQ.html>" +
        "ftp://ftp.sas.com/pub/neural/FAQ.html</a><br>" +

        "Russel, S., Norvig, P. (1995) Artificial Intelligence: A Modern " +
        "Approach." +
        "Upper Saddle River, NJ: Prentice-Hall<br>" +

        "<br>Intermediate References (from the NN FAQ):<br>" +

        "Fausett, L. (1994), Fundamentals of Neural Networks: Architectures," +
        " Algorithms, and Applications, Englewood Cliffs, NJ: Prentice Hall." +

        "<br>Bishop, C.M. (1995). Neural Networks for Pattern Recognition, " +

        "Oxford: Oxford University Press";
  }

  public String getModuleName() {
    return "Back Prop Neural Net Generator";
  }

  public String[] getInputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.datatype.table.ExampleTable",
        "ncsa.d2k.modules.core.datatype.parameter.ParameterPoint"};
    return types;
  }

  public String getInputInfo(int index) {
    switch (index) {
      case 0:
        return "The examples to train on. This ExampleTable must have" +
            " it's input columns, output columns, and training set defined" +
            ".";
        //  Furthermore, the outputs must have been scaled to the app"+
        //"ropriate range using ScaleForNeuralNet;
      case 1:
        return "The parameter definitions. This can be generated by" +
            " and optimizer from the ParameterSpace definition created " +
            "by BackPropParamSpaceGenerator or manually defined by" +
            " BackPropParameterDefine";
      default:
        return "No such input";
    }
  }

  public String getInputName(int index) {
    switch (index) {
      case 0:
        return "Training Table";
      case 1:
        return "Parameter Point";
      case 2:
        return "";
      default:
        return "No such input";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "ncsa.d2k.modules.core.prediction.neuralnet.BackPropModel"};
    return types;
  }

  public String getOutputInfo(int index) {
    switch (index) {
      case 0:
        return "A back propagation neural network model built with " +
            "the input data and parameters.";
      case 1:
        return "";
      case 2:
        return "";
      default:
        return "No such output";
    }
  }

  public String getOutputName(int index) {
    switch (index) {
      case 0:
        return "Back Propagation Model";
      default:
        return "No such output";
    }
  }

  /**
   * Return a list of the property descriptions.
   * @return a list of the property descriptions.
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] pds = new PropertyDescription[2];
    pds[0] = new PropertyDescription("makeModelAvailable",
                                     "Make Model Available for Saving",
                                     "If true, the last model produced by this module during execution " +
                                     "will appear in the Generated Models Pane.");
    pds[1] = new PropertyDescription("debug",
                                     "Generate Verbose Output",
                                     "If true, the module will output to the console the internal data of the neural net. " +
                                     "To all but advanced users, this is probably only useful to determine when the module " +
                                     "is firing");
    return pds;
  }

  ////////////////////////////////
  //D2K Property get/set methods
  ///////////////////////////////
  public void setDebug(boolean b) {
    debug = b;
  }

  public boolean getDebug() {
    return debug;
  }

  public boolean getMakeModelAvailable() {
    return makeModelAvailable;
  }

  public void setMakeModelAvailable(boolean b) {
    makeModelAvailable = b;
  }
  /*
    public boolean get(){
   return ;
    }
    public void set(boolean b){
   =b;
    }
    public double  get(){
   return ;
    }
    public void set(double d){
   =d;
    }
    public int get(){
   return ;
    }
    public void set(int i){
   =i;
    }
    public String get(){
   return ;
    }
    public void set(String s){
   =s;
    }
   */
}