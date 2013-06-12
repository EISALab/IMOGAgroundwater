package ncsa.d2k.modules.projects.dtcheng.inducers;
import ncsa.d2k.modules.core.prediction.*;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.*;

public class ClusteringLearnerInducer extends FunctionInducer
  {
  //int NumBiasParameters = 3;

  private int        NumRounds = 20;
  public  void    setNumRounds (int value) {this.NumRounds = value;}
  public  int     getNumRounds () {return this.NumRounds;}

  private int        ClusteringMethodIndex = 20;
  public  void    setClusteringMethodIndex (int value) {this.ClusteringMethodIndex = value;}
  public  int     getClusteringMethodIndex () {return this.ClusteringMethodIndex;}

  private double     TargetNumClusters = 0.0;
  public  void    setTargetNumClusters (double value) {this.TargetNumClusters = value;}
  public  double  getTargetNumClusters () {return this.TargetNumClusters;}

  private int        ClusteringInputFeatureIndex  = 0;
  private int        ClusteringInputFeatureNumber = 1;
  public  void    setClusteringInputFeatureNumber (int value) {this.ClusteringInputFeatureNumber = value;
                                                               this.ClusteringInputFeatureIndex = value - 1;}
  public  int     getClusteringInputFeatureNumber () {return this.ClusteringInputFeatureNumber;}

  public boolean PrintEvolvingModels     = false;
  public void    setPrintEvolvingModels (boolean value) {this.PrintEvolvingModels       = value;}
  public boolean getPrintEvolvingModels ()              {return this.PrintEvolvingModels;}

  public String getModuleInfo()
    {
    return "ClusteringLearnerInducer";
    }
  public String getModuleName()
    {
    return "ClusteringLearnerInducer";
    }

  ModelPrintOptions ModelPrintOptions = new ModelPrintOptions();

  public void instantiateBias(double [] bias)
    {
    ClusteringMethodIndex        = (int) bias[0];
    TargetNumClusters            =       bias[1];
    ClusteringInputFeatureNumber = (int) bias[2];
    ClusteringInputFeatureIndex  = (int) bias[2] - 1;
    NumRounds                    = (int) bias[3];
    }


  public Model generateModel(ExampleTable examples, ErrorFunction errorFunction) throws Exception
    {
    //int numClusters = (int) TargetNumClusters;


    int numExamples = examples.getNumRows();
    int numInputs   = examples.getNumInputFeatures();
    int numOutputs  = examples.getNumOutputFeatures();

    // find the min and max cluster index

    int minUserClusterIndex = Integer.MAX_VALUE;
    int maxUserClusterIndex = Integer.MIN_VALUE;
    for (int e = 0; e < numExamples; e++)
      {
      int userClusterIndex = (int) examples.getInputDouble(e, ClusteringInputFeatureIndex);

      if (userClusterIndex < minUserClusterIndex) minUserClusterIndex = userClusterIndex;
      if (userClusterIndex > maxUserClusterIndex) maxUserClusterIndex = userClusterIndex;
      }

    System.out.println("minUserClusterIndex = " + minUserClusterIndex);
    System.out.println("maxUserClusterIndex = " + maxUserClusterIndex);

    // compute number of clusters assuming all clusters indices in the range will be used
    int maxNumClustersByRange = maxUserClusterIndex - minUserClusterIndex + 1;
    boolean [] clusterExists = new boolean[maxNumClustersByRange];

    int [] rangeToInitialClusterIndex = new int[maxNumClustersByRange];
    for (int i = 0; i < maxNumClustersByRange; i++)
    {
      rangeToInitialClusterIndex[i] = -1;
    }
    int numInitialClusters = 0;

    // find the cluster indices which have examples
    for (int e = 0; e < numExamples; e++)
    {
      int rangeClusterIndex = (int) examples.getInputDouble(e, ClusteringInputFeatureIndex) - minUserClusterIndex;

      if (!clusterExists[rangeClusterIndex])
      {
        clusterExists[rangeClusterIndex] = true;
        rangeToInitialClusterIndex[rangeClusterIndex] = numInitialClusters;
        numInitialClusters++;
      }
    }
    System.out.println("numInitialClusters = " + numInitialClusters);

    // allocate space for cluster output means and populations
    double [][] clusterOutputSums = new double[numInitialClusters][numOutputs];
    int    []   clusterNumExamples = new int[numInitialClusters];
    int    [][] clusterInitialIndices = new int[numInitialClusters][1];

    // allocate space for overal output mean and population
    double [] overallOutputSums = new double[numOutputs];
    int       overallNumExamples = 0;



    for (int i = 0; i < numInitialClusters; i++)
    {
      clusterInitialIndices[i][0] = i;
    }
    // sum up cluster output vectors and overall output vector
    for (int e = 0; e < numExamples; e++)
      {
      int rangeClusterIndex = (int) examples.getInputDouble(e, ClusteringInputFeatureIndex) - minUserClusterIndex;
      int initialClusterIndex = rangeToInitialClusterIndex[rangeClusterIndex];

      for (int o = 0; o < numOutputs; o++)
        {
        double value =  examples.getOutputDouble(e, o);
        clusterOutputSums[initialClusterIndex][o] += value;
        overallOutputSums[o] += value;
        }
      clusterNumExamples[initialClusterIndex]++;
      overallNumExamples++;
      }


      int numClusters = numInitialClusters;


      for (int roundIndex = 0; roundIndex < NumRounds; roundIndex++)
      {


        double closestDistance = Double.MAX_VALUE;
        int closestI1 = -1;
        int closestI2 = -1;

        for (int i1 = 0; i1 < numClusters; i1++)
        {
          for (int i2 = i1 + 1; i2 < numClusters; i2++)
          {
            double varianceSum = 0;
            for (int o = 0; o < numOutputs; o++)
            {
              double value1 =  clusterOutputSums[i1][o] / clusterNumExamples[i1];
              double value2 =  clusterOutputSums[i2][o] / clusterNumExamples[i2];
              double difference = (value1 - value2);
              varianceSum += difference * difference;
            }
            double distance = Math.sqrt(varianceSum);
            if (distance < closestDistance)
            {
              closestDistance = distance;
              closestI1 = i1;
              closestI2 = i2;
            }
          }
        }


        //System.out.println("closestDistance = " + closestDistance);
        //System.out.println("closestI1 = " + closestI1);
        //System.out.println("closestI2 = " + closestI2);

        // merge closest clusters
        //// merge closestCluster2 into closestCluster1
        for (int o = 0; o < numOutputs; o++)
        {
          clusterOutputSums[closestI1][o] += clusterOutputSums[closestI2][o];
        }
        clusterNumExamples[closestI1] += clusterNumExamples[closestI2];

        int numInitialClusters1 = clusterInitialIndices[closestI1].length;
        int numInitialClusters2 = clusterInitialIndices[closestI2].length;
        int [] newInitialIndices = new int[numInitialClusters1 + numInitialClusters2];

        for (int i = 0; i < numInitialClusters1; i++)
        {
          newInitialIndices[i] = clusterInitialIndices[closestI1][i];
        }
        for (int i = 0; i < numInitialClusters2; i++)
        {
          newInitialIndices[numInitialClusters1 + i] = clusterInitialIndices[closestI2][i];
        }

        clusterInitialIndices[closestI1] = newInitialIndices;

        clusterOutputSums[closestI2] = clusterOutputSums[numClusters - 1];
        clusterNumExamples[closestI2] = clusterNumExamples[numClusters - 1];
        clusterInitialIndices[closestI2] = clusterInitialIndices[numClusters - 1];

        numClusters--;

      }

      if (false)
      {
        for (int i = 0; i < numClusters; i++)
        {
          System.out.println("cluster #" + (i + 1));

          for (int o = 0; o < numOutputs; o++)
          {
            double value = clusterOutputSums[i][o] / clusterNumExamples[i];
            System.out.println("o" + (o + 1) + " = " + value);
          }
          System.out.println("numExamples = " + clusterNumExamples[i]);

          int clusterNumInitialIndices = clusterInitialIndices[i].length;
          System.out.println("clusterNumInitialIndices = " + clusterNumInitialIndices);

          for (int j = 0; j < clusterNumInitialIndices; j++)
          {
            System.out.println("initialClusterIndex" + (j + 1) + " = " + clusterInitialIndices[i][j]);
          }

        }
      }

      int [] initialToFinalClusterIndex = new int[numInitialClusters];

      for (int i = 0; i < numClusters; i++)
      {
        int clusterNumInitialIndices = clusterInitialIndices[i].length;
        //System.out.println("clusterNumInitialIndices = " + clusterNumInitialIndices);

        for (int j = 0; j < clusterNumInitialIndices; j++)
        {
          int clusterIndex = clusterInitialIndices[i][j];
          initialToFinalClusterIndex[clusterIndex] = i;
        }

      }

      int [] rangeToFinalClusterIndex = new int[maxNumClustersByRange];
      for (int i = 0; i < maxNumClustersByRange; i++)
      {
        int clusterIndex = rangeToInitialClusterIndex[i];
        if (clusterIndex != -1)
        {
          rangeToFinalClusterIndex[i] = initialToFinalClusterIndex[rangeToInitialClusterIndex[i]];
        }
        else
        {
          rangeToFinalClusterIndex[i] = -1;
        }
      }
      int [] rangeToClusterIndex = new int[maxNumClustersByRange];


      ClusteringLearnerModel model = new ClusteringLearnerModel(examples,
          numClusters, minUserClusterIndex,
          maxNumClustersByRange, rangeToFinalClusterIndex,
          clusterNumExamples, clusterOutputSums,
          overallNumExamples, overallOutputSums,
          ClusteringInputFeatureIndex);


    return (Model) model;
    }
  }
