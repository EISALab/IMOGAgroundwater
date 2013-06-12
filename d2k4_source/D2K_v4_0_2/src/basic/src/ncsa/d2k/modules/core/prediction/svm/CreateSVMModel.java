package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.modules.PredictionModelModule;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import libsvm.*;

/**
  Produces a SVMModel Predictor Model.

  @author Xiaolei Li
  */

public class CreateSVMModel extends ModelProducerModule
{
	// empty constructor
	public CreateSVMModel()
	{
	}

	public String getInputInfo(int index)
	{
		switch (index) {
			case 0:
				return "The stored SVM Model.";
			case 1:
				return "Input data in an example table.";
			default:
				return "";
		}
	}

	public String getInputName(int index)
	{
		switch (index) {
			case 0:
				return "SVM Model";
			case 1:
				return "Example Table";
			default:
				return "";
		}
	}

	public String[] getInputTypes()
	{
		String[] in = {"libsvm.svm_model", "ncsa.d2k.modules.core.datatype.table.ExampleTable"};
		return in;
	}

	public String getModuleInfo()
	{
		return "Given a native libSVM model, produces a D2K SVM prediction model.";
	}

	public String getOutputInfo(int index)
	{
		switch (index) {
			case 0:
				return "SVM prediction model.";
			default:
				return "";
		}
	}

	public String getOutputName(int index)
	{
		switch (index) {
			case 0:
				return "SVM Predictor";
			default:
				return "";
		}
	}

	public String[] getOutputTypes()
	{
		String[] out = {"ncsa.d2k.modules.core.prediction.svm.SVMModel"};
		return out;
	}

	public void beginExecution()
	{
	}

	public void endExecution()
	{
		super.endExecution();
	}

	protected void doit() throws Exception
	{
		try {
			svm_model model = (libsvm.svm_model) this.pullInput(0);
			ExampleTable et = (ExampleTable) this.pullInput(1);

			SVMModel d2k_model = new SVMModel(model, et);

			this.pushOutput(d2k_model, 0);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			System.out.println("ERROR: CreateSVMModel.doit()");
			throw ex;
		}
	}
}
