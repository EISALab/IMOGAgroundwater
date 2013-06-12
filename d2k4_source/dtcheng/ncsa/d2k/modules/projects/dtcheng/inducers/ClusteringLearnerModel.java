package ncsa.d2k.modules.projects.dtcheng.inducers;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.table.*;
public class ClusteringLearnerModel extends Model implements java.io.Serializable
  {
  int         numClusters;
  int         minUserClusterIndex;
  int         maxNumClustersByRange;
  int []      rangeToInitialClusterIndex;
  double [][] clusterOutputSums;
  int    []   clusterNumExamples;
  double []   overallOutputSums;
  int         overallNumExamples;
  int         clusteringInputFeatureIndex;

  public ClusteringLearnerModel(ExampleTable examples, int numClusters, int minUserClusterIndex,
                                int maxNumClustersByRange, int [] rangeToInitialClusterIndex,
                                int [] clusterNumExamples, double [][] clusterOutputSums,
                                int    overallNumExamples, double []   overallOutputSums,
                                int clusteringInputFeatureIndex) {

    super(examples);

    this.numClusters = numClusters;
    this.minUserClusterIndex = minUserClusterIndex;
    this.maxNumClustersByRange = maxNumClustersByRange;
    this.rangeToInitialClusterIndex = rangeToInitialClusterIndex;
    this.clusterNumExamples = clusterNumExamples;
    this.clusterOutputSums  = (double [][]) clusterOutputSums.clone();
    this.overallNumExamples = overallNumExamples;
    this.overallOutputSums  = (double []) overallOutputSums.clone();
    this.clusteringInputFeatureIndex = clusteringInputFeatureIndex;

  }

  public double [] Evaluate(ExampleTable examples, int e) {
    double [] outputs = new double[examples.getNumOutputFeatures()];

    // assume input feature is nominal
    int rangeClusterIndex = (int) examples.getInputDouble(e, clusteringInputFeatureIndex) - minUserClusterIndex;

    boolean clusterInvalid = false;
    if ((rangeClusterIndex < 0) || (rangeClusterIndex >= maxNumClustersByRange))
    {
      clusterInvalid = true;
    }

    int initialClusterIndex = -1;
    if (!clusterInvalid)
    {
      initialClusterIndex = rangeToInitialClusterIndex[rangeClusterIndex];
    }

    if (initialClusterIndex == -1)
    {
      clusterInvalid = true;
    }

    if (clusterInvalid)
    {
      System.out.println("cluster invalid");
      for (int o = 0; o < examples.getNumOutputFeatures(); o++)
      {
        outputs[o] = overallOutputSums[o] / overallNumExamples;
      }
    }
    else
    {
      for (int o = 0; o < examples.getNumOutputFeatures(); o++)
      {
        outputs[o] = clusterOutputSums[initialClusterIndex][o] / clusterNumExamples[initialClusterIndex];
      }
    }

    return outputs;
  }

/*
  public void Instantiate(int numInputs, int numOutputs, String [] inputNames, String [] outputNames,
                          int numClusters, int minUserClusterIndex,
                          int maxNumClustersByRange, int [] rangeToInitialClusterIndex,
                          int [] clusterNumExamples, double [][] clusterOutputSums,
    int    overallNumExamples, double []   overallOutputSums,
    int clusteringInputFeatureIndex)
  {
    this.numInputs   = numInputs;
    this.numOutputs  = numOutputs;
    this.inputNames  = inputNames;
    this.outputNames = outputNames;
    this.numClusters = numClusters;
    this.minUserClusterIndex = minUserClusterIndex;
    this.maxNumClustersByRange = maxNumClustersByRange;
    this.rangeToInitialClusterIndex = rangeToInitialClusterIndex;
    this.clusterNumExamples = clusterNumExamples;
    this.clusterOutputSums  = (double [][]) clusterOutputSums.clone();
    this.overallNumExamples = overallNumExamples;
    this.overallOutputSums  = (double []) overallOutputSums.clone();
    this.clusteringInputFeatureIndex = clusteringInputFeatureIndex;
  }
*/

  public void print(ModelPrintOptions printOptions) throws Exception
  {
    System.out.println("ClusteringLearner Model");
    System.out.println("  clusteringInputFeatureIndex = " + clusteringInputFeatureIndex);
    /*
    for (int o = 0; o < numOutputs; o++)
      {
      System.out.println(outputNames[o] + " = " );
      for (int i = 0; i < numSelectedInputs; i++)
        {
        System.out.println(clusterOutputSums[o][i] + " * " + inputNames[i + (P1_InputFeatureStartNumber - 1)] + " + ");
        }
      System.out.println(clusterOutputSums[o][numSelectedInputs]);
      }
    */
    }


  }
