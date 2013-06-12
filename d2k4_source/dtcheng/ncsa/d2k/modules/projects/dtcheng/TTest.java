package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.ComputeModule;

public class TTest extends ComputeModule
{

  private boolean    Trace     = false;
  public  void    setTrace     (boolean value) {       this.Trace = value;}
  public  boolean getTrace     ()              {return this.Trace;}

/*
  private boolean    ComputePScores     = false;
  public  void    setComputePScores     (boolean value) {       this.ComputePScores = value;}
  public  boolean getComputePScores     ()              {return this.ComputePScores;}
*/

  private int        NumTails     = 1;
  public  void    setNumTails     (int value)     {       this.NumTails = value;}
  public  int     getNumTails     ()              {return this.NumTails;}

  public String getModuleInfo()
  {
    return "TTest";
  }
  public String getModuleName()
  {
    return "TTest";
  }

  public String[] getInputTypes()
  {
    String[] types = {"[[D"};
    return types;
  }

  public String[] getOutputTypes()
  {
    String[] types = {"[[D","[[D","[D"};
    return types;
  }

  public String getInputInfo(int i)
  {
    switch (i) {
      case 0: return "Double2DArray";
      default: return "No such input";
    }
  }

  public String getInputName(int i)
  {
    switch(i) {
      case 0:
        return "Double2DArray";
      default: return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i)
  {
    switch (i) {
      case 0: return "[[D";
      case 1: return "[[D";
      case 2: return "[D";
      default: return "No such output";
    }
  }

  public String getOutputName(int i)
  {
    switch(i) {
      case 0:
        return "ZScores";
      case 1:
        return "PScores";
      case 2:
        return "Means";
      default: return "NO SUCH OUTPUT!";
    }
  }


  public double logGamma( double xx)
  {
    // An approximation to ln(gamma(x))
    // define some constants...
    int j;
    double stp = 2.506628274650;
    double cof[] = new double[6];
    cof[0]=76.18009173;
    cof[1]=-86.50532033;
    cof[2]=24.01409822;
    cof[3]=-1.231739516;
    cof[4]=0.120858003E-02;
    cof[5]=-0.536382E-05;

    double x = xx - 1;
    double tmp = x + 5.5;
    tmp = (x + 0.5) * Math.log(tmp) - tmp;
    double ser = 1;
    for(j=0; j<6; j++)
    {
      x++;
      ser = ser + cof[j]/x;
    }
    double retVal = tmp + Math.log(stp*ser);
    return retVal;
  }



  public void doit()
  {
    double [][] observations = (double [][]) this.pullInput(0);
    int    numArrays         = observations[0].length;


    int numExamples = observations.length;

    if (Trace)
    {
      System.out.println("numExamples   " + numExamples);
    }


    double [][] zScores      = new double[numArrays][numArrays];
    double [][] pScores      = new double[numArrays][numArrays];
    double []   differences  = new double[numExamples];

    double [] x = new double[numExamples];
    double [] y = new double[numExamples];
    double [] means = new double[numArrays];

    for (int i = 0; i < numArrays; i++)
    {
      zScores[i][i] = 0.0;
      pScores[i][i] = 0.5;
    }

    for (int i = 0; i < numArrays; i++)
    {
      means[i] = 0.0;
      for (int e = 0; e < numExamples; e++)
        means[i] += observations[e][i];
      means[i] /= numExamples;
      if (Trace)
      {

        System.out.println("case #" + (i + 1));
        System.out.println("numExamples   " + numExamples);
        System.out.println("mean  " + means[i]);
      }

    }

    for (int i1 = 0; i1 < numArrays - 1; i1++)
    {
      for (int i2 = i1 + 1; i2 < numArrays; i2++)
      {

        for (int e = 0; e < numExamples; e++)
        {
          x[e] = observations[e][i1];
          y[e] = observations[e][i2];
        }

        /*************************/
        /* calculate differences */
        /*************************/

        for (int i = 0; i < numExamples; i++)
        {
          differences[i] = x[i] - y[i];
        }


        /************************************************/
        /* calculate meanDifference and meanX and meanY */
        /************************************************/
        double sumX = 0.0;
        double sumY = 0.0;
        double sumDifference = 0.0;
        for (int i = 0; i < numExamples; i++)
        {
          sumX += x[i];
          sumY += y[i];
          sumDifference += differences[i];
        }
        double meanX = sumX / numExamples;
        double meanY = sumY / numExamples;
        double meanDifference = sumDifference / numExamples;


        /*****************************/
        /* calculate sample variance */
        /*****************************/

        double sum = 0.0;
        for (int i = 0; i < numExamples; i++)
        {
          double diff = differences[i] - meanDifference;
          sum += diff * diff;
        }
        double sampleVariance = sum / (numExamples - 1);

        double stdOfDifference     = Math.sqrt(sampleVariance);
        double stdOfMeanDifference = Math.sqrt(sampleVariance) / Math.sqrt((double) numExamples);

        /**************************************************************/
        /* calculate standard deviation of meanDifference and Z score */
        /**************************************************************/

        double zScore = meanDifference / stdOfMeanDifference;

        if (meanDifference == 0.0 && stdOfDifference == 0.0 && stdOfMeanDifference == 0.0)
          zScore = 0.0;


        zScores[i1][i2] = zScore;
        zScores[i2][i1] = - zScore;

        if (Trace)
        {



          System.out.println("case #" + (i1 + 1) + " vs case #" + (i2 + 1));
          System.out.println("numExamples   " + numExamples);
          System.out.println("m1  " + meanX);
          System.out.println("m2  " + meanY);
          System.out.println("md  " + meanDifference);
          System.out.println("sd  " + stdOfDifference);
          System.out.println("smd " + stdOfMeanDifference);
          System.out.println("z   " + zScore);
        }

        /****************************************/
        /* calculate false positive probability */
        /****************************************/

        if (zScore < 0)
          zScore = - zScore;
        /*
        if (ComputePScores)
        {
          double r          = numExamples - 1;
          double resolution = 10000000;
          double width      = 1.0 / resolution;

          double sampleZScore = 0.0;
          double constant  = logGamma((r + 1.0) / 2.0) - Math.log(Math.sqrt(Math.PI * r)) - logGamma(r / 2.0);
          double pSum = 0.0;
          while (sampleZScore < zScore)
          {
            double delta = Math.exp(constant -  ((r + 1.0) / 2.0) * Math.log(1.0 + sampleZScore * sampleZScore / r));
            pSum         += delta;
            sampleZScore += width;
          }

          double probabilityFalsePositive = 0.0;
          switch (NumTails)
          {
            case 1:  probabilityFalsePositive =  1.0 - (0.5 + pSum * width)       ; break;
            case 2:  probabilityFalsePositive = (1.0 - (0.5 + pSum * width)) * 2.0; break;
          }

          if (Trace)
          {
            System.out.println("p = " + probabilityFalsePositive);
          }

          pScores[i1][i2] = probabilityFalsePositive;
          pScores[i2][i1] = probabilityFalsePositive;
        }
        */
      }
    }


    this.pushOutput(zScores, 0);
    this.pushOutput(pScores, 1);
    this.pushOutput(means,   2);
  }
}
