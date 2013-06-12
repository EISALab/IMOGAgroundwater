package ncsa.d2k.modules.core.prediction.svm;

import ncsa.d2k.core.modules.*;
import libsvm.*;

/**
  Given a svm_model class, this module will output the model into a
  plain text document that can be loaded later into a SVM.

  @author Xiaolei Li
  */
public class SVMModelWriter extends OutputModule
{
	String model_file_name;
	svm_model model;

	/* empty constructor */
	public SVMModelWriter()
	{
	}

	public String getInputInfo(int index)
	{
		switch (index) {
			case 0:
				return "Input SVM Model.";
			case 1:
				return "Desired file name of output model.";
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
				return "File Name";
			default:
				return "";
		}
	}

	public String[] getInputTypes()
	{
		String[] in = {"libsvm.svm_model", "java.lang.String"};
		return in;
	}

	public String getModuleInfo()
	{
		return "Given a svm_model class, this module will output the " +
			"model into a plain text document that can be loaded later "
			+ "into a SVM.";
	}

	public String getOutputInfo(int index)
	{
		return null;
	}

	public String getOutputName(int index)
	{
		return null;
	}

	public String[] getOutputTypes()
	{
		return null;
	}

	public void beginExecution()
	{
		model_file_name = null;
		model = null;
	}

	public void endExecution()
	{
		super.endExecution();
	}

//	public boolean isReady()
//	{
//		if (getFlags()[0] > 0 || getFlags()[1] > 0)
//			return true;
//		else
//			return false;
//	}


	protected void doit() throws Exception
	{
		try {
			// load the SVM model
			if (getFlags()[0] > 0 && model == null)
				model = (libsvm.svm_model) this.pullInput(0);

			// load the file name
			if (model_file_name == null && getFlags()[1] > 0)
				model_file_name = (String) this.pullInput(1);

			// write the stuff
			if (model != null && model_file_name != null) {
				svm.svm_save_model(model_file_name, model);
				System.out.println("SVM Model written to disk.");
				model = null;
                                model_file_name = null;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			System.out.println("ERROR: SVMModelWriter.doit()");
			throw ex;
		}
	}
}
