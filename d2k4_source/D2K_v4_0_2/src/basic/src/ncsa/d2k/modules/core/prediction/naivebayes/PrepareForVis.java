package ncsa.d2k.modules.core.prediction.naivebayes;

import ncsa.d2k.core.modules.*;

/**
 * Prepare a NaiveBayesModel for visualization.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class PrepareForVis extends DataPrepModule {

  public String[] getInputTypes() {
    String[] in = {"ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel"};
    return in;
  }

  public String[] getOutputTypes() {
    String[] out = {"ncsa.d2k.modules.core.prediction.naivebayes.NaiveBayesModel"};
    return out;
  }

  public String getInputInfo(int i) {
    return "A NaiveBayesModel module.";
  }

  public String getInputName(int i) {
    return "Naive Bayes Model";
  }

  public String getOutputInfo(int i) {
    return "A NaiveBayesModel module.";
  }

  public String getOutputName(int i) {
    return "Naive Bayes Model";
  }

  public String getModuleInfo() {
    return "<p>Overview: This module prepares a NaiveBayesModel for "+
        "visualization.  Many calculations that are needed by the visualization "+
        "are done here, before the model is visualized."+
        "<p>Detailed Description: This module determines which of the input "+
        "features of the training data are the best predictors of the output. "+
        "This is done by performing predictions on the training data and leaving "+
        "out one input feature each time.  Data structures to hold values for "+
        "the pie charts in NaiveBayesVis are also created here."+

        "<p>Data Type Restrictions: none"+
        "<p>Data Handling: The data structures used to display pie charts in "+
        "NaiveBayesVis are created by this module.  These structures are stored "+
        "in <i>Naive Bayes Model</i>.  The number of pie charts is "+
        "proportional to the number of discrete values in the inputs of the "+
        "training data set."+
        "<p>Scalability: This module will perform NxM predictions, "+
        "where N is the number of inputs and M is the number of training examples.";
  }

  public String getModuleName() {
    return "Prepare Model for Visualization";
  }

  public void doit() throws Exception {
    NaiveBayesModel nbm = (NaiveBayesModel)pullInput(0);
    nbm.setupForVis();
    nbm.setIsReadyForVisualization(true);
    pushOutput(nbm, 0);
  }
}
