package ncsa.d2k.modules.core.datatype.model;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.*;
public class Model extends PredictionModelModule implements java.io.Serializable {


  protected Model(ExampleTable examples) {
	super(examples);
  }

  protected Model(int trainingSetSize, String[] inputColumnLabels, String[] outputColumnLabels,
				  int[] inputFeatureTypes, int[] outputFeatureTypes) {
	super(trainingSetSize, inputColumnLabels, outputColumnLabels, inputFeatureTypes, outputFeatureTypes);
  }

  public double [] evaluate(ExampleTable exampleSet, int e) throws Exception {
	System.out.println("must override this method");
	throw new Exception();
  }

  public void evaluate(ExampleTable exampleSet, int e, double [] outputs) throws Exception {
	int numOutputs = exampleSet.getNumOutputFeatures();
	double [] internalOutputs = evaluate(exampleSet, e);
	for (int i = 0; i < numOutputs; i++) {
	  outputs[i] = internalOutputs[i];
	}
  }

  public void print(ModelPrintOptions options) throws Exception {
	System.out.println("must override this method");
	throw new Exception();
  }

  public int getNumInputFeatures() {
	return getInputColumnLabels().length;
  }

  //renaming

  public String[] getInputFeatureNames() {
	return getInputColumnLabels();
	}

  public String getInputFeatureName(int i) {
	return this.getInputColumnLabels()[i];
  }

  public int getNumOutputFeatures() {
	return getOutputColumnLabels().length;
  }

  public String[] getOutputFeatureNames() {
	return getOutputColumnLabels();
  }

  public String getOutputFeatureName(int i) {
	return this.getOutputColumnLabels()[i];
  }


  public void makePredictions(PredictionTable pt) {
	  int numOutputs = pt.getNumOutputFeatures();
	  double [] predictions = new double[numOutputs];
	  try {
		for (int i = 0 ; i < pt.getNumRows() ; i++) {
		  this.evaluate(pt, i, predictions);
		  for(int j = 0 ; j < numOutputs; j++) {
			pt.setDoublePrediction(predictions[j], i, j);
		  }
		}
	  } catch (Exception e) {}
  }

}