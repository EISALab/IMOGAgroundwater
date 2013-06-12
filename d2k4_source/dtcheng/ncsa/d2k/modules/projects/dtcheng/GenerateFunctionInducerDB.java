package ncsa.d2k.modules.projects.dtcheng;


import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.*;
import ncsa.d2k.modules.projects.dtcheng.primitive.Class1DArray;

public class GenerateFunctionInducerDB extends InputModule
  {

  public String getModuleInfo()
    {
		return "GenerateFunctionInducerDB";
	}
  public String getModuleName()
    {
		return "GenerateFunctionInducerDB";
	}

  public String[] getInputTypes()
    {
		String[] types = {		};
		return types;
	}

  public String[] getOutputTypes()
    {
		String[] types = {"ncsa.d2k.modules.projects.dtcheng.Class1DArray"};
		return types;
	}

  public String getInputInfo(int i)
    {
		switch (i) {
			default: return "No such input";
		}
	}

  public String getInputName(int i)
    {
		switch(i) {
			default: return "NO SUCH INPUT!";
		}
	}

  public String getOutputInfo(int i)
    {
		switch (i) {
			case 0: return "Inducer Array";
			default: return "No such output";
		}
	}

  public String getOutputName(int i)
    {
		switch(i) {
			case 0:
				return "Inducer Array";
			default: return "NO SUCH OUTPUT!";
		}
	}

  public void doit() throws Exception
    {
    int numInducers = 4;

    Class [] inducers = new Class[numInducers];

    try{inducers[0] = Class.forName("ncsa.d2k.modules.projects.dtcheng.MeanInducer");}
    catch (Exception e) {System.out.println("could not find class"); throw e;}
    try{inducers[1] = Class.forName("ncsa.d2k.modules.projects.dtcheng.DecisionTreeInducer");}
    catch (Exception e) {System.out.println("could not find class"); throw e;}
    try{inducers[2] = Class.forName("ncsa.d2k.modules.projects.dtcheng.InstanceBasedInducer");}
    catch (Exception e) {System.out.println("could not find class"); throw e;}
    try{inducers[3] = Class.forName("ncsa.d2k.modules.projects.dtcheng.NeuralNetInducer");}
    catch (Exception e) {System.out.println("could not find class"); throw e;}


    Class1DArray inducerArray = new Class1DArray(inducers);

    this.pushOutput(inducerArray, 0);
    }
  }
