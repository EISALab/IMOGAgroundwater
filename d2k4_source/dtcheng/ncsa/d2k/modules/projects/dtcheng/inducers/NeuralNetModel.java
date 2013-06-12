package ncsa.d2k.modules.projects.dtcheng.inducers;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.model.*;


public class NeuralNetModel extends Model implements java.io.Serializable
  {
  BackPropNN backPropNN;

  public NeuralNetModel(ExampleTable examples, BackPropNN backPropNN) {
    super(examples);
    this.backPropNN = backPropNN;
  }


  public double [] Evaluate(ExampleTable examples, int e)
    {
    double [] outputs = backPropNN.predictOutputs(examples, e);
    return outputs;
    }
/*
  public void Instantiate(int numInputs, int numOutputs, String [] inputNames, String [] outputNames,
                          BackPropNN backPropNN)
    {
    this.backPropNN = backPropNN;
    }
*/
  public void print(ModelPrintOptions printOptions) throws Exception {
    System.out.print("Nueral Net Model:");
    }

  }
