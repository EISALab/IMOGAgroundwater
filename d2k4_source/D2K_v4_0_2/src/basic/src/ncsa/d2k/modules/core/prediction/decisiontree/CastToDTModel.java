package ncsa.d2k.modules.core.prediction.decisiontree;

import ncsa.d2k.core.modules.*;

public class CastToDTModel extends ComputeModule {

	public String[] getInputTypes() {
		String[] in = {"ncsa.d2k.modules.PredictionModelModule"};
		return in;
	}

	public String[] getOutputTypes() {
		String[] out = {"ncsa.d2k.modules.core.prediction.decisiontree.DecisionTreeModel"};
		return out;
	}

	public String getInputInfo(int i) {
		return "Prediction Model";
	}

	public String getInputName(int i) {
		return "Prediction Model";
	}

	public String getOutputInfo(int i) {
		return "Decision Tree Model";
	}

	public String getOutputName(int i) {
		return "Decision Tree Model";
	}

	public String getModuleName() {
		return "CastToDTModel";
	}

	public String getModuleInfo() {
          String s = "<p> Overview: ";
          s += "This module converts a prediction model to a decision tree model. </p> ";
          s += "<p> Detailed Description: ";
          s += "A prediction model can be created by different algorithms, for example, ";
          s += "C45 decision tree, Rain Forest decision tree, Naive Bayes, etc. ";
          s += "This module converts a prediction model to a decision tree model. ";
          s += "After this conversion, the decision tree model can be visualized ";
          s += "and viewed using the decision tree visualizer and viewer. ";
          s += "<p> Restrictions: ";
          s += "Only the models that are compatible with the decision tree model ";
          s += "can be converted. For example, models created by the algorithm C45 ";
          s += "and RainForest can be converted to the decision tree model, but ";
          s += "models created by NaiveBayes cannot.";

          return s;
	}

	public void doit() {
		Object o = pullInput(0);
		pushOutput(o, 0);
	}
}