package ncsa.d2k.modules.core.prediction;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;

public abstract class FunctionInducer extends OrderedReentrantModule implements Cloneable
{
  //int NumBiasParameters = 0;

  double [] BiasParameters;

  //public boolean _Trace     = false;
  //public void    set_Trace (boolean value) {       this._Trace       = value;}
  //public boolean get_Trace ()              {return this._Trace;}

  public String getModuleInfo() {
    return "FunctionInducer - not a functiona module in itself, it is a base class for MeanOutputInducer.";
  }
  
  public String getModuleName()
  {
    return "FunctionInducer";
  }


  public String[] getInputTypes() {
    String[] types = {
      "ncsa.d2k.modules.core.datatype.table.ExampleTable",
      "ncsa.d2k.modules.core.prediction.ErrorFunction"
    };
    return types;
  }

  public String[] getOutputTypes() {
    String[] types = {"ncsa.d2k.modules.core.datatype.model.Model"};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      case 0: return "Example Table";
      case 1: return "Error Function";
      default: return "No such input";
    }
  }

  public String getInputName(int i) {
    switch(i) {
      case 0: return "Example Table";
      case 1: return "Error Function";
      default: return "NO SUCH INPUT!";
    }
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0: return "Model";
      default: return "No such output";
    }
  }

  public String getOutputName(int i) {
    switch(i) {
      case  0: return "Model";
      default: return "NO SUCH OUTPUT!";
    }
  }

  public void instantiateBiasFromProperties() throws Exception {
    System.out.println("override this method");
    throw new Exception();
  }

    /*
  public Model generateModel(ExampleTable examples, double [] bias) throws Exception
    {
    instantiateBias(bias);
    return generateModel(examples);
    }
    */

  public Model generateModel(ExampleTable examples, ErrorFunction errorFunction) throws Exception {
    System.out.println("override this method");
    throw new Exception();
  }

  public void doit() throws Exception {
	
	//	ANCA: added exceptions
	  ExampleTable  exampleSet ;
		  try {
			  exampleSet = (ExampleTable)this.pullInput(0);
		  } catch ( java.lang.ClassCastException e) {
			   throw new Exception("Input/Output attributes not selected.");
		  }
    
		  int[] inputs = exampleSet.getInputFeatures();
		  int [] outputs = exampleSet.getOutputFeatures();
		  for (int i = 0; i < inputs.length; i++)
			  if(!(exampleSet.getColumn(inputs[i])).getIsScalar()) 
				  throw new Exception ("input attributes like " +exampleSet.getColumn(inputs[i]).getLabel() + " must be numeric");
	
		  for (int i = 0; i < outputs.length; i++)
				  if(!(exampleSet.getColumn(outputs[i])).getIsScalar()) 
					  throw new Exception ("output attribute " + i  +" must be numeric");

      ErrorFunction errorFunction   = (ErrorFunction) this.pullInput(1);
    
 
    instantiateBiasFromProperties();

    Model model = null;

    model = generateModel(exampleSet, errorFunction);

    this.pushOutput(model, 0);
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
